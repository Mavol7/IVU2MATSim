package analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;


public class Person {
	
	
	public ArrayList<ArrayList<String>> create(String path) throws IOException{
		
		ArrayList<ArrayList<String>> all_persons = new ArrayList<ArrayList<String>>();
	
		EventsManager events = EventsUtils.createEventsManager();
		 
		WaitingTimeHandler waitingTimeHandler = new WaitingTimeHandler();												

		events.addHandler(waitingTimeHandler);
		MatsimEventsReader reader = new MatsimEventsReader(events);
		reader.readFile(path + "0.events.xml.gz");
		
//		Map<String,Double> waitingTimes = waitingTimeHandler.getWaitingTimes();
//		System.out.println(waitingTimes.size());
		
		Document doc = null; 
	
	    File data = new File(path + "0.experienced_plans.xml");
	    
        try {
        	// Das Dokument erstellen 
            SAXBuilder builder = new SAXBuilder(); 
			doc = builder.build(data);
			 
			Element element = doc.getRootElement(); 
			
			List person = (List) element.getChildren(); 
			
			for(int i=0;i<person.size();i++){
				ArrayList<String> one_person = new ArrayList<String>();
				//one person = [0:ID, 1:Score, 2:gesamte Fahrzeit, 3: Anschlusswartezeiten, 4: Anzahl Linienwechsel, 5:zurückgelegte Strecke]
				
//				System.out.println("ID: " + ((Element) person.get(i)).getAttributeValue("id"));				
				one_person.add(((Element) person.get(i)).getAttributeValue("id"));
				
				
				String waitingTime = waitingTimeHandler.getWaitingTime(((Element) person.get(i)).getAttributeValue("id").toString()).toString();
//				if(waitingTimes.containsKey(((Element) person.get(i)).getAttributeValue("id").toString())){
//					waitingTime = Integer.toString((waitingTimes.get(((Element) person.get(i)).getAttributeValue("id").toString()).intValue()));
//				}
				
				System.out.println(waitingTime);
				
				List plan = (List) ((Element) person.get(i)).getChildren();
				
//				System.out.println("Score: " + ((Element) plan.get(0)).getAttributeValue("score")); 				
				one_person.add(((Element) plan.get(0)).getAttributeValue("score"));
				
				List actions = (List) ((Element) plan.get(0)).getChildren();
				
				String travel = "00:00:00";
				boolean pred_mode = false;
//				String pred_time = "00:00:00";
				Integer route_change = 0;
				Double distance = 0.0;
				String waiting = "00:00:00";
				
				for(int j=1;j<actions.size();j+=2){
						if(((Element) actions.get(j)).getAttributeValue("mode").equals("pt")){
							travel = umrechner(addierer(reumrechner(travel), reumrechner(((Element) actions.get(j)).getAttributeValue("trav_time"))));
													
							if(((Element) actions.get(j+3)).getAttributeValue("type").equals("pt interaction")){
								route_change += 1;
//								waiting = umrechner(addierer(reumrechner(waiting),(subtrahierer(reumrechner(((Element) actions.get(j)).getAttributeValue("dep_time")), reumrechner(pred_time)))));
							}
	//						
	//						pred_mode = true;
	//						pred_time = ((Element) actions.get(j)).getAttributeValue("arr_time");
							
							List route = (List) ((Element) actions.get(j)).getChildren();						
							distance += Double.parseDouble(((Element) route.get(0)).getAttributeValue("distance"));
						}
					
				}
				
//				System.out.println("------>Fahrzeit: " + travel);
//				System.out.println("------>Linienwechsel: " + route_change);
//				System.out.println("------>Distanz: " + distance);
				
				one_person.add(travel);
				one_person.add(umrechner(waitingTime));
				one_person.add(route_change.toString());
				one_person.add(distance.toString());
				
				all_persons.add(one_person);
				
				
			}
			
			
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return all_persons; 
	    
	}
	
	
	//-------------------------------------------------------------------------------
	
		public static String umrechner(String zeit){ // Umrechnen von Sekunden in die Form HH:MM:SS

			int int_zeit = Integer.parseInt(zeit);
			

			int int_stunden = int_zeit/3600;		
			String string_stunden = String.valueOf(int_stunden);
			if(string_stunden.length()==1){
				string_stunden = "0"+string_stunden;
			}
			
			int int_minuten = (int_zeit - int_stunden * 3600) / 60;
			String string_minuten = String.valueOf(int_minuten);
			if(string_minuten.length()==1){
				string_minuten = "0"+string_minuten;
			}
			
			int int_sekunden = int_zeit - int_stunden * 3600 - int_minuten * 60;
			String string_sekunden = String.valueOf(int_sekunden);
			if(string_sekunden.length()==1){
				string_sekunden = "0"+string_sekunden;
			}
			

			return string_stunden + ":" + string_minuten + ":" + string_sekunden;
		}
		
		public static String reumrechner(String zeit){ //Umrechnen von HH:MM:SS in Sekunden
			
			String[] tokens = zeit.split(":");
			int hours = Integer.parseInt(tokens[0]);
			int minutes = Integer.parseInt(tokens[1]);
			int seconds = Integer.parseInt(tokens[2]);
			Integer duration = 3600 * hours + 60 * minutes + seconds;
			
			return duration.toString();
			
		}
		
		public static String addierer(String ankunft, String warte){ // Addieren von Sekunden, speziell für Ankunftszeit + Haltestellenhaltezeit
			
			int int_ankunft = Integer.parseInt(ankunft);
			int int_warte = Integer.parseInt(warte);

			
			return String.valueOf(int_ankunft + int_warte);
			
		}
		
		public static String subtrahierer(String ankunft, String abfahrt){ // Addieren von Sekunden, speziell für Ankunftszeit + Haltestellenhaltezeit
			
			int int_ankunft = Integer.parseInt(ankunft);
			int int_abfahrt = Integer.parseInt(abfahrt);

			
			return String.valueOf(int_abfahrt - int_ankunft);
			
		}



}
