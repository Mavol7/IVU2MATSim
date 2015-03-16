package konverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class vehicleTransit {
	
	public void create(String export, String output) throws IOException {

		FileReader ft = new FileReader(export + "Fahrzeugtypen.trd");
		BufferedReader br_ft = new BufferedReader(ft);

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(new File(output
					+ "transitVehicles.xml"));

			Document doc = docBuilder.newDocument(); // Wurzelelement definieren

			Element rootElement = doc.createElement("vehicleDefinitions");
			doc.appendChild(rootElement);

			Attr attr_vehicleDef1 = doc.createAttribute("xmlns");
			attr_vehicleDef1.setValue("http://www.matsim.org/files/dtd");
			rootElement.setAttributeNode(attr_vehicleDef1);

			Attr attr_vehicleDef2 = doc.createAttribute("xmlns:xsi");
			attr_vehicleDef2
					.setValue("http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttributeNode(attr_vehicleDef2);

			Attr attr_vehicleDef3 = doc.createAttribute("xsi:schemaLocation");
			attr_vehicleDef3
					.setValue("http://www.matsim.org/files/dtd http://www.matsim.org/files/dtd/vehicleDefinitions_v1.0.xsd");
			rootElement.setAttributeNode(attr_vehicleDef3);

			String zeile_ft = "";

			while ((zeile_ft = br_ft.readLine()) != null) {
				String[] segs_ft = zeile_ft.split("\"");

				if (segs_ft[1].equals("1.0")) {
					continue;
				}

				Element vehicleType = doc.createElement("vehicleType"); // vehicleType
																		// definieren
				rootElement.appendChild(vehicleType);

				Attr attr_vehicleType = doc.createAttribute("id");
				attr_vehicleType.setValue(segs_ft[1]);
				vehicleType.setAttributeNode(attr_vehicleType);

				Element description = doc.createElement("description");
				description.appendChild(doc.createTextNode(segs_ft[5]));
				vehicleType.appendChild(description);

				Element capacity = doc.createElement("capacity"); // capacity
																	// definieren
				vehicleType.appendChild(capacity);

				Element seats = doc.createElement("seats");
				capacity.appendChild(seats);

				Attr attr_seats = doc.createAttribute("persons");
				attr_seats.setValue(segs_ft[11]);
				seats.setAttributeNode(attr_seats);

				Element standingRoom = doc.createElement("standingRoom");
				capacity.appendChild(standingRoom);

				Attr attr_standingRoom = doc.createAttribute("persons");
				attr_standingRoom.setValue(segs_ft[13]);
				standingRoom.setAttributeNode(attr_standingRoom);

				Element length = doc.createElement("length"); // length
																// definieren
				vehicleType.appendChild(length);

				Attr attr_length = doc.createAttribute("meter");
				attr_length.setValue(segs_ft[9]);
				length.setAttributeNode(attr_length);

				FileReader wa = new FileReader(export + "Wagenumlaeufe.trd");
				BufferedReader br_wa = new BufferedReader(wa);

				String zeile_wa = "";

				while ((zeile_wa = br_wa.readLine()) != null) {

					String[] segs_wa = zeile_wa.split("\"");

					if (segs_wa[1].equals("1.0")) {
						continue;
					}

					if (segs_wa[7].equals(segs_ft[1])) {
						Element vehicle = doc.createElement("vehicle"); // vehicles
																		// definieren
						rootElement.appendChild(vehicle);

						Attr attr_vehicle1 = doc.createAttribute("id");
						attr_vehicle1.setValue(segs_wa[1] + "_" + segs_wa[3]
								+ "_" + segs_wa[5]);
						vehicle.setAttributeNode(attr_vehicle1);

						Attr attr_vehicle2 = doc.createAttribute("type");
						attr_vehicle2.setValue(segs_wa[7]);
						vehicle.setAttributeNode(attr_vehicle2);

					}
				}

				br_wa.close();

				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);

			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

		br_ft.close();

	}

}
