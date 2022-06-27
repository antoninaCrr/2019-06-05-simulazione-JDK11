package it.polito.tdp.crimes.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;

import it.polito.tdp.crimes.model.District;
import it.polito.tdp.crimes.model.Event;

public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Event> listAllEventsByDate(int anno, int mese, int giorno){
		String sql = "SELECT * "
				+ "FROM events e  "
				+ "WHERE YEAR(e.reported_date)=? AND MONTH(e.reported_date)=? AND DAY(e.reported_date)=?";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno);
			st.setInt(2, mese);
			st.setInt(3, giorno);
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Integer> getYears(){
		
		String sql ="SELECT DISTINCT YEAR(e.reported_date) as year "
				+ "FROM events e "
				+ "ORDER BY YEAR(e.reported_date)";
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Integer> list = new ArrayList<Integer>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
				list.add(res.getInt("year"));
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Integer> getDistricts(){
		
		String sql = "SELECT DISTINCT e.district_id "
				+ "FROM events e "
				+ "ORDER BY e.district_id";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Integer> list = new ArrayList<Integer>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
				list.add(res.getInt("district_id"));
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<District> getCentroCriminiByAnno(int anno) {
		
		String sql = "SELECT e.district_id , AVG(e.geo_lat) as avgLat, AVG(e.geo_lon) as  avgLong "
				+ "FROM events e "
				+ "WHERE YEAR(e.reported_date)=? "
				+ "GROUP BY e.district_id";
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, anno);
			
			List<District> list = new ArrayList<District>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
				int district_id = res.getInt("district_id");
				double avgLat = res.getDouble("avgLat");
				double avgLong = res.getDouble("avgLong");
				LatLng centro = new LatLng(avgLat,avgLong);
				list.add(new District(district_id,centro));
			
				
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public Integer getDistrettoMin(int anno) {
		
		String sql = "SELECT e.district_id, COUNT(*) AS criminalità "
				+ "FROM events e "
				+ "WHERE YEAR(e.reported_date)=? "
				+ "GROUP BY e.district_id "
				+ "ORDER BY COUNT(*) ";
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, anno);
			
			
			ResultSet res = st.executeQuery() ;
			
			res.first();
			
			int district_id = res.getInt("district_id");
			
			conn.close();
			return district_id;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Integer> getAnni(){
		String sql = "SELECT DISTINCT YEAR(reported_date) as anno FROM events";
		List<Integer> result = new ArrayList<Integer>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				result.add(res.getInt("anno"));
			}
			conn.close();
			Collections.sort(result);
			return result;
		} catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	public List<Integer> getMesi(){
		String sql = "SELECT DISTINCT MONTH(reported_date) as anno FROM events";
		List<Integer> result = new ArrayList<Integer>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				result.add(res.getInt("anno"));
			}
			conn.close();
			Collections.sort(result);
			return result;
		} catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	public List<Integer> getGiorni(){
		String sql = "SELECT DISTINCT DAY(reported_date) as anno FROM events";
		List<Integer> result = new ArrayList<Integer>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				result.add(res.getInt("anno"));
			}
			conn.close();
			Collections.sort(result);
			return result;
		} catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
		

}
