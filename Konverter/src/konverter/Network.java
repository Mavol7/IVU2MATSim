package konverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordImpl;
import org.matsim.core.utils.geometry.transformations.WGS84toCH1903LV03;


public class Network {

	
	public void create(String input) throws IOException {
		
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.loadConfig("input/config.xml"));
		
		new MatsimNetworkReader(scenario).readFile("input/networkWithoutTransit.xml");
		
		NetworkFactory nfac = scenario.getNetwork().getFactory();
	
		//////////////////////////////////

		FileReader bb = new FileReader(input + "Betriebsbereiche.trd");
		BufferedReader br_bb = new BufferedReader(bb);

		ArrayList<String> list_bb = new ArrayList<String>();

		String zeile_bb = "";
		while ((zeile_bb = br_bb.readLine()) != null) {
			String[] segs_bb = zeile_bb.split("\"");

			if (segs_bb[1].equals("1.0")) {
				continue;
			}

			list_bb.add(segs_bb[1] + "_" + segs_bb[3]);

		}

		br_bb.close();

		FileReader or = new FileReader(input + "Orte.trd");
		BufferedReader br_or = new BufferedReader(or);

		FileReader vb = new FileReader(input + "Verbindungen.trd");
		BufferedReader br_vb = new BufferedReader(vb);

		ArrayList<ArrayList<String>> list_vb = new ArrayList<ArrayList<String>>();

		String zeile_vb = "";
		while ((zeile_vb = br_vb.readLine()) != null) {
			String[] segs_vb = zeile_vb.split("\"");

			if (segs_vb[1].equals("1.0")) {
				continue;
			}

			ArrayList<String> list_vb_one = new ArrayList<String>();

			for (int i = 0; i < segs_vb.length; i++) {
				list_vb_one.add(segs_vb[i]);
			}

			list_vb.add(list_vb_one);

		}

		br_vb.close();

		FileReader fwv = new FileReader(input + "Fahrwegverlaeufe.trd");
		BufferedReader br_fwv = new BufferedReader(fwv);

			String zeile_or = "";

			while ((zeile_or = br_or.readLine()) != null) {
				String[] segs_or = zeile_or.split("\"");
				if (segs_or[1].equals("1.0")) {
					continue;
				}
				
//				if(!segs_or[3].equals("2")){

					Id<Node> id_n = Id.createNodeId(segs_or[1] + "_" + segs_or[3]);
					
					Node node = nfac.createNode(id_n, new WGS84toCH1903LV03().transform(new CoordImpl(coord_convert(segs_or[9]), coord_convert(segs_or[11]))));
					
					scenario.getNetwork().addNode(node);
//				}

			}

			br_or.close();

			String zeile_fwv = "";
			String[] pre_segs_fwv = new String[1];
			String mode = "";
			ArrayList<String> used_end = new ArrayList<String>();

			while ((zeile_fwv = br_fwv.readLine()) != null) {
				String[] segs_fwv = zeile_fwv.split("\"");

				if (pre_segs_fwv.length == 1) {
					pre_segs_fwv = segs_fwv;
					continue;
				}

				if (pre_segs_fwv[1].equals("1.0") | segs_fwv[1].equals("1.0")) {
					pre_segs_fwv = segs_fwv;
					continue;
				}
				
//				if(!pre_segs_fwv[9].equals("2") & !segs_fwv[9].equals("2")){


				if (pre_segs_fwv[3].equals(segs_fwv[3])) {

					
					for (int k = 0; k < list_vb.size(); k++) {

						if (list_vb.get(k).get(1).equals("1.0")) {
							continue;
						}

						if (pre_segs_fwv[7].equals(list_vb.get(k).get(1))
								& segs_fwv[7].equals(list_vb.get(k).get(5))) {
							FileReader li = new FileReader(input
									+ "Linien.trd");
							BufferedReader br_li = new BufferedReader(li);
							String zeile_li = "";
							while ((zeile_li = br_li.readLine()) != null) {
								String[] segs_li = zeile_li.split("\"");

								if (segs_li[1].equals("1.0")) {
									continue;
								}
						

								if (segs_fwv[1].equals(segs_li[1])) {
									
									Id<Link> id_l = Id.create("R_" + pre_segs_fwv[7]+ "_" + segs_fwv[7], Link.class);
									
									Node f_node = null;
									Node t_node = null;
									
									for (Node node : scenario.getNetwork().getNodes().values()){
										if(node.getId().toString().equals(pre_segs_fwv[7] + "_"+ pre_segs_fwv[9])){
											f_node = node;
											break;
										}
									}
									
									for (Node node : scenario.getNetwork().getNodes().values()){
										if(node.getId().toString().equals(segs_fwv[7] + "_"+ segs_fwv[9])){
											t_node = node;
											break;
										}									
									}
									
									Link link = nfac.createLink(id_l, f_node, t_node);
									
									String bb_line = "";

									for (int i = 0; i < list_bb.size(); i++) {
										String[] segs_bb = list_bb.get(i)
												.split("_");
										if (segs_bb[0].equals(segs_li[3])) {
											bb_line = segs_bb[1];
										}
									}

									
									Set<String> bbl = new HashSet<String>();
									bbl.add(bb_line);
									bbl.add("car");
									link.setAllowedModes(bbl);
									
									link.setLength(Double.parseDouble(list_vb.get(k).get(9)));
									
									link.setCapacity(1500);
									
									link.setFreespeed(50/3.6);
									
									scenario.getNetwork().addLink(link);

								}

							}

							br_li.close();

							list_vb.remove(k);
							break;
						}
					}

					br_vb.close();

				} else {
					if (!used_end.contains(pre_segs_fwv[7])) {
						FileReader li = new FileReader(input + "Linien.trd");
						BufferedReader br_li = new BufferedReader(li);
						String zeile_li = "";
						while ((zeile_li = br_li.readLine()) != null) {
							String[] segs_li = zeile_li.split("\"");

							if (segs_li[1].equals("1.0")) {
								continue;
							}
							
							Id<Link> id_l = Id.create("R_" + pre_segs_fwv[7] + "_END", Link.class);
							
							Node f_node = null;
							Node t_node = null;
							
							for (Node node : scenario.getNetwork().getNodes().values()){
								if(node.getId().toString().equals(pre_segs_fwv[7] + "_"+ pre_segs_fwv[9])){
									f_node = node;
									t_node = node;
									break;
								}
							}
							
							Link link = nfac.createLink(id_l, f_node, t_node);
							
							String bb_line = "";

							for (int i = 0; i < list_bb.size(); i++) {
								String[] segs_bb = list_bb.get(i)
										.split("_");
								if (segs_bb[0].equals(segs_li[3])) {
									bb_line = segs_bb[1];
								}
							}

							
							Set<String> bbl = new HashSet<String>();
							bbl.add(bb_line);
							bbl.add("car");
							link.setAllowedModes(bbl);
							
							link.setLength(0);
							
							link.setCapacity(1500);
							
							link.setFreespeed(50/3.6);
							
							scenario.getNetwork().addLink(link);
							
							mode = bb_line;

							break;

						}

						br_li.close();
						used_end.add(pre_segs_fwv[7]);
					}
				}
				
//				}
				
				pre_segs_fwv = segs_fwv;
				

			}

