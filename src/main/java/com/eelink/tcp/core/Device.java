package com.eelink.tcp.core;

import com.eelink.tcp.model.*;
import com.eelink.tcp.utils.CONST;
import com.eelink.tcp.utils.Logger;
import com.eelink.tcp.utils.StringHelper;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Device {

    private String m_id;

    private Channel channel;

    private boolean m_authorized;

    private GPSInfo m_lastGPSInfo;

    private Queue<byte[]> m_rxQueue;
    private Queue<byte[]> m_txQueue;

    private QueueCenter m_txCenter;

    private long m_counter;

    public Device( Channel channel )
    {
        reset();
        this.channel = channel;
    }

    private void reset()
    {
        m_authorized = false;

        m_rxQueue = new LinkedBlockingQueue<>();
        m_txQueue = new LinkedBlockingQueue<>();

        m_txCenter = new QueueCenter();

        m_counter = 0;

        m_lastGPSInfo = new GPSInfo();
        m_lastGPSInfo.setDatetime(new Date( CONST.DT20160101000000));
    }

    public String getId( ) {
        return m_id;
    }

    public void setId( String m_id ) {
        this.m_id = m_id;
    }

    public boolean offerRx( byte[] data )
    {
        return m_rxQueue.offer( data );
    }

    private void doSend( byte[] data )
    {
        InetSocketAddress inSocket = (InetSocketAddress) channel.remoteAddress();
        Logger.INFO( String.format( "[%s]s: %d:[%s:%d] %s"  , getId() == null ? "" : getId()
                                                            , data.length
                                                            , inSocket.getAddress().getHostAddress()
                                                            , inSocket.getPort()
                                                            , StringHelper.TO_STRING( data ) ) );
        try
        {
            if ( channel.isActive() && channel.isWritable() )
                channel.writeAndFlush( Unpooled.copiedBuffer( data ) ).sync();
        } catch ( InterruptedException e ) {
            Logger.ERR( e );
        }
    }

    public void work()
    {
        m_counter ++;

        doRecvHandler();

        if ( ( m_counter & 1 ) == 0 )
        {
            if (m_txCenter.getSize() > 0 && m_txCenter.getSize() < CONST.LINK_DATA_LENGTH)
            {
                m_txQueue.offer( Arrays.copyOfRange( m_txCenter.getBuffer(), m_txCenter.getStart(), m_txCenter.getStart() + m_txCenter.getSize() ) );
                m_txCenter.setSize(0);
                m_txCenter.setStart(0);
            }
        }

        byte[] head = m_txQueue.poll();
        if ( head != null ) doSend( head );
    }

    private void doRecvHandler()
    {
        byte[] head;
        int num = 0;
        int gpsNum = 0;
        HandleContext context;

        StringBuilder values = new StringBuilder();
        while ( m_rxQueue.size() > 0 && num < CONST.RECV_HANDLE_MAX_NUM )
        {
            head = m_rxQueue.poll();
            if ( head != null )
            {
                context = new HandleContext();
                doRecvHandlerCore( head, context );
                if ( CONST.DataType.pkt_gps == context.getPkType() && m_authorized )
                {
                    HandleGPSInfo( context.getGpsInfo() );
                    if ( 'X' != context.getGpsInfo().getState() )
                    {
                        values.append( String.format( "%s%s", 0 == gpsNum ? "" : ",", /* gpsInfo2Sql */ context.getGpsInfo().toString() ) );
                        gpsNum ++;
                    }
                }
            }

            num ++;
        }

        if ( m_authorized )
        {
            if ( gpsNum != 0 && m_id != null && ( ! CONST.BUSINESS_EDITION /* && ! m_expire */ ) ) {
                String sql = String.format( "insert into `xxx` values %s", values );

                // offer SQL
                ;

                Logger.FORMAT( "SQL: %s", sql );
            }
        }
    }

    private void HandleGPSInfo( GPSInfo gpsInfo )
    {
        if ( ! m_authorized || m_id == null || 'V' == gpsInfo.getState() )
        {
            gpsInfo.setState( 'X' );
            return;
        }

        if ( ( gpsInfo.getDatetime().before( m_lastGPSInfo.getDatetime() )	|| gpsInfo.getDatetime().equals( m_lastGPSInfo.getDatetime() ) ) || gpsInfo.getDatetime().after( new Date( System.currentTimeMillis() + 1200 * 1000 ) ) )
        {
            gpsInfo.setState( 'X' );
            return;
        }

        if ( ! isGPSLocate( gpsInfo.getState() ) && ( 0 == gpsInfo.getLac()[0] || 0 == gpsInfo.getCi()[0] ) )
        {
            int n = 0;
            for ( int i = 0; i < CONST.MAX_WIFI_NUM; i ++ )
            {
                if ( Long.parseLong( gpsInfo.getBssid( i ), 16 ) != 0 ) n ++;
                if ( n < 2 )
                {
                    gpsInfo.setState( 'X' );
                    return;
                }
            }
        }

        if ( isGPSLocate( gpsInfo.getState() ) )
        {
            double lng = Math.abs( gpsInfo.getLatlng().getLongitude() );
            double lat = Math.abs( gpsInfo.getLatlng().getLatitude() );
            if ( lng == 0.0 || lat == 0.0 || lng > 180.0 || lat > 90.0 )
            {
                gpsInfo.setState( 'X' );
                return;
            }

            if ( isGPSLocate( m_lastGPSInfo.getState() ) && ! m_lastGPSInfo.getLatlng().isZero() )
            {
                double distance = mx_distance( gpsInfo.getLatlng().getLongitude(), gpsInfo.getLatlng().getLatitude(), m_lastGPSInfo.getLatlng().getLongitude(), m_lastGPSInfo.getLatlng().getLatitude() );
                int seconds = ( int ) ( gpsInfo.getDatetime().getTime() - m_lastGPSInfo.getDatetime().getTime() );
                if ( seconds > 5 && distance > 100 * seconds )
                {
                    gpsInfo.setState( 'B' );
                }
            }
        }

        m_lastGPSInfo = gpsInfo;
    }

    private void doRecvHandlerCore( byte[] data, HandleContext context )
    {
        context.setCmd( data[2] & 0xff );
        context.setFlag( CONST.RESPONSE );
        context.setPkType( CONST.DataType.pkt_ukn );

        EErx rx = new EErx( data, 7, data.length - 7 );
        context.getResponse().writeData( data, 0, 7 );

        switch ( context.getCmd() )
        {
            case 0x01:  // Login Package
                HandleLoginPkt( context, rx );
                break;

            case 0x03:  // Heartbeat Package
                HandleBeatPkt( context, rx );
                break;

            case 0x12:  // Locate Package
                HandleGPSPkt( context, rx );
                break;

            case 0x14:  // Warning Package
                HandleWarnPkt( context, rx );
                break;

            case 0x1b:  // Param-Set Package

                break;

            default:
                break;
        }

        if ( ( context.getFlag() & CONST.RESPONSE ) > 0 )
        {
            context.getResponse().finalize();
            injectOutData( context.getResponse().getData(), context.getResponse().getSize() );
        }
    }

    private void injectOutData( byte[] data, int length )
    {
        int dataIndex = 0;
        while ( length != 0 )
        {
            int num = Math.min( length, CONST.MX_BUFFER_SIZE - m_txCenter.getSize() );

            System.arraycopy( data, dataIndex, m_txCenter.getBuffer(), m_txCenter.getSize(), num );
            m_txCenter.setSize( m_txCenter.getSize() + num );
            length -= num;
            dataIndex += num;

            if (m_txCenter.getSize() >= CONST.LINK_DATA_LENGTH) {
                while (m_txCenter.getSize() >= CONST.LINK_DATA_LENGTH) {
                    m_txQueue.offer( Arrays.copyOfRange(m_txCenter.getBuffer(), m_txCenter.getStart(), m_txCenter.getStart() + CONST.LINK_DATA_LENGTH ) );
                    m_txCenter.setStart( m_txCenter.getStart() + CONST.LINK_DATA_LENGTH );
                    m_txCenter.setSize( m_txCenter.getSize() - CONST.LINK_DATA_LENGTH );
                }

                if (m_txCenter.getSize() > 0) {
                    System.arraycopy( m_txCenter.getBuffer(), m_txCenter.getStart(), m_txCenter.getBuffer(), 0, m_txCenter.getSize() );
                    // m_txCenter.setSize( 0 );
                    m_txCenter.setStart( 0 );
                }
            }
        }
    }

    private void HandleLoginPkt( HandleContext context, EErx rx )
    {
        context.getResponse().writeLong( System.currentTimeMillis() / 1000, 4 );
        setId( rx.parseIMEI() );
        // Authentication
        // if ( ... ) m_authorized = true;
        m_authorized = true;

        int lang = rx.parseInt( 1 ).intValue() == 0 ? CONST.MX_LANG_ZHCN : CONST.MX_LANG_ENGLISH;
        int tz = 0;
        if ( rx.getSize() > 0 )
            tz = rx.parseInt( 1 ).intValue();
        int sysVer = 0, appVer = 0, psVer = 0, psOSIze = 0, psCSize = 0, psSum16 = 0;
        if ( rx.getSize() > 0 )
        {
            boolean now = true; // Upload The Param-Set Immediately
            sysVer  = rx.parseUInt( 2 ).intValue();
            appVer  = rx.parseUInt( 2 ).intValue();
            psVer   = rx.parseUInt( 2 ).intValue();
            psOSIze = rx.parseUInt( 2 ).intValue();
            psCSize = rx.parseUInt( 2 ).intValue();
            psSum16 = rx.parseUInt( 2 ).intValue();

            // now =

            context.getResponse().writeInt( 1, 2 );
            context.getResponse().writeInt( now ? 3 : 2, 1 );
        }

        Logger.FORMAT( "lang=%d; tz=%d; sysVer=%d; appVer=%d; psVer=%d, psOSIze=%d; psCSize=%d; psSum16=%d", lang, tz, sysVer, appVer, psVer, psOSIze, psCSize, psSum16 );

        context.setPkType( CONST.DataType.pkt_log );
    }

    private void HandleBeatPkt( HandleContext context, EErx rx )
    {
        context.setPkType(CONST.DataType.pkt_beat);
    }

    private void HandleGPSPkt( HandleContext context, EErx rx )
    {
        GetGpsInfo_Common( context.getGpsInfo(), rx, context.getCmd() );
        context.setFlag( context.getFlag() & ~( CONST.RESPONSE ) );
        context.setPkType( CONST.DataType.pkt_gps );
    }

    private void GetGpsInfo_Common( GPSInfo gpsInfo, EErx rx, int cmd )
    {
        EEtx tx = new EEtx();

        gpsInfo.setDatetime( new Date( rx.parseUInt( 4 ).longValue() * 1000) );
        int mask = rx.parseInt( 1 ).intValue();

        tx.writeInt(mask, 2);

        if ( ( mask & CONST.MASK_GPS ) != 0 ) {
            gpsInfo.getLatlng().setLatitude( rx.parseInt( 4 ).longValue() / CONST.DX );
            gpsInfo.getLatlng().setLongitude( rx.parseInt( 4 ).longValue() / CONST.DX );
            gpsInfo.setAltitude( rx.parseInt( 2 ).shortValue() );
            gpsInfo.setSpeed( rx.parseUInt( 2 ).intValue() * 10 );
            gpsInfo.setDegree( rx.parseUInt( 2 ).intValue() * 100 );
            gpsInfo.setSatellites( rx.parseUInt( 1 ).intValue() );
            tx.writeInt( gpsInfo.getAltitude(), 2 );
            tx.writeInt( gpsInfo.getSatellites(), 1 );
        }

        if ((mask & CONST.MASK_BSID0) != 0)
        {
            gpsInfo.setMcc( rx.parseUInt( 2 ).intValue() );
            gpsInfo.setMnc( rx.parseUInt( 2 ).intValue() );
        }
        final int[] _bsid_mask = { CONST.MASK_BSID0, CONST.MASK_BSID1, CONST.MASK_BSID2 };
        for ( int i = 0; i < CONST.MAX_BS_NUM; i++ )
        {
            if ( ( mask & _bsid_mask[i] ) != 0 )
            {
                // LAC 2 CI 4 RxLev 1
                tx.writeData( rx.getData(), rx.getIndex(), CONST.BSINFOSIZE );
                gpsInfo.setLac( i, rx.parseUInt( 2 ).intValue() );
                gpsInfo.setCi( i, rx.parseUInt( 4 ).longValue() );
                gpsInfo.setRxlev( i, rx.parseUInt( 1 ).intValue() );
            }
        }

        final int[] _bss_mask = { CONST.MASK_BSS0, CONST.MASK_BSS1, CONST.MASK_BSS2 };
        for ( int i = 0; i < CONST.MAX_WIFI_NUM; i++ )
        {
            if ( ( mask & _bss_mask[i] ) != 0 )
            {
                // BSSID 6 RSSI 1
                tx.writeData( rx.getData(), rx.getIndex(), 7 );
                gpsInfo.setBssid( i, Arrays.copyOfRange( rx.getData(), rx.getIndex(), rx.getIndex() + 6 ) );
                rx.skip( 6 );
                gpsInfo.setRssi( i, rx.parseInt( 1 ).intValue() );
            }
        }

        if ( 0x12 == cmd )
        { // 应该判命令
            // Status 2 Battery 2 AIN0 2 AIN1 2
            tx.writeData( rx.getData(), rx.getIndex(), 8 );

            gpsInfo.setStatus( rx.parseUInt( 2 ).intValue() );
            gpsInfo.setBattery( rx.parseUInt( 2 ).intValue() );
            gpsInfo.setAcc((gpsInfo.getStatus() >> 2) & 1);

            gpsInfo.setAin0( rx.parseUInt( 2 ).intValue() );
            gpsInfo.setAin1( rx.parseUInt( 2 ).intValue() );
            long dummy = rx.parseUInt( 4 ).longValue();

            int left = rx.getSize();
            if ( left >= 20 )
            { // 新增加的数据20个字节
                gpsInfo.setOffset( tx.getSize() );
                tx.writeData( rx.getData(), rx.getIndex(), left );
                rx.skip( 8 );
                gpsInfo.setExt1( rx.parseInt( 2 ).intValue() );
                if ( left >= 22 )
                {
                    rx.skip( 10 ); // 之前已用10个
                    gpsInfo.setExt2( rx.parseInt( 2 ).intValue() );
                }
            }

            gpsInfo.setExtlen( tx.getSize() );
            if ( gpsInfo.getExtlen() > 0 )
            {
                gpsInfo.setExtlen( Math.min( gpsInfo.getExtlen(), CONST.GPS_EXTEND_LEN ) );
                // GPS WiFi BS
                gpsInfo.setExtend( Arrays.copyOfRange( tx.getData(), 0, gpsInfo.getExtlen() ) );
            }
        }
        gpsInfo.setState( "VGLGWGWG".charAt( ( mask & CONST.MASK_GPS ) | ( mask & CONST.MASK_BSID0 ) | ( ( mask & CONST.MASK_BSS0 ) >> 2 ) ) );
        gpsInfo.setExtlen( Math.max( 1, gpsInfo.getExtlen() ) );
    }

    private void HandleWarnPkt( HandleContext context, EErx rx )
    {
        context.setPkType( CONST.DataType.pkt_warn );
        if ( m_authorized )
        {
            GetGpsInfo_Common( context.getGpsInfo(), rx, context.getCmd() );
            long currentTime = System.currentTimeMillis();
            long datetime = context.getGpsInfo().getDatetime().getTime();
            if ( ( datetime > currentTime - 86400L * 90 * 1000 ) && ( datetime < currentTime + 1200 * 1000 ) && ( isGPSLocate( context.getGpsInfo().getState() ) || ( context.getGpsInfo().getLac()[0] != 0 && context.getGpsInfo().getCi()[0] != 0 ) ) )
            {
                int warnType = GetWarnType( rx.parseUInt( 1 ).intValue() );
                int strId = CONST.StrWarn.STR_WARN_SOS.ordinal() + warnType;
                context.getResponse().sprintf( "%s", GetGlobalStr( strId, GetLang() ) );
                QueryLocation( context.getResponse(), context.getGpsInfo() );
//              InsertWarnTable
                ;

                Logger.FORMAT( "Warn: %s", new String( Arrays.copyOfRange( context.getResponse().getData(), 7, context.getResponse().getData().length ) ).trim() );
            }
        }
    }

    private boolean isGPSLocate(char state) {
        return 'A' == state || 'G' == state;
    }

    private int GetWarnType(int warn) {
        final int[] __type = { 0x02, 0x14, 0x01, 0x03, 0x08, 0x09, 0x85, 0x86, 0x81, 0x82, 0x83, 0x84, 0x05, 0x04, 0x20,
                0x21, 0x22, 0x23, 0x24, 0x25 };

        int index = 0;
        int count = __type.length;
        while ((index < count) && warn != __type[index])
            index++;

        return CONST.Warn.MX_CUST_WARN_MOTION.ordinal() == index ? CONST.Warn.MX_CUST_WARN_VIB.ordinal() : index;
    }

    private int GetLang() {
        return CONST.MX_LANG_ENGLISH;
    }

    private String GetGlobalStr( int index, int lang )
    {
        return CONST.__globalStr[index][lang];
    }

    private void QueryLocation( EEtx location, GPSInfo gpsInfo )
    {
        if ( ! m_authorized ) return;

        Coordinate wgs = gpsInfo.getLatlng();
        if ( ! isGPSLocate( gpsInfo.getState() ) )
        {
            // Locate By LBS or WIFI
            // wgs =
            ;
        }

        location.sprintf( "http://maps.google.com/?q=%.6f,%.6f", wgs.getLatitude(), wgs.getLongitude() );

    }

    private double toRadians(double x) {
        // TODO Auto-generated method stub
        return x * CONST.M_PI / 180.0;
    }

    private double mx_distance(double lng1, double lat1, double lng2, double lat2) {
        if (Math.abs(lng1 - lng2) <= 1e-7 && Math.abs(lat1 - lat2) < 1e-7) {
            return 0;
        } else if (Math.abs(lng1 + lng2) < 1e-7 && Math.abs(lat1 + lat2) <= 1e-7) {
            return CONST.M_PI * CONST.EARTH_RADIUS;
        } else {
            if (Math.abs(lng1 - lng2) > 2.0 || Math.abs(lat1 - lat2) > 2.0) { // 粗算距离超出200km采用精确算法
                return mx_spheric_angle(lng1, lat1, lng2, lat2);
            } else {
                double dx = lng1 - lng2; // 经度差值
                double dy = lat1 - lat2; // 纬度差值
                double b = (lat1 + lat2) / 2.0; // 平均纬度
                double Lx = toRadians(dx) * Math.cos(toRadians(b)); // 东西距离
                double Ly = toRadians(dy); // 南北距离
                return CONST.EARTH_RADIUS * Math.sqrt(Lx * Lx + Ly * Ly); // 用平面的矩形对角距离公式计算总距离
            }
        }
    }

    private double mx_spheric_angle(double lng1, double lat1, double lng2, double lat2) {
        lng1 *= CONST.M_PI / 180.0;
        lat1 *= CONST.M_PI / 180.0;
        lng2 *= CONST.M_PI / 180.0;
        lat2 *= CONST.M_PI / 180.0;
        return (Math.acos(Math.cos(lat1) * Math.cos(lat2) * Math.cos(lng1 - lng2) + Math.sin(lat1) * Math.sin(lat2)));
    }
}
