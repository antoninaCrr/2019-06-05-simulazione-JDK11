package it.polito.tdp.crimes.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();
		model.creaGrafo(2015);
		model.simula(10, 2015, 5, 27);
	}

}
