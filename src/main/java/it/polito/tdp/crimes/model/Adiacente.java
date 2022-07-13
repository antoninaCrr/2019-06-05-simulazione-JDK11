package it.polito.tdp.crimes.model;

public class Adiacente implements Comparable<Adiacente> {

	private District d;
    private Double distanza;
	
    public Adiacente(District d, Double distanza) {
		//super();
		this.d = d;
		this.distanza = distanza;
	}

	public District getD() {
		return d;
	}

	public void setD(District d) {
		this.d = d;
	}

	public Double getDistanza() {
		return distanza;
	}

	public void setDistanza(Double distanza) {
		this.distanza = distanza;
	}

	@Override
	public String toString() {
		return d.getDisctrict_id()+"  "+this.distanza+" km";

	}

	@Override
	public int compareTo(Adiacente o) {
		// TODO Auto-generated method stub
		return this.distanza.compareTo(o.getDistanza());
	}
    
    
    
    
}
