package com.eelink.tcp.core;

import com.eelink.tcp.App;
import com.eelink.tcp.utils.Logger;
import com.eelink.tcp.utils.StringHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class EELinkServerHandler extends ChannelInboundHandlerAdapter {

    private Device device;
    private byte[] dataFragment;

    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
//      super.exceptionCaught( ctx, cause );
        ctx.close();
    }

    @Override
    public void userEventTriggered( ChannelHandlerContext ctx, Object evt ) throws Exception {
//      super.userEventTriggered( ctx, evt );
        if ( evt instanceof IdleStateEvent )
        {
            IdleStateEvent event = ( IdleStateEvent ) evt;
            if ( event.state() == IdleState.READER_IDLE )
                ctx.channel().close();
            else
                super.userEventTriggered( ctx, evt );
        }
    }

    @Override
    public void handlerAdded( ChannelHandlerContext ctx ) throws Exception {
        device = new Device( ctx.channel() );
        App.offerDevice( device );
        super.handlerAdded( ctx );
    }

    @Override
    public void handlerRemoved( ChannelHandlerContext ctx ) throws Exception {
        App.deleteDevice( device );
        super.handlerRemoved( ctx );
    }

    @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
//      super.channelRead( ctx, msg );
        byte[] bytes = ( byte[] ) msg;
        InetSocketAddress inSocket = ( InetSocketAddress ) ctx.channel().remoteAddress();

        Logger.INFO( String.format( "[%s]r: %d:[%s:%d] %s"  , device.getId() == null ? "unknown" : device.getId()
                                                            , bytes.length
                                                            , inSocket.getAddress().getHostAddress()
                                                            , inSocket.getPort()
                                                            , StringHelper.TO_STRING( bytes ) ) );

        if ( dataFragment != null )
        {
            byte[] tmpData = Arrays.copyOf( dataFragment, dataFragment.length + bytes.length );
            System.arraycopy( bytes, 0, tmpData, dataFragment.length, bytes.length );
            bytes = tmpData;
            dataFragment = null;
        }

        int index = 0;
        int remain = bytes.length;
        boolean quit = false;
        while ( ( remain >= 7 ) && ! quit )
        {
            if ( 0x67 == bytes[index] && 0x67 == bytes[index + 1] )
            {
                int packetLen = ( ( bytes[index + 3] & 0xff ) << 8 ) + ( bytes[index + 4] & 0xff ) + 5;
                quit = remain < packetLen;
                if ( ! quit )
                {
                    // ignored the result
                    device.offerRx( Arrays.copyOfRange( bytes, index, index + packetLen ) );
                    index += packetLen;
                    remain -= packetLen;
                }
            }
            else
            {
                index ++;
                remain --;
            }

            if ( index < bytes.length - 1 )
                dataFragment = Arrays.copyOfRange( bytes, index, bytes.length );
        }
    }
}
