package konverter;

import java.io.IOException;


public class Konverter {
	
	
	// Bloeder Kommentar
	  public static void main(String[] args) throws IOException{
		  String export = "D:\\Uni\\Informatik\\Semester 9\\Bachelorarbeit\\IVUfleet\\";
		  String output = "D:\\Uni\\Informatik\\Semester 9\\Bachelorarbeit\\Ausgabe2\\";
		  
//		  vehicleTransit vt = new vehicleTransit();
		  network nw = new network();
//		  transitSchedule ts = new transitSchedule();
		  
//		  vt.create(export, output);
		  nw.create(export, output);
//		  ts.create(export, output);
	  }
			  
	
		
}
