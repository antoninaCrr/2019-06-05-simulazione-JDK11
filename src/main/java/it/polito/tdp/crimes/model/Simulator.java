package it.polito.tdp.crimes.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.db.EventsDao;
import it.polito.tdp.crimes.model.Evento.TipoEvento;

public class Simulator {
	
	// param in input
	Integer giorno;
	Integer mese;
	Integer anno;
	Integer N;
	private Graph<District, DefaultWeightedEdge> grafo;
	private Map<Integer, District> distretti;

	
	// param in output
	private Integer malGestiti;
	
	// stato del mondo
	private Map<Integer,Integer> agenti; // mappa di distretto-#agenti
	
	// coda degli eventi
	private PriorityQueue<Evento> coda;	
	
	
	// metodo init
	public void init(Integer N, Integer anno, Integer mese, Integer giorno,
			Graph<District, DefaultWeightedEdge> grafo, Map<Integer, District> distretti) {
		this.N = N;
		this.anno = anno;
		this.mese = mese;
		this.giorno = giorno;
		this.grafo = grafo;
		this.distretti = distretti;
		
		this.malGestiti = 0;
		this.agenti = new HashMap<>();
		for(District di : this.grafo.vertexSet()) {
			agenti.put(di.getDisctrict_id(), 0);
		}
		
		// devo segliere dove posizionare la centrale
		// devo interrogare il db
		EventsDao dao = new EventsDao();
		Integer minD = dao.getDistrettoMin(anno);
		this.agenti.put(minD, this.N);
		
		// creo la coda
		this.coda = new PriorityQueue<Evento>();
		
		for(Event ei : dao.listAllEventsByDate(this.anno, this.mese, this.giorno)) {
			coda.add(new Evento(TipoEvento.CRIMINE,ei.getReported_date(),ei));
		}
		
		
		
		
	}
	
	// metodo run
	public void run() {
		Evento e;
		while((e = coda.poll()) != null) {
			processEvent(e);
		}
		
	}
	
	// metodo processEvent
    public void processEvent(Evento e) {
    	
    	switch(e.getTipo()) {
    	   
    	case CRIMINE:
    		System.out.println("NUOVO CRIMINE: "+e.getCrimine().getIncident_id());
    		Integer partenza = null; // distretto di partenza dell'agente
    		partenza = cercaAgente(e.getCrimine().getDistrict_id()); 
    		  
    		  if(partenza != null) {
    			  // c'è un agente libero
    			  if(partenza.equals(e.getCrimine().getDistrict_id())) {
    				  // tempo di arrivo = 0 -- se l'agente libero si trova già nel distretto del crimine (CASO LIMITE)
    				  System.out.println("AGENTE ARRIVA PER CRIMINE: "+e.getCrimine().getIncident_id());
    		    	  Long duration = getDuration(e.getCrimine().getOffense_category_id()); // la durata della gestione evento dipende dalla categoria dell'evento criminoso stesso
    		    	  this.coda.add(new Evento(TipoEvento.GESTITO,e.getData().plusSeconds(duration),e.getCrimine()));
    			  }else {
    				  // tempo di arrivo > 0 --> schedulo l'evento
    				  // devo calcolare il tempo impiegato a raggiungere il luogo del crimine
    				  Double distance = this.grafo.getEdgeWeight(this.grafo.getEdge(distretti.get(partenza), distretti.get(e.getCrimine().getDistrict_id()))); // segnale errore perchè il mio grafo ha come vertici dei distretti e non degli interi
    				  Long seconds = (long)((distance*1000)/(60/3.6));
    				  this.coda.add(new Evento(TipoEvento.ARRIVA_AGENTE, e.getData().plusSeconds(seconds), e.getCrimine()));
    			  }
    		  }else {
    			  // non c'è alcun agente libero (CASO LIMITE)
    			  System.out.println("CRIMINE "+e.getCrimine().getIncident_id()+" MAL GESTITO");
    			  this.malGestiti++;
    		  }
    		break;
    	   
    	case ARRIVA_AGENTE:
    		System.out.println("AGENTE ARRIVA PER CRIMINE: "+e.getCrimine().getIncident_id());
    		Long duration = getDuration(e.getCrimine().getOffense_category_id()); // la durata della gestione evento dipende dalla categoria dell'evento criminoso stesso
    		  this.coda.add(new Evento(TipoEvento.GESTITO,e.getData().plusSeconds(duration),e.getCrimine()));
    		  
    		// controllo se il crimine è mal gestito
    		  if(e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) { // se l'ora di arrivo dell'agente (ora dell'evento ARRIVA_AGENTE) è maggiore dell'ora 
    			  System.out.println("CRIMINE "+e.getCrimine().getIncident_id()+" MAL GESTITO");
    			  this.malGestiti++;                                                       // dell'ora in cui è accaduto il crimine + 15 min
    			                                                                           // allora è mal gestito
    		  }
    		break;
    	   
    	case GESTITO:
    		// l'agente torna libero --> incremento di 1 il numero di agenti presenti nel distretto
    		System.out.println("CRIMINE "+ e.getCrimine().getIncident_id()+" GESTITO");
    		this.agenti.put(e.getCrimine().getDistrict_id(), 
    				this.agenti.get(e.getCrimine().getDistrict_id())+1);
    	       break;
    	   
    	}
    }

	private Integer cercaAgente(Integer district_id) {
		// TODO Auto-generated method stub
		double distanza = Double.MAX_VALUE;
		Integer distretto = null;
		
		for(Integer d : this.agenti.keySet()) {
			if(this.agenti.get(d) > 0) {
				// ci sono degli agenti liberi
				// ma questo distretto è il più vicino?
				if(district_id.equals(d)) { // controllo se nel distretto del crimine vi è un agente libero altrimenti potrei rischiare un'eccezione sul calcolo della distanza
					// la distanza è zero
					distanza = Double.valueOf(0);
					distretto = d;
				}
				if(this.grafo.getEdgeWeight(this.grafo.getEdge(distretti.get(district_id), distretti.get(d))) < distanza) {
					// ho trovato un nuovo minimo
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(distretti.get(district_id), distretti.get(d)));
					distretto = d;
				}
			}
		}
		
		return distretto;
	}

	private Long getDuration(String offense_category_id) {
		// TODO Auto-generated method stub
		if(offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if(r.nextDouble() > 0.5) {
				return Long.valueOf(2*60*60); // ho un valore di ritorno in secondi che risulta più semplice da sommare alle ore
			}else {
				return Long.valueOf(1*60*60);
			}
		}else {
			return Long.valueOf(1*60*60);
		}
		
	}

	public Integer getMalGestiti() {
		return malGestiti;
	}
	
	
}
