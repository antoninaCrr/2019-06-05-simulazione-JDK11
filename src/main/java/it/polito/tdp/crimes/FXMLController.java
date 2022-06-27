/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.model.Model;
import it.polito.tdp.crimes.model.Vicino;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxAnno"
    private ComboBox<Integer> boxAnno; // Value injected by FXMLLoader

    @FXML // fx:id="boxMese"
    private ComboBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="boxGiorno"
    private ComboBox<Integer> boxGiorno; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaReteCittadina"
    private Button btnCreaReteCittadina; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaReteCittadina(ActionEvent event) {

    	txtResult.clear();
    	int anno = this.boxAnno.getValue();
    	if(anno==0) {
    		txtResult.appendText("Seleziona un anno!");
    		return;
    	}
    	
    	this.model.creaGrafo(anno);
    	txtResult.appendText("Grafo creato : "+this.model.getNVertici()+" vertici, "+this.model.getNArchi()+" archi\n\n");
    	
    	for(Integer id : this.model.getVertici()) {
    		
    		txtResult.appendText("Distretti adiacenti a "+id+"\n");
    		for(Vicino v : this.model.getVicini(id)) {
    			txtResult.appendText(v.getDisctrict_id()+" ("+v.getDistance()+" km)\n");
    		}
    	}
    	
    	
    }

    @FXML
    void doSimula(ActionEvent event) {

    	int anno = this.boxAnno.getValue();
    	int mese = this.boxMese.getValue();
    	int giorno = this.boxGiorno.getValue();
    	
    	if(anno==0 || mese==0 || giorno==0) {
    		txtResult.appendText("Selezionare anno, mese e giorno!");
    		return;
    	}
    	
    	int N;
    	
    	try {
    		N = Integer.parseInt(this.txtN.getText());
    		int malGestiti = this.model.simula(anno, mese, giorno, N);
    		
    		txtResult.appendText("SIMULAZIONE TERMINAATA\n");
    		txtResult.appendText("Numero di casi mal gestiti : "+malGestiti);
    		
    	}catch(NumberFormatException e) {
    		e.printStackTrace();
    		txtResult.appendText("Il campo 'n' deve essere un numero intero!");
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxGiorno != null : "fx:id=\"boxGiorno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaReteCittadina != null : "fx:id=\"btnCreaReteCittadina\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.boxAnno.getItems().addAll(this.model.getYears());
    	this.boxMese.getItems().addAll(this.model.getMonths());
    	this.boxGiorno.getItems().addAll(this.model.getDay());
    }
}
