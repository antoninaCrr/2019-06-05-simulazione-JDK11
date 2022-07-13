package it.polito.tdp.crimes.model;

public class Arco {

	private District d1;
	private District d2;
	private Double peso;
	
	
	public Arco(District d1, District d2, Double peso) {
		super();
		this.d1 = d1;
		this.d2 = d2;
		this.peso = peso;
	}


	public District getD1() {
		return d1;
	}


	public void setD1(District d1) {
		this.d1 = d1;
	}


	public District getD2() {
		return d2;
	}


	public void setD2(District d2) {
		this.d2 = d2;
	}


	public Double getPeso() {
		return peso;
	}


	public void setPeso(Double peso) {
		this.peso = peso;
	}
	
	
	
	
	
}
