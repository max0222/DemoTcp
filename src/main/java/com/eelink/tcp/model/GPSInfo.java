package com.eelink.tcp.model;

import com.eelink.tcp.utils.CONST;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * _track
 * 
 * @author zhu 2019-08-14
 */
public class GPSInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * datetime
	 */
	private Date datetime;

	private Coordinate latlng;

	/**
	 * speed
	 */
	private int speed;

	/**
	 * degree
	 */
	private int degree;

	/**
	 * mileage
	 */
	private long mileage;

	/**
	 * status
	 */
	private int status;

	private int altitude;

	/**
	 * mcc
	 */
	private int mcc;

	/**
	 * mnc
	 */
	private int mnc;

	private int[] lac = new int[CONST.MAX_BS_NUM];

	private long[] ci = new long[CONST.MAX_BS_NUM];

	private int[] rxlev = new int[CONST.MAX_BS_NUM];

	private byte[][] bssid = new byte[CONST.MAX_WIFI_NUM][6];

	private int[] rssi = new int[CONST.MAX_WIFI_NUM];

	private int satellites;

	private int battery;

	private int ain0;

	private int ain1;

	private int ext1;

	private int ext2;

	/**
	 * extend
	 */
	private byte[] extend;

	private int extlen;

	private int offset;

	private int acc;

	/**
	 * state
	 */
	private char state;

	public GPSInfo() {
		latlng = new Coordinate();
		datetime = new Date(CONST.DT20160101000000);
	}

	public void setLac(int index, int lac) {
		if (this.lac.length > index)
			this.lac[index] = lac;
	}

	public void setCi(int index, long ci) {
		if (this.ci.length > index)
			this.ci[index] = ci;
	}

	public void setRxlev(int index, int rxlev) {
		if (this.rxlev.length > index)
			this.rxlev[index] = rxlev;
	}

	public void setBssid(int index, byte[] bssid) {
		if (this.bssid.length > index && bssid.length == 6)
			this.bssid[index] = bssid;
	}

	public String getBssid(int index) {
		byte[] bytes = bssid[index];
		StringBuilder buf = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			buf.append(String.format("%02x", b & 0xff ));
		}
		return buf.toString();
	}

	public void setRssi(int index, int rssi) {
		if (this.rssi.length > index)
			this.rssi[index] = rssi;
	}

	public void updateMileage(int index) {
		int[] _ci_shift = { 16, 8, 0 };
		int t = (int) ((this.ci[index] >> 16) & 0xFF);
		if (t != 0) {
			this.mileage |= CONST.BS_3G_FLAG | (t << _ci_shift[index]);
		}
	}

	public void copyProperties( GPSInfo gpsInfo )
	{
		latlng 		= gpsInfo.latlng;
		speed		= gpsInfo.speed;
		degree		= gpsInfo.degree;
		altitude 	= gpsInfo.altitude;
		mcc			= gpsInfo.mcc;
		mnc			= gpsInfo.mnc;
		lac			= gpsInfo.lac;
		ci			= gpsInfo.ci;
		rxlev		= gpsInfo.rxlev;
		bssid		= gpsInfo.bssid;
		rssi		= gpsInfo.rssi;
		satellites	= gpsInfo.satellites;
		state		= gpsInfo.state;
	}

	public boolean isTimeout()
	{
		long diffMS = System.currentTimeMillis() - this.datetime.getTime();
		return diffMS > TimeUnit.DAYS.toMillis( 90 );
	}

	public static long getSerialVersionUID( ) {
		return serialVersionUID;
	}

	public Date getDatetime( ) {
		return datetime;
	}

	public void setDatetime( Date datetime ) {
		this.datetime = datetime;
	}

	public Coordinate getLatlng( ) {
		return latlng;
	}

	public void setLatlng( Coordinate latlng ) {
		this.latlng = latlng;
	}

	public int getSpeed( ) {
		return speed;
	}

	public void setSpeed( int speed ) {
		this.speed = speed;
	}

	public int getDegree( ) {
		return degree;
	}

	public void setDegree( int degree ) {
		this.degree = degree;
	}

	public long getMileage( ) {
		return mileage;
	}

	public void setMileage( long mileage ) {
		this.mileage = mileage;
	}

	public int getStatus( ) {
		return status;
	}

	public void setStatus( int status ) {
		this.status = status;
	}

	public int getAltitude( ) {
		return altitude;
	}

	public void setAltitude( int altitude ) {
		this.altitude = altitude;
	}

	public int getMcc( ) {
		return mcc;
	}

	public void setMcc( int mcc ) {
		this.mcc = mcc;
	}

	public int getMnc( ) {
		return mnc;
	}

	public void setMnc( int mnc ) {
		this.mnc = mnc;
	}

	public int[] getLac( ) {
		return lac;
	}

	public void setLac( int[] lac ) {
		this.lac = lac;
	}

	public long[] getCi( ) {
		return ci;
	}

	public void setCi( long[] ci ) {
		this.ci = ci;
	}

	public int[] getRxlev( ) {
		return rxlev;
	}

	public void setRxlev( int[] rxlev ) {
		this.rxlev = rxlev;
	}

	public byte[][] getBssid( ) {
		return bssid;
	}

	public void setBssid( byte[][] bssid ) {
		this.bssid = bssid;
	}

	public int[] getRssi( ) {
		return rssi;
	}

	public void setRssi( int[] rssi ) {
		this.rssi = rssi;
	}

	public int getSatellites( ) {
		return satellites;
	}

	public void setSatellites( int satellites ) {
		this.satellites = satellites;
	}

	public int getBattery( ) {
		return battery;
	}

	public void setBattery( int battery ) {
		this.battery = battery;
	}

	public int getAin0( ) {
		return ain0;
	}

	public void setAin0( int ain0 ) {
		this.ain0 = ain0;
	}

	public int getAin1( ) {
		return ain1;
	}

	public void setAin1( int ain1 ) {
		this.ain1 = ain1;
	}

	public int getExt1( ) {
		return ext1;
	}

	public void setExt1( int ext1 ) {
		this.ext1 = ext1;
	}

	public int getExt2( ) {
		return ext2;
	}

	public void setExt2( int ext2 ) {
		this.ext2 = ext2;
	}

	public byte[] getExtend( ) {
		return extend;
	}

	public void setExtend( byte[] extend ) {
		this.extend = extend;
	}

	public int getExtlen( ) {
		return extlen;
	}

	public void setExtlen( int extlen ) {
		this.extlen = extlen;
	}

	public int getOffset( ) {
		return offset;
	}

	public void setOffset( int offset ) {
		this.offset = offset;
	}

	public int getAcc( ) {
		return acc;
	}

	public void setAcc( int acc ) {
		this.acc = acc;
	}

	public char getState( ) {
		return state;
	}

	public void setState( char state ) {
		this.state = state;
	}
}