package konverter;


import java.io.IOException;


public class Konverter {
	
	
	  public static void main(String[] args) throws IOException{
		  String input = "D:\\BA\\Faelle\\Planfall1\\IVUfleet\\";
		  
		  VehicleTransit vt = new VehicleTransit();
		  Network nw = new Network();
		  TransitSchedule ts = new TransitSchedule();
		  
		  vt.create(input);
		  nw.create(input);
		  ts.create(input);
		  
	  }
			  
	
		
}