package analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


public class Route {
	
	public ArrayList<ArrayList<String>> create(String path) throws IOException{

			
		ArrayList<ArrayList<String>> all_routes = new ArrayList<ArrayList<String>>();
	
		Document doc = null; 
	
	    File data = new File(path);
	    
        try {
        	// Das Dokument erstellen 
            SAXBuilder builder = new SAXBuilder(); 
			doc = builder.build(data);
			 
			Element element = doc.getRootElement(); 
			
			List person = (List) element.getChildren(); 
		
			
			for(int i=0;i<person.size();i++){
				
				ArrayList<String> one_route = new ArrayList<String>();
				
				List plan = (List) ((Element) person.get(i)).getChildren();
						
				List actions = (List) ((Element) plan.get(0)).getChildren();
				
				for(int j=1;j<actions.size();j+=2){
					if(((Element) actions.get(j)).getAttributeValue("mode").equals("pt")){
						
						List route = (List) ((Element) actions.get(j)).getChildren();
						
						String transition = ((Element) route.get(0)).getValue();
						
						String[] used_route = transition.split("===");
						
						String[] used_route_spez = used_route[3].split("_");
						
						one_route.add(used_route[2]);
						one_route.add(used_route[2] + "_" + used_route_spez[0]);
						
						
					}
				}

				
				all_routes.add(one_route);
				
			}
			
			
			
        } catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return all_routes;
	}

}
