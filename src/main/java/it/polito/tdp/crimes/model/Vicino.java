package it.polito.tdp.crimes.model;

public class Vicino implements Comparable<Vicino> {

	private Integer disctrict_id;
	private double distance;
	
	public Vicino(Integer disctrict_id, double distance) {
		super();
		this.disctrict_id = disctrict_id;
		this.distance = distance;
	}

	public Integer getDisctrict_id() {
		return disctrict_id;
	}

	public double getDistance() {
		return distance;
	}

	@Override
	public int compareTo(Vicino o) {
		return Double.compare(distance, o.distance);
	}
	
	
	
	
}
