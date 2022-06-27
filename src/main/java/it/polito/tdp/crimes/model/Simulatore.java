package it.polito.tdp.crimes.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.db.EventsDao;
import it.polito.tdp.crimes.model.Evento.TipoEvento;

public class Simulatore {

	
	// input
	private int giorno;
	private int mese; 
	private int anno;
	private int N; // numero degli agenti
	
	
	// output
	private int numEventiMalGestiti;
	
	// stato del mondo simulato
	private Graph<Integer,DefaultWeightedEdge> grafo;
	private Map<Integer,Integer> numAgentiDisponibiliPerDistretto;
	
	// coda
	private PriorityQueue<Evento> coda;
	
	
	public void init(int giorno, int mese, int anno, int N, Graph<Integer,DefaultWeightedEdge> grafo) {
		
		EventsDao dao = new EventsDao();
		
		this.coda = new PriorityQueue<Evento>();
		
		this.giorno = giorno;
		this.mese = mese;
		this.anno = anno;
		this.N = N;
		this.grafo = grafo;
		
		this.numAgentiDisponibiliPerDistretto = new HashMap<Integer,Integer>();
		
		// creo la mappa
		for(Integer i : this.grafo.vertexSet()) {
			this.numAgentiDisponibiliPerDistretto.put(i, 0);
		}
		
		// calcolo il distretto con minore criminalità dell'anno 
		this.numAgentiDisponibiliPerDistretto.put(dao.getDistrettoMin(anno), N);
		
		// carico la coda degli eventi
		for(Event e : dao.listAllEventsByDate(anno, mese, giorno)) {
			this.coda.add(new Evento(TipoEvento.CRIMINE,e.getReported_date(),e));
		}
	}
	
	public void run() {
		
		while(!this.coda.isEmpty()) {
			Evento e = this.coda.poll();
			processEvent(e);
		}
	}

	private void processEvent(Evento e) {
		
		switch(e.getTipo()) {
		
		case CRIMINE:
			System.out.println("NUOVO CRIMINE! " + e.getCrimine().getIncident_id());
			//cerco l'agente libero più vicino
			Integer partenza = null;
			partenza = cercaAgente(e.getCrimine().getDistrict_id());
			if(partenza != null) {
				//c'è un agente libero in partenza
				//setto l'agente come occupato
				this.numAgentiDisponibiliPerDistretto.put(partenza, this.numAgentiDisponibiliPerDistretto.get(partenza) - 1);
				//cerco di capire quanto ci metterà l'agente libero ad arrivare sul posto
				Double distanza;
				if(partenza.equals(e.getCrimine().getDistrict_id()))
					distanza = 0.0;
				else
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(partenza, e.getCrimine().getDistrict_id()));
				
				Long seconds = (long) ((distanza * 1000)/(60/3.6));
				this.coda.add(new Evento(TipoEvento.ARRIVA_AGENTE, e.getData().plusSeconds(seconds), e.getCrimine()));
				
			} else {
				//non c'è nessun agente libero al momento -> crimine mal gestito
				System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " MAL GESTITO!");
				this.numEventiMalGestiti ++;
			}
			break;
			
		case ARRIVA_AGENTE:
			System.out.println("ARRIVA AGENTE PER CRIMINE! " + e.getCrimine().getIncident_id());
			Long duration = getDurata(e.getCrimine().getOffense_category_id());
			this.coda.add(new Evento(TipoEvento.GESTITO,e.getData().plusSeconds(duration), e.getCrimine()));
			//controllare se il crimine è mal gestito
			if(e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
				System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " MAL GESTITO!");
				this.numEventiMalGestiti ++;
			}
			break;
			
		case GESTITO:
			System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " GESTITO");
			this.numAgentiDisponibiliPerDistretto.put(e.getCrimine().getDistrict_id(), this.numAgentiDisponibiliPerDistretto.get(e.getCrimine().getDistrict_id())+1);
			break;
			
		
		
	}

	}
	
	
	private Long getDurata(String offense_category_id) {
		if(offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if(r.nextDouble() > 0.5)
				return Long.valueOf(2*60*60);
			else
				return Long.valueOf(1*60*60);
		} 
		else {
			return Long.valueOf(2*60*60);
		}
	}

	
	private Integer cercaAgente(Integer district_id) {
		Double distanza = Double.MAX_VALUE;
		Integer distretto = null;
		
		for(Integer d : this.numAgentiDisponibiliPerDistretto.keySet()) {
			if(this.numAgentiDisponibiliPerDistretto.get(d) > 0) {
				if(district_id.equals(d)) {
					distanza = 0.0;
					distretto = d; 
				} else if(this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d)) < distanza) {
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d));
					distretto = d;
				}
			}
		}
		return distretto;
	}
	
	public int getMalGestiti() {
		return this.numEventiMalGestiti;
	}
	
}
