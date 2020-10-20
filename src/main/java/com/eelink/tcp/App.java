package com.eelink.tcp;

import com.eelink.tcp.task.DeviceWorker;
import com.eelink.tcp.core.Device;
import com.eelink.tcp.core.ByteDecoder;
import com.eelink.tcp.core.EELinkServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class App {

    private static final int PORT = Integer.parseInt( System.getProperty( "port", "32002" ) );

    public static final List<Device> deviceArr      = new ArrayList<>();
    private static final Queue<Device> deviceAddTmp = new LinkedBlockingQueue<>();
    private static final Queue<Device> deviceDelTmp = new LinkedBlockingQueue<>();

    public static void main( String ... args ) throws Exception {
        TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) );

        new HashedWheelTimer().newTimeout( new DeviceWorker(), 2, TimeUnit.SECONDS );

        EventLoopGroup bossGroup = new NioEventLoopGroup( 1 );
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b
                    .group( bossGroup, workerGroup )
                    .channel( NioServerSocketChannel.class )
                    .handler( new LoggingHandler( LogLevel.INFO ) )
                    .childHandler( new ChannelInitializer<SocketChannel>() {
                        protected void initChannel( SocketChannel ch ) throws Exception {
                            ch.pipeline().addLast( "TimeOut", new IdleStateHandler( 10 * 60, 0, 0 ) );
                            ch.pipeline().addLast( "Decoder", new ByteDecoder() );
                            ch.pipeline().addLast( new EELinkServerHandler() );
                        }
                    } );

            // Bind and start to accept incoming connections.
            b.bind( PORT ).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static boolean offerDevice( Device device )
    {
        return deviceAddTmp.offer( device );
    }

    public static boolean deleteDevice( Device device )
    {
        return deviceDelTmp.offer( device );
    }

    public static void mergeDevice()
    {
        Device device = deviceAddTmp.poll();
        while ( device != null )
        {
            deviceArr.add( device );
            device = deviceAddTmp.poll();
        }

        device = deviceDelTmp.poll();
        while ( device != null )
        {
            deviceArr.remove( device );
            device = deviceDelTmp.poll();
        }
    }

}
