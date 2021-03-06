package konverter;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;

public class RunSimulation {
	
	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig("input/config.xml");
		
		Scenario scenario = ScenarioUtils.loadScenario(config);
		
		Controler controler = new Controler(scenario);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.overwriteExistingFiles);
		controler.run();
	
	}
	
}
