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
	private Graph<Integer,DefaultWeightedEdge> grafo;
	private List<District> distrettiConCentro;
	
	public Model() {
		this.dao = new EventsDao();
	}
	
	public void creaGrafo(int anno) {
		
		this.grafo = new SimpleWeightedGraph<Integer,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.grafo, this.dao.getDistricts());
		
		List<Integer> districts = this.dao.getDistricts();
		distrettiConCentro = this.dao.getCentroCriminiByAnno(anno);
		for(Integer i : districts) {
			for(Integer j : districts) {
				if(i!=j) {
					double distanza = LatLngTool.distance(distrettiConCentro.get(i-1).getCentro(), distrettiConCentro.get(j-1).getCentro(), LengthUnit.KILOMETER);
					Graphs.addEdge(this.grafo, i, j,distanza);
				}
			}
		}
		
		
		System.out.println("GRAFO CREATO: "+this.grafo.vertexSet().size()+" vertici, "+this.grafo.edgeSet().size()+" archi\n");
	}
	
	
	public int simula(int anno, int mese, int giorno, int N) {
		Simulatore sim = new Simulatore();
		sim.init(giorno, mese, anno, N, grafo);
		sim.run();
		return sim.getMalGestiti();
	}
	
	public List<Integer> getYears(){
		return this.dao.getYears();
	}
	
	public List<Integer> getMonths(){
		return this.dao.getMesi();
	}
	
	public List<Integer> getDay(){
		return this.dao.getGiorni();
	}
	
	public int getNVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Integer> getVertici() {
		return new ArrayList<Integer>(this.grafo.vertexSet());
	}

	public List<Vicino> getVicini(Integer id) {
		
		List<Integer> adiacenti = Graphs.neighborListOf(this.grafo, id);
		List<Vicino> vicini = new ArrayList<Vicino>();
		for(Integer i : adiacenti) {
			vicini.add(new Vicino(i,this.grafo.getEdgeWeight(this.grafo.getEdge(id, i))));
		}
		Collections.sort(vicini);
		return vicini;
	}

	
	
	
	
}
