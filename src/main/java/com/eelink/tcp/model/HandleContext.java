package com.eelink.tcp.model;

import com.eelink.tcp.utils.CONST;

public class HandleContext {

    private int cmd;
    private GPSInfo gpsInfo;
    private EEtx response;
    private int flag;
    private CONST.DataType pkType;

    public HandleContext()
    {
        response = new EEtx();
        gpsInfo = new GPSInfo();
    }

    public int getCmd( ) {
        return cmd;
    }

    public void setCmd( int cmd ) {
        this.cmd = cmd;
    }

    public GPSInfo getGpsInfo( ) {
        return gpsInfo;
    }

    public void setGpsInfo( GPSInfo gpsInfo ) {
        this.gpsInfo = gpsInfo;
    }

    public EEtx getResponse( ) {
        return response;
    }

    public void setResponse( EEtx response ) {
        this.response = response;
    }

    public int getFlag( ) {
        return flag;
    }

    public void setFlag( int flag ) {
        this.flag = flag;
    }

    public CONST.DataType getPkType( ) {
        return pkType;
    }

    public void setPkType( CONST.DataType pkType ) {
        this.pkType = pkType;
    }
}
