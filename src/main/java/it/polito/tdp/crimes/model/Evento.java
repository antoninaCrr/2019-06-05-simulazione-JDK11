package it.polito.tdp.crimes.model;

import java.time.LocalDateTime;

public class Evento implements Comparable<Evento> {

	// TIPI DI EVENTO
	// 1. Evento criminoso modellati dalla classe Event
	//  1.1 La centrale seleziona l'agente più vicino al luogo del crimine
	//  1.2. Setta l'agente a occupato
	
	// 2. Agente arriva sul luogo del crimine
	//  2.1  Definisco quanto dura l'intervento
	//  2.2 Controlla se l'evento è mal gestito o no (?)
	
	// 3. Crimine terminato
	//  3.1 Libero l'agente
	
	public enum TipoEvento{
		CRIMINE,
		ARRIVA_AGENTE,
		GESTITO
	}
	
	private TipoEvento tipo;
	private LocalDateTime data; // param su cui facciamo l'ordinamento
	private Event crimine; // E' IL JAVA BEAN CHE MODELLA LE RIGHE DELLA TAB
	
	public Evento(TipoEvento tipo, LocalDateTime data, Event crimine) {
		super();
		this.tipo = tipo;
		this.data = data;
		this.crimine = crimine;
	}

	public TipoEvento getTipo() {
		return tipo;
	}

	public void setTipo(TipoEvento tipo) {
		this.tipo = tipo;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public Event getCrimine() {
		return crimine;
	}

	public void setCrimine(Event crimine) {
		this.crimine = crimine;
	}

	@Override
	public int compareTo(Evento o) {
		// TODO Auto-generated method stub
		return this.data.compareTo(o.getData());
	}
	
	
	
	
	
}
