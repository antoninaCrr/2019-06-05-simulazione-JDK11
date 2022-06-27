package it.polito.tdp.crimes.model;

import com.javadocmd.simplelatlng.LatLng;

public class District {
	
	private int disctrict_id;
	private LatLng centro;

	public District(int disctrict_id, LatLng centro) {
		super();
		this.disctrict_id = disctrict_id;
		this.centro = centro;
	}

	public int getDisctrict_id() {
		return disctrict_id;
	}

	public void setDisctrict_id(int disctrict_id) {
		this.disctrict_id = disctrict_id;
	}

	public LatLng getCentro() {
		return centro;
	}

	public void setCentro(LatLng centro) {
		this.centro = centro;
	}
	
	
	
	
}
