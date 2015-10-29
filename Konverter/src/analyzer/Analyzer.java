package analyzer;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;


public class Analyzer {

	static String gz_path_ref = "D:\\BA\\IVU2MATSim\\result_simulation\\Nullfall\\ITERS\\it.0\\0.experienced_plans.xml.gz";
	static String gz_path_com = "D:\\BA\\IVU2MATSim\\result_simulation\\Planfall1\\ITERS\\it.0\\0.experienced_plans.xml.gz";
	

		
	public static void main(String[] args){
		
		decompressGzipFile(gz_path_ref, "D:\\BA\\IVU2MATSim\\result_simulation\\Nullfall\\ITERS\\it.0\\0.experienced_plans.xml");		
		decompressGzipFile(gz_path_com, "D:\\BA\\IVU2MATSim\\result_simulation\\Planfall1\\ITERS\\it.0\\0.experienced_plans.xml");
		
		String path_ref = "D:\\BA\\IVU2MATSim\\result_simulation\\Nullfall\\ITERS\\it.0\\0.experienced_plans.xml";
		String path_com = "D:\\BA\\IVU2MATSim\\result_simulation\\Planfall1\\ITERS\\it.0\\0.experienced_plans.xml";
		
		String path_ref2 = "D:\\BA\\IVU2MATSim\\result_simulation\\Nullfall\\ITERS\\it.0\\";
		String path_com2 = "D:\\BA\\IVU2MATSim\\result_simulation\\Planfall1\\ITERS\\it.0\\";
		
		String output = "D:\\BA\\IVU2MATSim\\result_analyzer\\";
		
		FileWriter writer;
		File file;
		
		DecimalFormat f = new DecimalFormat("#0.00"); 
		
		String ausgabe = "12345"; // Was soll ausgegeben werden?
		// 1: detaillierte Ausgabe jeder Person, 2: Durchschnitt für alle Personen, 3: Ausgabe für Linie, 4: Ausgabe für Route, 5: Ausgabe für Betriebsbereich
		
		Person p = new Person();
		Route r = new Route();
		Mode m = new Mode();
		
		
		try {
			
			if(ausgabe.contains("1")){
				file = new File(output + "Person.txt");
				writer = new FileWriter(file ,false);
				ArrayList<ArrayList<String>> reference = p.create(path_ref2);
				ArrayList<ArrayList<String>> compare = p.create(path_com2);
				for(int i=0;i<reference.size();i++){
					for(int j=0;i<compare.size();j++){
						if(reference.get(i).get(0).equals(compare.get(j).get(0))){
							
							if(reference.get(i).get(2).equals("00:00:00") & !compare.get(i).get(2).equals("00:00:00")){
								writer.write("ID: " + reference.get(i).get(0));
								writer.write(System.getProperty("line.separator"));	
								writer.write("-> Nutzte zuvor kein PT, danach schon");
								writer.write(System.getProperty("line.separator"));	
								break;
							}else{
								if(!reference.get(i).get(2).equals("00:00:00") & compare.get(i).get(2).equals("00:00:00")){
									writer.write("ID: " + reference.get(i).get(0));
									writer.write(System.getProperty("line.separator"));	
									writer.write("-> Nutzte zuvor PT, danach nicht mehr");
									writer.write(System.getProperty("line.separator"));	
									break;
								}else{
									if(reference.get(i).get(2).equals("00:00:00") & compare.get(i).get(2).equals("00:00:00")){
										writer.write("ID: " + reference.get(i).get(0));
										writer.write(System.getProperty("line.separator"));	
										writer.write("-> Nutzte zuvor kein PT, danach ebenfalls nicht");
										writer.write(System.getProperty("line.separator"));	
										break;
									}
								}
							}
							
							Double score_ref = Double.parseDouble(reference.get(i).get(1));
							Double score_com = Double.parseDouble(compare.get(i).get(1));
							Double score_dif = (1.0 - (score_ref/score_com)) * 100;
							
							Double travel_ref = Double.parseDouble(reumrechner(reference.get(i).get(2)));
							Double travel_com = Double.parseDouble(reumrechner(compare.get(i).get(2)));
							Double travel_dif = (1.0 - (travel_ref/travel_com)) * 100;
							
							
							Double waiting_ref = 0.0;
							Double waiting_com = 0.0;
							Double waiting_dif = 0.0;							

							if(Double.parseDouble(reumrechner(reference.get(i).get(3))) != 0.0){
								waiting_ref = Double.parseDouble(reumrechner(reference.get(i).get(3)));
							}
							
							if(Double.parseDouble(reumrechner(compare.get(i).get(3))) != 0.0){
								waiting_com = Double.parseDouble(reumrechner(compare.get(i).get(3)));
							}
							
							if((waiting_ref != 0.0) && (waiting_ref != 0.0)){
								waiting_dif = (1.0 - (waiting_ref/waiting_com)) * 100;
							}
							
							Double change_ref = 0.0;
							Double change_com = 0.0;
							Double change_dif = 0.0;
							
//							System.out.println("----------------------------");
//							for(int k=0; k<reference.get(i).size(); k++){
//								System.out.println(reference.get(i).get(k));
//							}

							if(Double.parseDouble(reference.get(i).get(4)) != 0.0){
								change_ref = Double.parseDouble(reference.get(i).get(4));
							}
							
							if(Double.parseDouble(compare.get(i).get(4)) != 0.0){
								change_com = Double.parseDouble(compare.get(i).get(4));
							}
							
							if((change_ref != 0.0) && (change_ref != 0.0)){
								change_dif = (1.0 - (change_ref/change_com)) * 100;
							}
							
							Double distance_ref = Double.parseDouble(reference.get(i).get(5));
							Double distance_com = Double.parseDouble(compare.get(i).get(5));
							Double distance_dif = (1.0 - (distance_ref/distance_com)) * 100;	

							
							writer.write("ID: " + reference.get(i).get(0));
							writer.write(System.getProperty("line.separator"));	
							
							if(score_dif>0.0){
								writer.write("-> Score: +" + f.format(score_dif) + "%" + " (" + f.format(score_ref) + " / " + f.format(score_com) + ")");
								writer.write(System.getProperty("line.separator"));	
							}else{
								writer.write("-> Score: " + f.format(score_dif) + "%" + " (" + f.format(score_ref) + " / " + f.format(score_com) + ")");
								writer.write(System.getProperty("line.separator"));	
							}
							
							if(travel_dif>0.0){
								writer.write("-> Fahrzeiten: +" + f.format(travel_dif) + "%" + " (" + f.format(travel_ref/60) + " Minuten / " + f.format(travel_com/60) + " Minuten)");
								writer.write(System.getProperty("line.separator"));	
							}else{
								writer.write("-> Fahrzeiten: " + f.format(travel_dif) + "%" + " (" + f.format(travel_ref/60) + " Minuten / " + f.format(travel_com/60) + " Minuten)");
								writer.write(System.getProperty("line.separator"));	
							}
							
							if(waiting_dif>0.0){
								writer.write("-> Anschlusswartezeiten: +" + f.format(waiting_dif) + "%" + " (" + f.format(waiting_ref/60) + " Minuten / " + f.format(waiting_com/60) + " Minuten)");
								writer.write(System.getProperty("line.separator"));	
							}else{
								writer.write("-> Anschlusswartezeiten: " + f.format(waiting_dif) + "%" + " (" + f.format(waiting_ref/60) + " Minuten / " + f.format(waiting_com/60) + " Minuten)");
								writer.write(System.getProperty("line.separator"));	
							}
							
							if(change_dif>0.0){
								writer.write("-> Anzahl Linienwechsel: +" + f.format(change_dif) + "%" + " (" + f.format(change_ref) + " / " + f.format(change_com) + ")");
								writer.write(System.getProperty("line.separator"));	
							}else{
								writer.write("-> Anzahl Linienwechsel: " + f.format(change_dif) + "%" + " (" + f.format(change_ref) + " / " + f.format(change_com) + ")");
								writer.write(System.getProperty("line.separator"));	
							}
							
							if(distance_dif>0.0){
								writer.write("-> Zurückgelegte Strecke: +" + f.format(distance_dif) + "%" + " (" + f.format(distance_ref) + " Meter / " + f.format(distance_com) + " Meter)");
								writer.write(System.getProperty("line.separator"));	
							}else{
								writer.write("-> Zurückgelegte Strecke: " + f.format(distance_dif) + "%" + " (" + f.format(distance_ref) + " Meter / " + f.format(distance_com) + " Meter)");
								writer.write(System.getProperty("line.separator"));	
							}
						
							break;
						}
					}
					
					writer.write(System.getProperty("line.separator"));	
				}
				writer.flush();
				writer.close();
			}
			
			
			if(ausgabe.contains("2")){
				file = new File(output + "Population.txt");
				writer = new FileWriter(file ,false);
				ArrayList<ArrayList<String>> reference = p.create(path_ref2);
				ArrayList<ArrayList<String>> compare = p.create(path_com2);
				Double score = 0.0;
				Double travel = 0.0;
				Double waiting = 0.0;
				Double change = 0.0;
				Double distance = 0.0;
				int pt_ref = 0;
				

				for(int i=0;i<reference.size();i++){
					if(!reference.get(i).get(2).equals("00:00:00")){
						score += Double.parseDouble(reference.get(i).get(1)); 
						travel += Double.parseDouble(reumrechner(reference.get(i).get(2)));
						waiting += Double.parseDouble(reumrechner(reference.get(i).get(3)));
						change += Double.parseDouble(reference.get(i).get(4)); 
						distance += Double.parseDouble(reference.get(i).get(5)); 
						
						pt_ref+=1;
					}
				}
				
				Double score_ave_ref = score/pt_ref;
				Double travel_ave_ref = travel/pt_ref;
				Double waiting_ave_ref = waiting/pt_ref;
				Double change_ave_ref = change/pt_ref;
				Double distance_ave_ref = distance/pt_ref;
				
				
				score = 0.0;
				travel = 0.0;
				waiting = 0.0;
				change = 0.0;
				distance = 0.0;
				int pt_com = 0;
				
				for(int i=0;i<compare.size();i++){
					if(!compare.get(i).get(2).equals("00:00:00")){
						score += Double.parseDouble(compare.get(i).get(1)); 
						travel += Double.parseDouble(reumrechner(compare.get(i).get(2))); 
						waiting += Double.parseDouble(reumrechner(compare.get(i).get(3))); 
						change += Double.parseDouble(compare.get(i).get(4)); 
						distance += Double.parseDouble(compare.get(i).get(5)); 
					
						pt_com+=1;
					}

				}
				
				Double score_ave_com = score/pt_com;
				Double travel_ave_com = travel/pt_com;
				Double waiting_ave_com = waiting/pt_com;
				Double change_ave_com = change/pt_com;
				Double distance_ave_com = distance/pt_com;
				
				Double score_ave_dif = (1.0 - (score_ave_ref/score_ave_com)) * 100;
				Double travel_ave_dif = (1.0 - (travel_ave_ref/travel_ave_com)) * 100;
				Double waiting_ave_dif = (1.0 - (waiting_ave_ref/waiting_ave_com)) * 100;
				Double change_ave_dif = (1.0 - (change_ave_ref/change_ave_com)) * 100;
				Double distance_ave_dif = (1.0 - (distance_ave_ref/distance_ave_com)) * 100;
				
				writer.write("Durchschnitt");
				writer.write(System.getProperty("line.separator"));	
				
				if(score_ave_dif>0.0){
					writer.write("-> Score: +" + f.format(score_ave_dif) + "%" + " (" + f.format(score_ave_ref) + " / " + f.format(score_ave_com) + ")");
					writer.write(System.getProperty("line.separator"));	
				}else{
					writer.write("-> Score: " + f.format(score_ave_dif) + "%" + " (" + f.format(score_ave_ref) + " / " + f.format(score_ave_com) + ")");
					writer.write(System.getProperty("line.separator"));	
				}
				
				if(travel_ave_dif>0.0){
					writer.write("-> Fahrzeiten: +" + f.format(travel_ave_dif) + "%" + " (" + f.format(travel_ave_ref/60) + " Minuten / " + f.format(travel_ave_com/60) + " Minuten)");
					writer.write(System.getProperty("line.separator"));	
				}else{
					writer.write("-> Fahrzeiten: " + f.format(travel_ave_dif) + "%" + " (" + f.format(travel_ave_ref/60) + " Minuten / " + f.format(travel_ave_com/60) + " Minuten)");
					writer.write(System.getProperty("line.separator"));	
				}
				
				if(waiting_ave_dif>0.0){
					writer.write("-> Anschlusswartezeiten: +" + f.format(waiting_ave_dif) + "%" + " (" + f.format(waiting_ave_ref/60) + " Minuten / " + f.format(waiting_ave_com/60) + " Minuten)");
					writer.write(System.getProperty("line.separator"));	
				}else{
					writer.write("-> Anschlusswartezeiten: " + f.format(waiting_ave_dif) + "%" + " (" + f.format(waiting_ave_ref/60) + " Minuten / " + f.format(waiting_ave_com/60) + " Minuten)");
					writer.write(System.getProperty("line.separator"));	
				}
				
				if(change_ave_dif>0.0){
					writer.write("-> Anzahl Linienwechsel: +" + f.format(change_ave_dif) + "%" + " (" + f.format(change_ave_ref) + " / " + f.format(change_ave_com) + ")");
					writer.write(System.getProperty("line.separator"));	
				}else{
					writer.write("-> Anzahl Linienwechsel: " + f.format(change_ave_dif) + "%" + " (" + f.format(change_ave_ref) + " / " + f.format(change_ave_com) + ")");
					writer.write(System.getProperty("line.separator"));	
				}
				
				if(distance_ave_dif>0.0){
					writer.write("-> Zurückgelegte Strecke: +" + f.format(distance_ave_dif) + "%" + " (" + f.format(distance_ave_ref) + " Meter / " + f.format(distance_ave_com) + " Meter)");
					writer.write(System.getProperty("line.separator"));	
				}else{
					writer.write("-> Zurückgelegte Strecke: " + f.format(distance_ave_dif) + "%" + " (" + f.format(distance_ave_ref) + " Meter / " + f.format(distance_ave_com) + " Meter)");
					writer.write(System.getProperty("line.separator"));	
				}
				
				writer.write("-> Anzahl Passagiere: " + pt_ref + " (Vorher) / " + pt_com + " (Nachher)");
				writer.write(System.getProperty("line.separator"));	
				
				writer.flush();
				writer.close();
				
			}
			
			if(ausgabe.contains("3")){
				file = new File(output + "Route.txt");
				writer = new FileWriter(file ,false);
				ArrayList<ArrayList<String>> reference = r.create(path_ref);
				ArrayList<ArrayList<String>> compare = r.create(path_com);
				
				ArrayList<String> links_ref = new ArrayList<String>();
				
				for(int i=0;i<reference.size();i++){
					for(int j=0;j<reference.get(i).size();j+=2){					
						links_ref.add(reference.get(i).get(j));							
					}
				}
				
				Double overall_ref = (double) links_ref.size();
				
				ArrayList<String> links_com = new ArrayList<String>();
				
				for(int i=0;i<compare.size();i++){
					for(int j=0;j<compare.get(i).size();j+=2){					
						links_com.add(compare.get(i).get(j));							
					}
				}
				
				Double overall_com = (double) links_com.size();
				
				while(!links_ref.isEmpty()){
					writer.write("Linie: " + links_ref.get(0));
					writer.write(System.getProperty("line.separator"));	
					Integer link_ref = 1;
					Integer link_com = 0;
					
					for(int k=1;k<links_ref.size();k++){
						if(links_ref.get(0).equals(links_ref.get(k))){
							link_ref+=1;
							links_ref.remove(k);
							k-=1;
						}
					}
					
					if(links_com.contains(links_ref.get(0))){
						for(int k=0;k<links_com.size();k++){
							if(links_ref.get(0).equals(links_com.get(k))){
								link_com+=1;
								links_com.remove(k);
								k-=1;
							}
						}
						
						writer.write("-> Häufigkeit: " + link_ref + " (Vorher) / " + link_com + " (Nachher)");
						writer.write(System.getProperty("line.separator"));	
						Double share_ref = (link_ref/overall_ref)*100;
						Double share_com = (link_com/overall_com)*100;
						writer.write("-> Anteil: " + f.format(share_ref) + "% (Vorher) / " + f.format(share_com) + "% (Nachher)");
						writer.write(System.getProperty("line.separator"));	
						Double share = (1.0-((link_ref/overall_ref)/(link_com/overall_com)))*100;
						if(share>0.0){
							writer.write("-> Anteil Gegenüberstellung: +" + f.format(share) + "%");
							writer.write(System.getProperty("line.separator"));
							writer.write(System.getProperty("line.separator"));	
						}else{
							writer.write("-> Anteil Gegenüberstellung: " + f.format(share) + "%");
							writer.write(System.getProperty("line.separator"));	
							writer.write(System.getProperty("line.separator"));	
						}
					}else{
						writer.write("-> Häufigkeit: " + link_ref + " / Im Vergleichsplan nicht vorhanden");
						writer.write(System.getProperty("line.separator"));	
						Double share = (link_ref/overall_ref)*100;
						writer.write("-> Anteil im Referenzplan: " + f.format(share) + "%");
						writer.write(System.getProperty("line.separator"));
						writer.write(System.getProperty("line.separator"));	
					}
					
					links_ref.remove(0);					
					
				}
				
				while(!links_com.isEmpty()){
					writer.write("Linie: " + links_com.get(0));
					writer.write(System.getProperty("line.separator"));	
					Integer link_com = 1;
					
					for(int k=1;k<links_com.size();k++){
						if(links_com.get(0).equals(links_com.get(k))){
							link_com+=1;
							links_com.remove(k);
							k-=1;
						}
					}
					
					links_com.remove(0);
					
					writer.write("-> Häufigkeit: " + link_com + " / Im Referenzplan nicht vorhanden");
					writer.write(System.getProperty("line.separator"));	
					Double share = (link_com/overall_com)*100;
					writer.write("-> Anteil im Vergleichsplan: " + f.format(share) + "%");
					writer.write(System.getProperty("line.separator"));	
					writer.write(System.getProperty("line.separator"));	
				}
				
				
				writer.flush();
				writer.close();			
			}
			
			if(ausgabe.contains("4")){
				file = new File(output + "RouteProfile.txt");
				writer = new FileWriter(file ,false);
				ArrayList<ArrayList<String>> reference = r.create(path_ref);
				ArrayList<ArrayList<String>> compare = r.create(path_com);
				
				ArrayList<String> routes_ref = new ArrayList<String>();
				
				for(int i=0;i<reference.size();i++){
					for(int j=1;j<reference.get(i).size();j+=2){					
						routes_ref.add(reference.get(i).get(j));							
					}
				}
				
				Double overall_ref = (double) routes_ref.size();
				
				ArrayList<String> routes_com = new ArrayList<String>();
				
				for(int i=0;i<compare.size();i++){
					for(int j=1;j<compare.get(i).size();j+=2){					
						routes_com.add(compare.get(i).get(j));							
					}
				}
				
				Double overall_com = (double) routes_com.size();
				
				while(!routes_ref.isEmpty()){
					writer.write("Route: " + routes_ref.get(0));
					writer.write(System.getProperty("line.separator"));	
					Integer route_ref = 1;
					Integer route_com = 0;
					
					for(int k=1;k<routes_ref.size();k++){
						if(routes_ref.get(0).equals(routes_ref.get(k))){
							route_ref+=1;
							routes_ref.remove(k);
							k-=1;
						}
					}
					
					if(routes_com.contains(routes_ref.get(0))){
						for(int k=0;k<routes_com.size();k++){
							if(routes_ref.get(0).equals(routes_com.get(k))){
								route_com+=1;
								routes_com.remove(k);
								k-=1;
							}
						}
						
						writer.write("-> Häufigkeit: " + route_ref + " (Vorher) / " + route_com + " (Nachher)");
						writer.write(System.getProperty("line.separator"));	
						Double share_ref = (route_ref/overall_ref)*100;
						Double share_com = (route_com/overall_com)*100;
						writer.write("-> Anteil: " + f.format(share_ref) + "% (Vorher) / " + f.format(share_com) + "% (Nachher)");
						writer.write(System.getProperty("line.separator"));	
						Double share = (1.0-((route_ref/overall_ref)/(route_com/overall_com)))*100;
						if(share>0.0){
							writer.write("-> Anteil Gegenüberstellung: +" + f.format(share) + "%");
							writer.write(System.getProperty("line.separator"));	
							writer.write(System.getProperty("line.separator"));	
						}else{
							writer.write("-> Anteil Gegenüberstellung: " + f.format(share) + "%");
							writer.write(System.getProperty("line.separator"));	
							writer.write(System.getProperty("line.separator"));	
						}
					}else{
						writer.write("-> Häufigkeit: " + route_ref + " / Im Vergleichsplan nicht vorhanden");
						writer.write(System.getProperty("line.separator"));	
						Double share = (route_ref/overall_ref)*100;
						writer.write("-> Anteil im Referenzplan: " + f.format(share) + "%");
						writer.write(System.getProperty("line.separator"));	
						writer.write(System.getProperty("line.separator"));	
					}
					
					routes_ref.remove(0);					
					
				}
				
				while(!routes_com.isEmpty()){
					writer.write("Route: " + routes_com.get(0));
					writer.write(System.getProperty("line.separator"));	
					Integer route_com = 1;
					
					for(int k=1;k<routes_com.size();k++){
						if(routes_com.get(0).equals(routes_com.get(k))){
							route_com+=1;
							routes_com.remove(k);
							k-=1;
						}
					}
					
					routes_com.remove(0);
					
					writer.write("-> Häufigkeit: " + route_com + " / Im Referenzplan nicht vorhanden");
					writer.write(System.getProperty("line.separator"));	
					Double share = (route_com/overall_com)*100;
					writer.write("-> Anteil im Vergleichsplan: " + f.format(share) + "%");
					writer.write(System.getProperty("line.separator"));	
					writer.write(System.getProperty("line.separator"));	
				}
				
				writer.flush();
				writer.close();
				
			}
			
			if(ausgabe.contains("5")){
				file = new File(output + "Mode.txt");
				writer = new FileWriter(file ,false);
				ArrayList<ArrayList<String>> reference = m.create(path_ref);
				ArrayList<ArrayList<String>> compare = m.create(path_com);
				
				ArrayList<String> modes_ref = new ArrayList<String>();
				
				for(int i=0;i<reference.size();i++){
					for(int j=0;j<reference.get(i).size();j+=1){					
						modes_ref.add(reference.get(i).get(j));							
					}
				}
				
				Double overall_ref = (double) modes_ref.size();
				
				ArrayList<String> modes_com = new ArrayList<String>();
				
				for(int i=0;i<compare.size();i++){
					for(int j=0;j<compare.get(i).size();j+=1){					
						modes_com.add(compare.get(i).get(j));							
					}
				}
				
				Double overall_com = (double) modes_com.size();
				
				while(!modes_ref.isEmpty()){
					writer.write("Betriebsbereich: " + modes_ref.get(0));
					writer.write(System.getProperty("line.separator"));	
					Integer mode_ref = 1;
					Integer mode_com = 0;
					
					for(int k=1;k<modes_ref.size();k++){
						if(modes_ref.get(0).equals(modes_ref.get(k))){
							mode_ref+=1;
							modes_ref.remove(k);
							k-=1;
						}
					}
					
					if(modes_com.contains(modes_ref.get(0))){
						for(int k=0;k<modes_com.size();k++){
							if(modes_ref.get(0).equals(modes_com.get(k))){
								mode_com+=1;
								modes_com.remove(k);
								k-=1;
							}
						}
						
						writer.write("-> Häufigkeit: " + mode_ref + " (Vorher) / " + mode_com + " (Nachher)");
						writer.write(System.getProperty("line.separator"));	
						Double share_ref = (mode_ref/overall_ref)*100;
						Double share_com = (mode_com/overall_com)*100;
						writer.write("-> Anteil: " + f.format(share_ref) + "% (Vorher) / " + f.format(share_com) + "% (Nachher)");
						writer.write(System.getProperty("line.separator"));	
						Double share = (1.0-((mode_ref/overall_ref)/(mode_com/overall_com)))*100;
						if(share>0.0){
							writer.write("-> Anteil Gegenüberstellung: +" + f.format(share) + "%");
							writer.write(System.getProperty("line.separator"));
							writer.write(System.getProperty("line.separator"));	
						}else{
							writer.write("-> Anteil Gegenüberstellung: " + f.format(share) + "%");
							writer.write(System.getProperty("line.separator"));	
							writer.write(System.getProperty("line.separator"));	
						}
					}else{
						writer.write("-> Häufigkeit: " + mode_ref + " / Im Vergleichsplan nicht vorhanden");
						writer.write(System.getProperty("line.separator"));	
						Double share = (mode_ref/overall_ref)*100;
						writer.write("-> Anteil im Referenzplan: " + f.format(share) + "%");
						writer.write(System.getProperty("line.separator"));	
						writer.write(System.getProperty("line.separator"));	
					}
					
					modes_ref.remove(0);					
					
				}
				
				while(!modes_com.isEmpty()){
					writer.write("Betriebsbereich: " + modes_com.get(0));
					writer.write(System.getProperty("line.separator"));	
					Integer mode_com = 1;
					
					for(int k=1;k<modes_com.size();k++){
						if(modes_com.get(0).equals(modes_com.get(k))){
							mode_com+=1;
							modes_com.remove(k);
							k-=1;
						}
					}
					
					modes_com.remove(0);
					
					writer.write("-> Häufigkeit: " + mode_com + " / Im Referenzplan nicht vorhanden");
					writer.write(System.getProperty("line.separator"));	
					Double share = (mode_com/overall_com)*100;
					writer.write("-> Anteil im Vergleichsplan: " + f.format(share) + "%");
					writer.write(System.getProperty("line.separator"));	
					writer.write(System.getProperty("line.separator"));	
				}
				
				writer.write(System.getProperty("line.separator"));	
				
				writer.flush();
				writer.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File file_n = new File(path_ref);
		File file_p = new File(path_com);
		
		if(file_n.exists()){
            file_p.delete();
        }

		if(file_p.exists()){
            file_p.delete();
        }
		
	}
	
//	------------------------------------------------------------------
	
	
	public static String reumrechner(String zeit){ //Umrechnen von HH:MM:SS in Sekunden
		
		String[] tokens = zeit.split(":");
		int hours = Integer.parseInt(tokens[0]);
		int minutes = Integer.parseInt(tokens[1]);
		int seconds = Integer.parseInt(tokens[2]);
		Integer duration = 3600 * hours + 60 * minutes + seconds;
		
		return duration.toString();
		
	}
	
	
 
    private static void decompressGzipFile(String gzipFile, String newFile) {
        try {
            FileInputStream fis = new FileInputStream(gzipFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int len;
            while((len = gis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            //close resources
            fos.close();
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
         
    }
	

}
