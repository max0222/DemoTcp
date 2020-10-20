package com.eelink.tcp.model;

public class Coordinate implements Cloneable {
	private double longitude;
	private double latitude;

	public Coordinate() {
		// TODO Auto-generated constructor stub
		this.longitude = 0.0;
		this.latitude = 0.0;
	}

	public boolean isEqual(Coordinate c) {
		return (this.latitude == c.latitude) && (this.longitude == c.longitude);
	}

	public boolean isZero() {
		return (this.latitude == 0) && (this.longitude == 0);
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public Coordinate clone() {
		Coordinate c = null;
		try {
			c = (Coordinate) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return c;
	}

}
