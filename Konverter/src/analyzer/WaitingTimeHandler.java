package analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.core.api.experimental.events.AgentWaitingForPtEvent;
import org.matsim.core.api.experimental.events.handler.AgentWaitingForPtEventHandler;

public class WaitingTimeHandler implements  AgentWaitingForPtEventHandler, PersonEntersVehicleEventHandler{
	
	Map<String,ArrayList<Double>> start = new HashMap<String,ArrayList<Double>>();
	Map<String,ArrayList<Double>> end = new HashMap<String,ArrayList<Double>>();

	@Override
	public void reset(int iteration) {
		
	}

	@Override
	public void handleEvent(AgentWaitingForPtEvent event) {						
//		if(waitingTime.containsKey(event.agentId.toString())){
//			Double id_waitingTime = waitingTime.get(event.agentId.toString()) + event.getTime();
//			waitingTime.replace(event.agentId.toString(), id_waitingTime);
//		}else{
//			waitingTime.put(event.agentId.toString(), 0.0);
//		}
		
		if(start.containsKey(event.getPersonId().toString())){
			ArrayList<Double> newList = start.get(event.getPersonId().toString());
			newList.add(event.getTime());
			start.put(event.getPersonId().toString(), newList);
		}else{
			ArrayList<Double> newList = new ArrayList<Double>();
			newList.add(event.getTime());
			start.put(event.getPersonId().toString(), newList);
		}
	}
	

	@Override
	public void handleEvent(PersonEntersVehicleEvent event) {
		
		if(end.containsKey(event.getPersonId().toString())){
			ArrayList<Double> newList = end.get(event.getPersonId().toString());
			newList.add(event.getTime());
			end.put(event.getPersonId().toString(), newList);
		}else{
			ArrayList<Double> newList = new ArrayList<Double>();
			newList.add(event.getTime());
			end.put(event.getPersonId().toString(), newList);
		}
	
	}

	
	public Integer getWaitingTime(String agent){
		Double waiting = 0.0;
		Double timestamp1 = 0.0;
		Double timestamp2 = 0.0;
		
		if(start.containsKey(agent) & end.containsKey(agent)){
			if(start.get(agent).size() == end.get(agent).size()){
				for(Double time : start.get(agent)){
					
					timestamp1 = time;
					timestamp2 = end.get(agent).remove(0);
					waiting += timestamp2 - timestamp1;
				}
			}
		}
			
		return waiting.intValue();
	}
}
