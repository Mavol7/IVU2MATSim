package konverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleCapacity;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleWriterV1;
import org.matsim.vehicles.VehiclesFactory;

public class VehicleTransit {

	
public void create(String input) throws IOException {
		
	
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.loadConfig("input/config.xml"));
		
		VehiclesFactory vfac = scenario.getTransitVehicles().getFactory();
	
		//////////////////////////////////
		
		FileReader ft = new FileReader(input + "Fahrzeugtypen.trd");
		BufferedReader br_ft = new BufferedReader(ft);
		
		String zeile_ft = "";
		
		VehicleType vehicleType;

		while ((zeile_ft = br_ft.readLine()) != null) {
			String[] segs_ft = zeile_ft.split("\"");

			if (segs_ft[1].equals("1.0")) {
				continue;
			}
			
			FileReader wa2 = new FileReader(input + "Wagenumlaeufe.trd");
			BufferedReader br_wa2 = new BufferedReader(wa2);

			String zeile_wa2 = "";
			boolean used = false;
			
			while ((zeile_wa2 = br_wa2.readLine()) != null) {
				String[] segs_wa2 = zeile_wa2.split("\"");

				if (segs_wa2[1].equals("1.0")) {
					continue;
				}
				
				if(segs_wa2[7].equals(segs_ft[1])){
					used = true;
					break;
				}
			}
			
			br_wa2.close();
			
			if(!used){
				continue;
			}
			
			Id<VehicleType> id_vt = Id.create(segs_ft[1], VehicleType.class);		
			
			vehicleType = vfac.createVehicleType(id_vt);
			
			vehicleType.setDescription(segs_ft[5]);
			
			VehicleCapacity capacity = vfac.createVehicleCapacity();
			capacity.setSeats(Integer.valueOf(segs_ft[11]));
			capacity.setStandingRoom(Integer.valueOf(segs_ft[13]));
			vehicleType.setCapacity(capacity);
			vehicleType.setLength(Double.parseDouble(segs_ft[9]));
			
			scenario.getTransitVehicles().addVehicleType(vehicleType);
			
			FileReader wa = new FileReader(input + "Wagenumlaeufe.trd");
			BufferedReader br_wa = new BufferedReader(wa);

			String zeile_wa = "";

			while ((zeile_wa = br_wa.readLine()) != null) {

				String[] segs_wa = zeile_wa.split("\"");

				if (segs_wa[1].equals("1.0")) {
					continue;
				}

				if (segs_wa[7].equals(segs_ft[1])) {
					
					Id<Vehicle> id_v = Id.create(segs_wa[1] + "_" + segs_wa[3] + "_" + segs_wa[5], Vehicle.class);
					Vehicle vehicle = vfac.createVehicle(id_v, vehicleType);

					scenario.getTransitVehicles().addVehicle(vehicle);
				}
					
			}

			br_wa.close();
		}
		
		br_ft.close();
		
		
		//////////////////////////////////
		
		new VehicleWriterV1(scenario.getTransitVehicles()).writeFile("output_konverter/Planfall1/transitVehicles.xml");

}
}