			br_fwv.close();

			if (!used_end.contains(pre_segs_fwv[7])) {
				
//				if(!pre_segs_fwv[9].equals("2")){
				
				Id<Link> id_l = Id.create("R_" + pre_segs_fwv[7] + "_END", Link.class);
				
				Node f_node = null;
				Node t_node = null;
				
				for (Node node : scenario.getNetwork().getNodes().values()){
					if(node.getId().toString().equals(pre_segs_fwv[7] + "_" + pre_segs_fwv[9])){
						f_node = node;						
						t_node = node;
						break;
					}
				}
				
				Link link = nfac.createLink(id_l, f_node, t_node);
				
				
				Set<String> bbl = new HashSet<String>();
				bbl.add(mode);
				bbl.add("car");
				link.setAllowedModes(bbl);
				
				link.setLength(0);
				
				link.setCapacity(1500);
				
				link.setFreespeed(50/3.6);
				
				scenario.getNetwork().addLink(link);
		

//			}
			
			}
			
			FileReader vb2 = new FileReader(input + "Verbindungen.trd");
			BufferedReader br_vb2 = new BufferedReader(vb2);


			String zeile_vb2 = "";
			while ((zeile_vb2 = br_vb2.readLine()) != null) {
				String[] segs_vb2 = zeile_vb2.split("\"");

				if (segs_vb2[1].equals("1.0")) {
					continue;
				}
				
				if(!segs_vb2[3].equals("2") | !segs_vb2[7].equals("2")){	
					boolean alr_used = false;
					for(Link link : scenario.getNetwork().getLinks().values()){
						if(link.getId().toString().equals("R_" + segs_vb2[1] + "_" + segs_vb2[5])){
							alr_used = true;
							break;
						}
					}
					
					if(!alr_used){
						Id<Link> id_l = Id.create("R_" + segs_vb2[1] + "_" + segs_vb2[5], Link.class);
						
						Node f_node = null;
						Node t_node = null;
						
						for (Node node : scenario.getNetwork().getNodes().values()){
							if(node.getId().toString().equals(segs_vb2[1] + "_" + segs_vb2[3])){					
								f_node = node;
								break;
							}
						}
						
						for (Node node : scenario.getNetwork().getNodes().values()){
							if(node.getId().toString().equals(segs_vb2[5] + "_" + segs_vb2[7])){						
								t_node = node;
								break;
							}
						}
						
						Link link = nfac.createLink(id_l, f_node, t_node);
						
						
						Set<String> bbl = new HashSet<String>();
						bbl.add(mode);
						bbl.add("car");
						link.setAllowedModes(bbl);
						
						link.setLength(Double.parseDouble(segs_vb2[9]));
						
						link.setCapacity(1500);
						
						link.setFreespeed(50/3.6);
						
						scenario.getNetwork().addLink(link);
					}
				}
				
			}
			br_vb2.close();
				
			//////////////////////////////////
				
			new NetworkWriter(scenario.getNetwork()).write("output_konverter/Planfall1/network.xml");
	}
	
	//-------------------------------------------------------------------------------
	
	public Double coord_convert(String coord){
		
		Double grad = Double.parseDouble((coord.substring(0, 3)));
		Double min = Double.parseDouble(coord.substring(3, 5))/60;
		Double sek = Double.parseDouble(coord.substring(5, 7))/3600;
		Double rest = Double.parseDouble(coord.substring(7, 10))/3600000;
		
		Double result = grad + min + sek +rest;
		
		return result;
	}
}
