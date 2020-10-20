package com.eelink.tcp.utils;

import static java.lang.String.format;

public class Logger {
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( Logger.class );
	
	public static void INFO( String info ) {
		LOGGER.info( info );
	}

	public static void FORMAT( String format, Object ... args )
	{
		INFO( format( format, args ) );
	}
	
	public static void ERR( String err ) {
		LOGGER.error( err );
	}
	
	public static void ERR( Exception e ) {
		if ( e != null ) LOGGER.error( "Catch An Exception" ,  e );
	}
}
