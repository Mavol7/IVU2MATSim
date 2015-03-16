package konverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class network {

	
	public void create(String export, String output) throws IOException {

		FileReader bb = new FileReader(export + "Betriebsbereiche.trd");
		BufferedReader br_bb = new BufferedReader(bb);

		ArrayList<String> list_bb = new ArrayList<String>();

		String zeile_bb = "";
		while ((zeile_bb = br_bb.readLine()) != null) {
			String[] segs_bb = zeile_bb.split("\"");

			if (segs_bb[1].equals("1.0")) {
				continue;
			}

			list_bb.add(segs_bb[1] + "_" + segs_bb[5]);

		}

		br_bb.close();

		FileReader or = new FileReader(export + "Orte.trd");
		BufferedReader br_or = new BufferedReader(or);

		FileReader vb = new FileReader(export + "Verbindungen.trd");
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

		FileReader fwv = new FileReader(export + "Fahrwegverlaeufe.trd");
		BufferedReader br_fwv = new BufferedReader(fwv);

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(new File(output
					+ "network.xml"));

			Document doc = docBuilder.newDocument(); // Wurzelelement definieren
			Element network = doc.createElement("network");
			doc.appendChild(network);

			Attr attr_network = doc.createAttribute("name");
			attr_network.setValue("Netzwerk");
			network.setAttributeNode(attr_network);

			Element nodes = doc.createElement("nodes"); // nodes definieren
			network.appendChild(nodes);

			Element links = doc.createElement("links"); // links definieren
			network.appendChild(links);

			Attr attr_links = doc.createAttribute("capperiod");
			attr_links.setValue("1:00:00");
			links.setAttributeNode(attr_links);

			String zeile_or = "";
			// String last = "";

			while ((zeile_or = br_or.readLine()) != null) {
				String[] segs_or = zeile_or.split("\"");
				if (segs_or[1].equals("1.0")) {
					continue;
				}

				// if(segs_or[3].equals("1")){
				// FileReader hp = new
				// FileReader("D:\\Uni\\Informatik\\Semester 9\\Bachelorarbeit\\IVU.fleet_HCR\\Haltepunkte.trd");
				// BufferedReader br_hp = new BufferedReader(hp);
				// String zeile_hp = "";
				// while( (zeile_hp = br_hp.readLine()) != null ){
				// String[] segs_hp = zeile_hp.split("\"");
				// if(segs_hp[1].equals("1.0")){
				// continue;
				// }

				// if(segs_or[1].equals(segs_hp[1])){
				// FileReader hs = new
				// FileReader("D:\\Uni\\Informatik\\Semester 9\\Bachelorarbeit\\IVU.fleet_HCR\\Haltestellen.trd");
				// BufferedReader br_hs = new BufferedReader(hs);
				// String zeile_hs = "";
				// while( (zeile_hs = br_hs.readLine()) != null ){
				// String[] segs_hs = zeile_hs.split("\"");
				//
				// if(segs_hs[1].equals("1.0")){
				// continue;
				// }
				//
				//
				// if(segs_hp[5].equals(segs_hs[1])){

				// if(segs_hs[1].equals(last)){
				// continue;
				// }

				// if(segs_hp[5].equals(last)){
				// continue;
				// }
				//
				// Element node = doc.createElement("node");
				// nodes.appendChild(node);
				//
				// Attr attr_node1 = doc.createAttribute("id");
				// attr_node1.setValue(segs_hp[5]);
				// node.setAttributeNode(attr_node1);
				//
				// Attr attr_node2 = doc.createAttribute("x");
				// attr_node2.setValue(segs_or[9]);
				// node.setAttributeNode(attr_node2);
				//
				//
				// Attr attr_node3 = doc.createAttribute("y");
				// attr_node3.setValue(segs_or[11]);
				// node.setAttributeNode(attr_node3);
				//
				// System.out.println(segs_hp[5]+"_"+segs_or[9]+"_"+segs_or[11]);
				//
				// last = segs_hp[5];
				//
				// }

				// }
				// br_hs.close();
				//
				// }
				// }
				// br_hp.close();
				//
				//
				// }else{
				// if(segs_or[1].equals(last)){
				// continue;
				// }
				
				if(!segs_or[3].equals("2")){

					Element node = doc.createElement("node");
					nodes.appendChild(node);
	
					Attr attr_node1 = doc.createAttribute("id");
					attr_node1.setValue(segs_or[1] + "_" + segs_or[3]);
					node.setAttributeNode(attr_node1);
	
					Attr attr_node2 = doc.createAttribute("x");
					attr_node2.setValue(segs_or[9]);
					node.setAttributeNode(attr_node2);
	
					Attr attr_node3 = doc.createAttribute("y");
					attr_node3.setValue(segs_or[11]);
					node.setAttributeNode(attr_node3);
	
					System.out.println(segs_or[1] + "_" + segs_or[9] + "_"
							+ segs_or[11]);
				}

				// last = segs_or[1];
			}

			// }
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
				
				if(!pre_segs_fwv[9].equals("2") & !segs_fwv[9].equals("2")){

				// String from = "";
				// String to = "";

				if (pre_segs_fwv[3].equals(segs_fwv[3])) {

					
					for (int k = 0; k < list_vb.size(); k++) {

						if (list_vb.get(k).get(1).equals("1.0")) {
							continue;
						}

						if (pre_segs_fwv[7].equals(list_vb.get(k).get(1))
								& segs_fwv[7].equals(list_vb.get(k).get(5))) {
							FileReader li = new FileReader(export
									+ "Linien.trd");
							BufferedReader br_li = new BufferedReader(li);
							String zeile_li = "";
							while ((zeile_li = br_li.readLine()) != null) {
								String[] segs_li = zeile_li.split("\"");

								if (segs_li[1].equals("1.0")) {
									continue;
								}

								if (segs_fwv[1].equals(segs_li[1])) {
									// if(segs_fwv[9].equals("1")){
									// FileReader hp2 = new
									// FileReader("D:\\Uni\\Informatik\\Semester 9\\Bachelorarbeit\\IVU.fleet_HCR\\Haltepunkte.trd");
									// BufferedReader br_hp2 = new
									// BufferedReader(hp2);
									// String zeile_hp2 = "";
									// while( (zeile_hp2 = br_hp2.readLine()) !=
									// null ){
									// String[] segs_hp2 =
									// zeile_hp2.split("\"");
									// if(segs_hp2[1].equals("1.0")){
									// continue;
									// }
									//
									// if(segs_fwv[7].equals(segs_hp2[1])){
									// to = segs_hp2[5];
									// }
									// }
									// br_hp2.close();
									// }else{
									// to = segs_fwv[7];
									// }
									//
									//
									// if(pre_segs_fwv[9].equals("1")){
									// FileReader hp1 = new
									// FileReader("D:\\Uni\\Informatik\\Semester 9\\Bachelorarbeit\\IVU.fleet_HCR\\Haltepunkte.trd");
									// BufferedReader br_hp1 = new
									// BufferedReader(hp1);
									// String zeile_hp1 = "";
									// while( (zeile_hp1 = br_hp1.readLine()) !=
									// null ){
									// String[] segs_hp1 =
									// zeile_hp1.split("\"");
									// if(segs_hp1[1].equals("1.0")){
									// continue;
									// }
									//
									// if(pre_segs_fwv[7].equals(segs_hp1[1])){
									// from = segs_hp1[5];
									// }
									// }
									// br_hp1.close();
									// }else{
									// from = pre_segs_fwv[7];
									// }

									Element link = doc.createElement("link");
									links.appendChild(link);

									Attr attr_link1 = doc.createAttribute("id");
									attr_link1.setValue("R_" + pre_segs_fwv[7]
											+ "_" + segs_fwv[7]);
									link.setAttributeNode(attr_link1);

									Attr attr_link2 = doc
											.createAttribute("from");
									attr_link2.setValue(pre_segs_fwv[7] + "_"
											+ pre_segs_fwv[9]);
									link.setAttributeNode(attr_link2);

									Attr attr_link3 = doc.createAttribute("to");
									attr_link3.setValue(segs_fwv[7] + "_"
											+ segs_fwv[9]);
									link.setAttributeNode(attr_link3);

									Attr attr_link4 = doc
											.createAttribute("length");
									attr_link4.setValue(list_vb.get(k).get(9));
									link.setAttributeNode(attr_link4);

									Attr attr_link5 = doc
											.createAttribute("capacity");
									attr_link5.setValue("2000");
									link.setAttributeNode(attr_link5);

									Attr attr_link6 = doc
											.createAttribute("freespeed");
									attr_link6.setValue("100");
									link.setAttributeNode(attr_link6);

									String bb_line = "";

									for (int i = 0; i < list_bb.size(); i++) {
										String[] segs_bb = list_bb.get(i)
												.split("_");
										if (segs_bb[0].equals(segs_li[3])) {
											bb_line = segs_bb[1];
										}
									}

									Attr attr_link7 = doc
											.createAttribute("modes");
									attr_link7.setValue(bb_line);
									link.setAttributeNode(attr_link7);

									Attr attr_link8 = doc
											.createAttribute("permlanes");
									attr_link8.setValue("2");
									link.setAttributeNode(attr_link8);
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
						FileReader li = new FileReader(export + "Linien.trd");
						BufferedReader br_li = new BufferedReader(li);
						String zeile_li = "";
						while ((zeile_li = br_li.readLine()) != null) {
							String[] segs_li = zeile_li.split("\"");

							if (segs_li[1].equals("1.0")) {
								continue;
							}

							Element link = doc.createElement("link");
							links.appendChild(link);

							Attr attr_link1 = doc.createAttribute("id");
							attr_link1
									.setValue("R_" + pre_segs_fwv[7] + "_END");
							link.setAttributeNode(attr_link1);

							Attr attr_link2 = doc.createAttribute("from");
							attr_link2.setValue(pre_segs_fwv[7] + "_"
									+ pre_segs_fwv[9]);
							link.setAttributeNode(attr_link2);

							Attr attr_link3 = doc.createAttribute("to");
							attr_link3.setValue(pre_segs_fwv[7] + "_"
									+ pre_segs_fwv[9]);
							link.setAttributeNode(attr_link3);

							Attr attr_link4 = doc.createAttribute("length");
							attr_link4.setValue("0");
							link.setAttributeNode(attr_link4);

							Attr attr_link5 = doc.createAttribute("capacity");
							attr_link5.setValue("2000");
							link.setAttributeNode(attr_link5);

							Attr attr_link6 = doc.createAttribute("freespeed");
							attr_link6.setValue("100");
							link.setAttributeNode(attr_link6);

							String bb_line = "";

							for (int i = 0; i < list_bb.size(); i++) {
								String[] segs_bb = list_bb.get(i).split("_");
								if (segs_bb[0].equals(segs_li[3])) {
									bb_line = segs_bb[1];
								}
							}

							Attr attr_link7 = doc.createAttribute("modes");
							attr_link7.setValue(bb_line);
							link.setAttributeNode(attr_link7);

							Attr attr_link8 = doc.createAttribute("permlanes");
							attr_link8.setValue("2");
							link.setAttributeNode(attr_link8);

							mode = bb_line;

							break;

						}

						br_li.close();
						used_end.add(pre_segs_fwv[7]);
					}
				}
				
				}
				
				pre_segs_fwv = segs_fwv;
				

			}

			br_fwv.close();

			if (!used_end.contains(pre_segs_fwv[7])) {
				Element link = doc.createElement("link");
				links.appendChild(link);

				Attr attr_link1 = doc.createAttribute("id");
				attr_link1.setValue("R_" + pre_segs_fwv[7] + "_END");
				link.setAttributeNode(attr_link1);

				Attr attr_link2 = doc.createAttribute("from");
				attr_link2.setValue(pre_segs_fwv[7] + "_" + pre_segs_fwv[9]);
				link.setAttributeNode(attr_link2);

				Attr attr_link3 = doc.createAttribute("to");
				attr_link3.setValue(pre_segs_fwv[7] + "_" + pre_segs_fwv[9]);
				link.setAttributeNode(attr_link3);

				Attr attr_link4 = doc.createAttribute("length");
				attr_link4.setValue("0");
				link.setAttributeNode(attr_link4);

				Attr attr_link5 = doc.createAttribute("capacity");
				attr_link5.setValue("2000");
				link.setAttributeNode(attr_link5);

				Attr attr_link6 = doc.createAttribute("freespeed");
				attr_link6.setValue("100");
				link.setAttributeNode(attr_link6);

				Attr attr_link7 = doc.createAttribute("modes");
				attr_link7.setValue(mode);
				link.setAttributeNode(attr_link7);

				Attr attr_link8 = doc.createAttribute("permlanes");
				attr_link8.setValue("2");
				link.setAttributeNode(attr_link8);
			}

			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
					"http://www.matsim.org/files/dtd/network_v1.dtd");

			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}
}
