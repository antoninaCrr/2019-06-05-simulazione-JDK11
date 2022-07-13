package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private EventsDao dao;
	private Graph<District,DefaultWeightedEdge> grafo;
	private Map<Integer, District> distretti;
	private Simulator sim;
	
	public Model() {
		this.dao = new EventsDao();
		this.sim = new Simulator();
	}
	
	public void creaGrafo(int anno) {
		
		this.grafo = new SimpleWeightedGraph<District,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.distretti = new HashMap<>();
		
		// vertici
		Graphs.addAllVertices(this.grafo, dao.getDistrictByYear(anno, distretti));
		
		// archi
		for(District d1 : this.grafo.vertexSet()) {
			for(District d2 : this.grafo.vertexSet()) {
				if(!d1.equals(d2)) {
					double peso = LatLngTool.distance(d1.getCentro(), d2.getCentro(),LengthUnit.KILOMETER);
					Graphs.addEdgeWithVertices(this.grafo, d1, d2, peso);
				}
			}
		}
				
		System.out.println("GRAFO CREATO: "+this.grafo.vertexSet().size()+" vertici, "+this.grafo.edgeSet().size()+" archi\n");
	}
	
	
	
	
	public int getNVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<District> getVertici() {
		return new ArrayList<District>(this.grafo.vertexSet());
	}
	
	public List<Integer> getYears(){
		return dao.getYears();
	}

	public List<Adiacente> getAdiacenti(District d){
		List<Adiacente> vicini = new ArrayList<>();
		
		for(District di : Graphs.neighborListOf(this.grafo, d)) {
			Adiacente a = new Adiacente(di,this.grafo.getEdgeWeight(this.grafo.getEdge(d, di)));
			vicini.add(a);
		}
		
		Collections.sort(vicini);
		
		return vicini;
		
	}
	
	public void simula(Integer N, Integer anno, Integer mese, Integer giorno) {
		
		sim.init(N, anno, mese, giorno, grafo, distretti);
		sim.run();
	}
	
	public Integer getMalGestiti() {
		
		return sim.getMalGestiti();
	}
	

	
	
	
	
}
