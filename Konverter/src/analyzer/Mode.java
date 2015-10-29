package analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


public class Mode {
	
	public ArrayList<ArrayList<String>> create(String path) throws IOException{

		
		ArrayList<ArrayList<String>> all_modes = new ArrayList<ArrayList<String>>();
	
		Document doc = null; 
	
	    File data = new File(path);
	    
        try {
        	// Das Dokument erstellen 
            SAXBuilder builder = new SAXBuilder(); 
			doc = builder.build(data);
			 
			Element element = doc.getRootElement(); 
			
			List person = (List) element.getChildren(); 
		
			
			for(int i=0;i<person.size();i++){
				
				ArrayList<String> one_mode = new ArrayList<String>();
				
				List plan = (List) ((Element) person.get(i)).getChildren();
				
				List actions = (List) ((Element) plan.get(0)).getChildren();
				
				for(int j=1;j<actions.size();j+=2){
					one_mode.add(((Element) actions.get(j)).getAttributeValue("mode"));
				}
				
				all_modes.add(one_mode);
			}
			
			
        } catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return all_modes;
	}
}
