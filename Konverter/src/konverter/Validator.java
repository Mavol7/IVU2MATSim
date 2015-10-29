package konverter;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.utils.TransitScheduleValidator;
import org.matsim.pt.utils.TransitScheduleValidator.ValidationResult;

public class Validator {

	public static void main(String[] args){
		
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.loadConfig("input/config.xml"));
		new MatsimNetworkReader(scenario).readFile("output/network.xml");
		new TransitScheduleReader(scenario).readFile("output/transitSchedule.xml");
			
		ValidationResult result = TransitScheduleValidator.validateAll(scenario.getTransitSchedule(), scenario.getNetwork());
		TransitScheduleValidator.printResult(result);
	}
	
}
