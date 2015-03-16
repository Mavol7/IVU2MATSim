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

public class transitSchedule {
	
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

		FileReader fhz = new FileReader(export + "Fahrthaltezeiten.trd");
		BufferedReader br_fhz = new BufferedReader(fhz);

		ArrayList<ArrayList<String>> list_fhz = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> list_fhz2 = new ArrayList<ArrayList<String>>();

		String zeile_fhz = "";
		while ((zeile_fhz = br_fhz.readLine()) != null) {
			String[] segs_fhz = zeile_fhz.split("\"");

			if (segs_fhz[1].equals("1.0")) {
				continue;
			}

			ArrayList<String> list_fhz_one = new ArrayList<String>();

			for (int i = 0; i < segs_fhz.length; i++) {
				list_fhz_one.add(segs_fhz[i]);
			}

			list_fhz.add(list_fhz_one);
			list_fhz2.add(list_fhz_one);

		}

		br_fhz.close();

		FileReader fa = new FileReader(export + "Fahrten.trd");
		BufferedReader br_fa = new BufferedReader(fa);

		ArrayList<ArrayList<String>> list_fa = new ArrayList<ArrayList<String>>();

		String zeile_fa = "";
		while ((zeile_fa = br_fa.readLine()) != null) {
			String[] segs_fa = zeile_fa.split("\"");

			if (segs_fa[1].equals("1.0")) {
				continue;
			}

			ArrayList<String> list_fa_one = new ArrayList<String>();

			for (int i = 0; i < segs_fa.length; i++) {
				list_fa_one.add(segs_fa[i]);
			}

			list_fa.add(list_fa_one);

		}

		br_fa.close();

		FileReader li = new FileReader(export + "Linien.trd");
		BufferedReader br_li = new BufferedReader(li);

		FileReader fwv1 = new FileReader(export + "Fahrwegverlaeufe.trd");
		BufferedReader br_fwv1 = new BufferedReader(fwv1);

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(new File(output
					+ "transitSchedule.xml"));

			Document doc = docBuilder.newDocument(); // Wurzelelement definieren
			Element transitSchedule = doc.createElement("transitSchedule");
			doc.appendChild(transitSchedule);

			// transitStops

			Element transitStops = doc.createElement("transitStops");
			transitSchedule.appendChild(transitStops);

			String zeile_fwv1 = "";
			String[] pre_segs_fwv1 = { "0", "1.0" };
			String x = "";
			String y = "";
			boolean empty = false;
			while ((zeile_fwv1 = br_fwv1.readLine()) != null) {
				String[] segs_fwv1 = zeile_fwv1.split("\"");

				if (segs_fwv1[1].equals("1.0") | pre_segs_fwv1[1].equals("1.0")) {
					pre_segs_fwv1 = segs_fwv1;
					continue;
				}
				
				for(int i=0;i<list_fa.size();i++){
					if(pre_segs_fwv1[1].equals(list_fa.get(i).get(9)) & pre_segs_fwv1[3].equals(list_fa.get(i).get(13))){
						if(!(list_fa.get(i).get(19).equals("0"))){
							empty = true;
							break;
						}else{
							break;
						}
					}
				}
				
				if(empty){
					empty = false;
					pre_segs_fwv1 = segs_fwv1;
					continue;
				}

				FileReader or = new FileReader(export + "Orte.trd");
				BufferedReader br_or = new BufferedReader(or);
				String zeile_or = "";

				while ((zeile_or = br_or.readLine()) != null) {
					String[] segs_or = zeile_or.split("\"");

					if (segs_or[1].equals("1.0")) {
						continue;
					}

					if (pre_segs_fwv1[7].equals(segs_or[1])
							& pre_segs_fwv1[9].equals("1")) {
						Element stopFacility = doc
								.createElement("stopFacility");
						transitStops.appendChild(stopFacility);

						Attr attr_stopFacility1 = doc.createAttribute("id");
						attr_stopFacility1.setValue("SF" + "_"
								+ pre_segs_fwv1[7] + "_" + pre_segs_fwv1[1]
								+ "_" + pre_segs_fwv1[3] + "_"
								+ pre_segs_fwv1[5]);
						stopFacility.setAttributeNode(attr_stopFacility1);

						Attr attr_stopFacility2 = doc.createAttribute("x");
						attr_stopFacility2.setValue(segs_or[9]);
						stopFacility.setAttributeNode(attr_stopFacility2);

						Attr attr_stopFacility3 = doc.createAttribute("y");
						attr_stopFacility3.setValue(segs_or[11]);
						stopFacility.setAttributeNode(attr_stopFacility3);

						if (!segs_fwv1[5].equals("1")) {
							Attr attr_stopFacility4 = doc
									.createAttribute("linkRefId");
							attr_stopFacility4.setValue("R_" + pre_segs_fwv1[7]
									+ "_" + segs_fwv1[7]);
							stopFacility.setAttributeNode(attr_stopFacility4);
						} else {
							Attr attr_stopFacility4 = doc
									.createAttribute("linkRefId");
							attr_stopFacility4.setValue("R_" + pre_segs_fwv1[7]
									+ "_END");
							stopFacility.setAttributeNode(attr_stopFacility4);
						}

						x = segs_or[9];
						y = segs_or[11];
					}
				}

				br_or.close();

				pre_segs_fwv1 = segs_fwv1;
			}

			br_fwv1.close();

			Element stopFacility = doc.createElement("stopFacility");
			transitStops.appendChild(stopFacility);

			Attr attr_stopFacility1 = doc.createAttribute("id");
			attr_stopFacility1.setValue("SF" + "_" + pre_segs_fwv1[7] + "_"
					+ pre_segs_fwv1[1] + "_" + pre_segs_fwv1[3] + "_"
					+ pre_segs_fwv1[5]);
			stopFacility.setAttributeNode(attr_stopFacility1);

			Attr attr_stopFacility2 = doc.createAttribute("x");
			attr_stopFacility2.setValue(x);
			stopFacility.setAttributeNode(attr_stopFacility2);

			Attr attr_stopFacility3 = doc.createAttribute("y");
			attr_stopFacility3.setValue(y);
			stopFacility.setAttributeNode(attr_stopFacility3);

			Attr attr_stopFacility4 = doc.createAttribute("linkRefId");
			attr_stopFacility4.setValue("R_" + pre_segs_fwv1[7] + "_END");
			stopFacility.setAttributeNode(attr_stopFacility4);

			// Übergang zu transitLine

			String last_trip = "";
			String zeile_li = "";
			while ((zeile_li = br_li.readLine()) != null) {
				String[] segs_li = zeile_li.split("\"");

				if (segs_li[1].equals("1.0")) {
					continue;
				}

				Element transitLine = doc.createElement("transitLine");
				transitSchedule.appendChild(transitLine);

				Attr attr_transitLine = doc.createAttribute("id");
				attr_transitLine.setValue(segs_li[1]);
				transitLine.setAttributeNode(attr_transitLine);

				System.out.println("Linie: " + segs_li[1]);

				FileReader fw = new FileReader(export + "Fahrwege.trd");
				BufferedReader br_fw = new BufferedReader(fw);

				String zeile_fw = "";
				while ((zeile_fw = br_fw.readLine()) != null) {
					String[] segs_fw = zeile_fw.split("\"");

					if (segs_fw[1].equals("1.0")) {
						continue;
					}

					if (segs_fw[1].equals(segs_li[1])) {

						for (int k = 0; k < list_fhz.size(); k++) {

							if (segs_li[1].equals(list_fhz.get(k).get(7))
									& segs_fw[3].equals(list_fhz.get(k).get(9))) {
								Element transitRoute = doc
										.createElement("transitRoute");
								transitLine.appendChild(transitRoute);

								Attr attr_transitRoute = doc
										.createAttribute("id");
								attr_transitRoute.setValue(segs_fw[3] + "_"
										+ list_fhz.get(k).get(5) + "_"
										+ list_fhz.get(k).get(7) + "_"
										+ list_fhz.get(k).get(3));
								transitRoute
										.setAttributeNode(attr_transitRoute);

								System.out.println("-> Linienvariante: "
										+ segs_fw[3] + "_"
										+ list_fhz.get(k).get(5));

								String bb_line = "";

								for (int i = 0; i < list_bb.size(); i++) {
									String[] segs_bb = list_bb.get(i)
											.split("_");
									if (segs_bb[0].equals(segs_li[3])) {
										bb_line = segs_bb[1];
									}
								}

								Element transportMode = doc
										.createElement("transportMode");
								transportMode.appendChild(doc
										.createTextNode(bb_line));
								transitRoute.appendChild(transportMode);

								System.out.println("--> Betriebsbereich: "
										+ segs_li[3]);

								String current_time = "0";

								Element routeProfile = doc
										.createElement("routeProfile");
								transitRoute.appendChild(routeProfile);

								Element route = doc.createElement("route");
								transitRoute.appendChild(route);

								if (list_fa.isEmpty()) {
									break;
								} else {

									for (int i = 0; i < list_fa.size(); i++) {

										if (list_fa.get(i).get(1).equals("1.0")) {
											continue;
										}

										if (!list_fa.get(i).get(19).equals("0")) {
											continue;
										}

										if (list_fa.get(i).get(1)
												.equals(list_fhz.get(k).get(1))
												& list_fa
														.get(i)
														.get(3)
														.equals(list_fhz.get(k)
																.get(3))
												& list_fa
														.get(i)
														.get(5)
														.equals(list_fhz.get(k)
																.get(5))
												& list_fa
														.get(i)
														.get(9)
														.equals(list_fhz.get(k)
																.get(7))
												& list_fa
														.get(i)
														.get(13)
														.equals(list_fhz.get(k)
																.get(9))) {

											if (!list_fa.get(i).get(19)
													.equals("0")) {
												continue;
											}

											ArrayList<ArrayList<String>> current_fhz = new ArrayList<ArrayList<String>>();

											for (int m = 0; m < list_fhz2
													.size(); m++) {
												ArrayList<String> one_fhz = new ArrayList<String>();
												if (list_fhz
														.get(k)
														.get(1)
														.equals(list_fhz2
																.get(m).get(1))
														& list_fhz
																.get(k)
																.get(3)
																.equals(list_fhz2
																		.get(m)
																		.get(3))
														& list_fhz
																.get(k)
																.get(5)
																.equals(list_fhz2
																		.get(m)
																		.get(5))
														& list_fhz
																.get(k)
																.get(7)
																.equals(list_fhz2
																		.get(m)
																		.get(7))
														& list_fhz
																.get(k)
																.get(9)
																.equals(list_fhz2
																		.get(m)
																		.get(9))) {
													one_fhz.add(list_fhz.get(m)
															.get(13));
													one_fhz.add(list_fhz.get(m)
															.get(15));
													current_fhz.add(one_fhz);
												}
											}

											FileReader wa = new FileReader(
													export
															+ "Wagenumlaeufe.trd");
											BufferedReader br_wa = new BufferedReader(
													wa);

											String zeile_wa = "";

											while ((zeile_wa = br_wa.readLine()) != null) {

												String[] segs_wa = zeile_wa
														.split("\"");

												if (segs_wa[1].equals("1.0")) {
													continue;
												}

												if (list_fa.get(i).get(1)
														.equals(segs_wa[3])
														& list_fa
																.get(i)
																.get(3)
																.equals(segs_wa[1])
												// & list_fa
												// .get(i)
												// .get(39)
												// .equals(segs_wa[7])
												) {
													Element departures = doc
															.createElement("departures");
													transitRoute
															.appendChild(departures);

													Element departure = doc
															.createElement("departure");
													departures
															.appendChild(departure);

													Attr attr_departures1 = doc
															.createAttribute("id");
													attr_departures1
															.setValue(list_fa
																	.get(i)
																	.get(7));
													departure
															.setAttributeNode(attr_departures1);

													Attr attr_departures2 = doc
															.createAttribute("departureTime");
													attr_departures2
															.setValue(umrechner(list_fa
																	.get(i)
																	.get(33)));
													departure
															.setAttributeNode(attr_departures2);

													Attr attr_departures3 = doc
															.createAttribute("vehicleRefId");
													attr_departures3
															.setValue(segs_wa[1]
																	+ "_"
																	+ segs_wa[3]
																	+ "_"
																	+ segs_wa[5]);
													departure
															.setAttributeNode(attr_departures3);
													System.out
															.println("---> departure id: "
																	+ list_fa
																			.get(i)
																			.get(7)
																	+ ", departureTime: "
																	+ umrechner(list_fa
																			.get(i)
																			.get(33))
																	+ ", vehicleRefId: "
																	+ segs_wa[1]
																	+ "_"
																	+ segs_wa[3]
																	+ "_"
																	+ segs_wa[5]);
												}

											}

											br_wa.close();

											// if(last_trip.equals(segs_fa[9]+"_"+segs_fa[13])){
											// continue;
											// }
											// last_trip =
											// segs_fa[9]+"_"+segs_fa[13];

											FileReader fwv = new FileReader(
													export
															+ "Fahrwegverlaeufe.trd");
											BufferedReader br_fwv = new BufferedReader(
													fwv);

											String zeile_fwv = "";
											String[] pre_segs_fwv = new String[1];

											while ((zeile_fwv = br_fwv
													.readLine()) != null) {
												String[] segs_fwv = zeile_fwv
														.split("\"");

												if (segs_fwv[1].equals("1.0")) {
													continue;
												}

												if (segs_fw[1]
														.equals(segs_fwv[1])
														& segs_fw[3]
																.equals(segs_fwv[3])) {
													if (segs_fwv[5]
															.equals(list_fa
																	.get(i)
																	.get(25))) {
														Element stop = doc
																.createElement("stop");
														routeProfile
																.appendChild(stop);

														Attr attr_stop1 = doc
																.createAttribute("refId");
														attr_stop1
																.setValue("SF"
																		+ "_"
																		+ segs_fwv[7]
																		+ "_"
																		+ segs_fwv[1]
																		+ "_"
																		+ segs_fwv[3]
																		+ "_"
																		+ segs_fwv[5]);
														stop.setAttributeNode(attr_stop1);

														Attr attr_stop2 = doc
																.createAttribute("departureOffset");
														attr_stop2
																.setValue(umrechner(current_time));
														stop.setAttributeNode(attr_stop2);

														System.out
																.println("---> refId: "
																		+ segs_fwv[7]
																		+ ", departureOffset: "
																		+ umrechner(current_time));

													} else {

														FileReader vb = new FileReader(
																export
																		+ "Verbindungen.trd");
														BufferedReader br_vb = new BufferedReader(
																vb);
														String zeile_vb = "";

														while ((zeile_vb = br_vb
																.readLine()) != null) {
															String[] segs_vb = zeile_vb
																	.split("\"");

															if (segs_vb[1]
																	.equals("1.0")) {
																continue;
															}

															if (segs_vb[1]
																	.equals(pre_segs_fwv[7])
																	& segs_vb[5]
																			.equals(segs_fwv[7])) {

																Element link = doc
																		.createElement("link");
																route.appendChild(link);

																Attr attr_link = doc
																		.createAttribute("refId");

																if (!segs_fwv[5]
																		.equals(list_fa
																				.get(i)
																				.get(31))) {
																	attr_link
																			.setValue("R_"
																					+ pre_segs_fwv[7]
																					+ "_"
																					+ segs_fwv[7]);
																	link.setAttributeNode(attr_link);

																	System.out
																			.println("----> route: R_"
																					+ pre_segs_fwv[7]
																					+ "_"
																					+ segs_fwv[7]);
																	break;
																} else {

																	attr_link
																			.setValue("R_"
																					+ pre_segs_fwv[7]
																					+ "_"
																					+ segs_fwv[7]);
																	link.setAttributeNode(attr_link);

																	// System.out
																	// .println("----> route: R_"
																	// +
																	// pre_segs_fwv[7]
																	// + "_"
																	// +
																	// segs_fwv[7]);

																	Element link2 = doc
																			.createElement("link");
																	route.appendChild(link2);

																	Attr attr_link2 = doc
																			.createAttribute("refId");

																	attr_link2
																			.setValue("R_"
																					+ segs_fwv[7]
																					+ "_END");
																	link2.setAttributeNode(attr_link2);

																	// System.out
																	// .println("----> route: R_"
																	// +
																	// pre_segs_fwv[7]
																	// + "_"
																	// +
																	// segs_fwv[7]);
																	break;
																}
															}
														}
														br_vb.close();

														FileReader fz = new FileReader(
																export
																		+ "Fahrzeiten.trd");
														BufferedReader br_fz = new BufferedReader(
																fz);
														String zeile_fz = "";
														while ((zeile_fz = br_fz
																.readLine()) != null) {
															String[] segs_fz = zeile_fz
																	.split("\"");

															if (segs_fz[1]
																	.equals("1.0")) {
																continue;
															}

															if (pre_segs_fwv[7]
																	.equals(segs_fz[1])
																	& segs_fwv[7]
																			.equals(segs_fz[5])
																	& list_fa
																			.get(i)
																			.get(15)
																			.equals(segs_fz[9])) {

																current_time = addierer(
																		current_time,
																		segs_fz[11]);

																if (segs_fwv[9]
																		.equals("1")) {
																	Element stop = doc
																			.createElement("stop");
																	routeProfile
																			.appendChild(stop);

																	Attr attr_stop1 = doc
																			.createAttribute("refId");
																	attr_stop1
																			.setValue("SF"
																					+ "_"
																					+ segs_fwv[7]
																					+ "_"
																					+ segs_fwv[1]
																					+ "_"
																					+ segs_fwv[3]
																					+ "_"
																					+ segs_fwv[5]);
																	stop.setAttributeNode(attr_stop1);

																	Attr attr_stop3 = doc
																			.createAttribute("arrivalOffset");
																	attr_stop3
																			.setValue(umrechner(current_time));
																	stop.setAttributeNode(attr_stop3);

																	// if
																	// (list_fhz.get(k).get(13)
																	// .equals(segs_fwv[7]))
																	// {
																	// current_time
																	// =
																	// addierer(
																	// current_time,
																	// list_fhz.get(k).get(15));
																	// // break;
																	// }

																	for (int l = 0; l < current_fhz
																			.size(); l++) {
																		if (current_fhz
																				.get(l)
																				.get(0)
																				.equals(segs_fwv[7])) {
																			current_time = addierer(
																					current_time,
																					current_fhz
																							.get(l)
																							.get(1));
																			break;
																		}
																	}

																	FileReader hphz = new FileReader(
																			export
																					+ "Haltepunkthaltezeiten.trd");
																	BufferedReader br_hphz = new BufferedReader(
																			hphz);
																	String zeile_hphz = "";

																	if (!segs_fwv[5]
																			.equals(list_fa
																					.get(i)
																					.get(31))) {
																		while ((zeile_hphz = br_hphz
																				.readLine()) != null) {
																			String[] segs_hphz = zeile_hphz
																					.split("\"");

																			if (segs_hphz[1]
																					.equals("1.0")) {
																				continue;
																			}

																			if (segs_hphz[1]
																					.equals(segs_fwv[7])
																					& segs_hphz[3]
																							.equals(list_fa
																									.get(i)
																									.get(15))) {
																				current_time = addierer(
																						current_time,
																						segs_hphz[5]);
																				break;
																			}

																		}
																		br_hphz.close();
																		Attr attr_stop2 = doc
																				.createAttribute("departureOffset");
																		attr_stop2
																				.setValue(umrechner(current_time));
																		stop.setAttributeNode(attr_stop2);
																		System.out
																				.println("---> refId: "
																						+ segs_fwv[7]
																						+ ", arrivalOffset: "
																						+ umrechner(current_time)
																						+ ", departureOffset: "
																						+ umrechner(current_time));
																	} else {
																		System.out
																				.println("---> refId: "
																						+ segs_fwv[7]
																						+ ", arrivalOffset: "
																						+ umrechner(current_time));

																	}

																} else {

																	// if
																	// (segs_fhz[13]
																	// .equals(segs_fwv[7]))
																	// {
																	// current_time
																	// =
																	// addierer(
																	// current_time,
																	// segs_fhz[15]);
																	// // break;
																	// }
																	//
																	for (int l = 0; l < current_fhz
																			.size(); l++) {
																		if (current_fhz
																				.get(l)
																				.get(0)
																				.equals(segs_fwv[7])) {
																			current_time = addierer(
																					current_time,
																					current_fhz
																							.get(l)
																							.get(1));
																		}
																	}

																	FileReader hphz = new FileReader(
																			export
																					+ "Haltepunkthaltezeiten.trd");
																	BufferedReader br_hphz = new BufferedReader(
																			hphz);
																	String zeile_hphz = "";

																	if (!segs_fwv[5]
																			.equals(list_fa
																					.get(i)
																					.get(31))) {
																		while ((zeile_hphz = br_hphz
																				.readLine()) != null) {
																			String[] segs_hphz = zeile_hphz
																					.split("\"");

																			if (segs_hphz[1]
																					.equals("1.0")) {
																				continue;
																			}

																			if (segs_hphz[1]
																					.equals(segs_fwv[7])
																					& segs_hphz[3]
																							.equals(list_fa
																									.get(i)
																									.get(15))) {
																				current_time = addierer(
																						current_time,
																						segs_hphz[5]);
																				break;
																			}

																		}
																		br_hphz.close();
																	}
																}

															}

														}
														br_fz.close();

													}

												}
												pre_segs_fwv = segs_fwv;

											}

											br_fwv.close();

											list_fa.remove(i);
											i -= 1;

											k += current_fhz.size() - 1;
										}

									}
								}

							}

						}

						boolean not_used = true;

						for (int j = 0; j < list_fa.size(); j++) {

							if (list_fa.get(j).get(1).equals("1.0")) {
								continue;
							}

							if (list_fa.get(j).get(9).equals(segs_li[1])
									& list_fa.get(j).get(13).equals(segs_fw[3])
									& list_fa.get(j).get(19).equals("0")) {
								not_used = false;
								break;
							}
						}

						if (not_used) {
							break;

						} else {

							Element transitRoute = doc
									.createElement("transitRoute");
							transitLine.appendChild(transitRoute);

							Attr attr_transitRoute = doc.createAttribute("id");
							attr_transitRoute.setValue(segs_fw[3]);
							transitRoute.setAttributeNode(attr_transitRoute);

							System.out.println("-> Linienvariante: "
									+ segs_fw[3]);

							String bb_line = "";

							for (int j = 0; j < list_bb.size(); j++) {
								String[] segs_bb = list_bb.get(j).split("_");
								if (segs_bb[0].equals(segs_li[3])) {
									bb_line = segs_bb[1];
								}
							}

							Element transportMode = doc
									.createElement("transportMode");
							transportMode.appendChild(doc
									.createTextNode(bb_line));
							transitRoute.appendChild(transportMode);

							System.out.println("--> Betriebsbereich: "
									+ segs_li[3]);

							Element routeProfile = doc
									.createElement("routeProfile");
							transitRoute.appendChild(routeProfile);

							Element route = doc.createElement("route");
							transitRoute.appendChild(route);

							Element departures = doc
									.createElement("departures");
							transitRoute.appendChild(departures);

							for (int i = 0; i < list_fa.size(); i++) {

								if (list_fa.get(i).get(1).equals("1.0")) {
									continue;
								}

								if (!list_fa.get(i).get(19).equals("0")) {
									continue;
								}

								if (list_fa.get(i).get(9).equals(segs_fw[1])
										& list_fa.get(i).get(13)
												.equals(segs_fw[3])) {

									if (last_trip.equals(list_fa.get(i).get(9)
											+ "_" + list_fa.get(i).get(13))) {

										FileReader wa = new FileReader(export
												+ "Wagenumlaeufe.trd");
										BufferedReader br_wa = new BufferedReader(
												wa);

										String zeile_wa = "";

										while ((zeile_wa = br_wa.readLine()) != null) {

											String[] segs_wa = zeile_wa
													.split("\"");

											if (segs_wa[1].equals("1.0")) {
												continue;
											}

											if (list_fa.get(i).get(1)
													.equals(segs_wa[3])
													& list_fa.get(i).get(3)
															.equals(segs_wa[1])
											// & list_fa.get(i).get(39)
											// .equals(segs_wa[7])
											) {

												Element departure = doc
														.createElement("departure");
												departures
														.appendChild(departure);

												Attr attr_departures1 = doc
														.createAttribute("id");
												attr_departures1
														.setValue(list_fa
																.get(i).get(7));
												departure
														.setAttributeNode(attr_departures1);

												Attr attr_departures2 = doc
														.createAttribute("departureTime");
												attr_departures2
														.setValue(umrechner(list_fa
																.get(i).get(33)));
												departure
														.setAttributeNode(attr_departures2);

												Attr attr_departures3 = doc
														.createAttribute("vehicleRefId");
												attr_departures3
														.setValue(segs_wa[1]
																+ "_"
																+ segs_wa[3]
																+ "_"
																+ segs_wa[5]);
												departure
														.setAttributeNode(attr_departures3);
												System.out
														.println("---> departure id: "
																+ list_fa
																		.get(i)
																		.get(7)
																+ ", departureTime: "
																+ umrechner(list_fa
																		.get(i)
																		.get(33))
																+ ", vehicleRefId: "
																+ segs_wa[1]
																+ "_"
																+ segs_wa[3]
																+ "_"
																+ segs_wa[5]);
											}
										}
										br_wa.close();
										continue;
									}

									last_trip = list_fa.get(i).get(9) + "_"
											+ list_fa.get(i).get(13);

									String current_time = "0";

									FileReader wa = new FileReader(export
											+ "Wagenumlaeufe.trd");
									BufferedReader br_wa = new BufferedReader(
											wa);

									String zeile_wa = "";

									while ((zeile_wa = br_wa.readLine()) != null) {

										String[] segs_wa = zeile_wa.split("\"");

										if (segs_wa[1].equals("1.0")) {
											continue;
										}

										if (list_fa.get(i).get(1)
												.equals(segs_wa[3])
												& list_fa.get(i).get(3)
														.equals(segs_wa[1])
										// & list_fa.get(i).get(39)
										// .equals(segs_wa[7])
										) {

											Element departure = doc
													.createElement("departure");
											departures.appendChild(departure);

											Attr attr_departures1 = doc
													.createAttribute("id");
											attr_departures1.setValue(list_fa
													.get(i).get(7));
											departure
													.setAttributeNode(attr_departures1);

											Attr attr_departures2 = doc
													.createAttribute("departureTime");
											attr_departures2
													.setValue(umrechner(list_fa
															.get(i).get(33)));
											departure
													.setAttributeNode(attr_departures2);

											Attr attr_departures3 = doc
													.createAttribute("vehicleRefId");
											attr_departures3
													.setValue(segs_wa[1] + "_"
															+ segs_wa[3] + "_"
															+ segs_wa[5]);
											departure
													.setAttributeNode(attr_departures3);
											System.out
													.println("---> departure id: "
															+ list_fa.get(i)
																	.get(7)
															+ ", departureTime: "
															+ umrechner(list_fa
																	.get(i)
																	.get(33))
															+ ", vehicleRefId: "
															+ segs_wa[1]
															+ "_"
															+ segs_wa[3]
															+ "_"
															+ segs_wa[5]);
										}

									}

									br_wa.close();

									FileReader fwv = new FileReader(export
											+ "Fahrwegverlaeufe.trd");
									BufferedReader br_fwv = new BufferedReader(
											fwv);

									String zeile_fwv = "";
									String[] pre_segs_fwv = new String[1];

									while ((zeile_fwv = br_fwv.readLine()) != null) {
										String[] segs_fwv = zeile_fwv
												.split("\"");

										if (segs_fwv[1].equals("1.0")) {
											continue;
										}

										if (segs_fw[1].equals(segs_fwv[1])
												& segs_fw[3]
														.equals(segs_fwv[3])) {
											if (segs_fwv[5].equals(list_fa.get(
													i).get(25))) {
												Element stop = doc
														.createElement("stop");
												routeProfile.appendChild(stop);

												Attr attr_stop1 = doc
														.createAttribute("refId");
												attr_stop1.setValue("SF" + "_"
														+ segs_fwv[7] + "_"
														+ segs_fwv[1] + "_"
														+ segs_fwv[3] + "_"
														+ segs_fwv[5]);
												stop.setAttributeNode(attr_stop1);

												Attr attr_stop2 = doc
														.createAttribute("departureOffset");
												attr_stop2
														.setValue(umrechner(current_time));
												stop.setAttributeNode(attr_stop2);

												System.out
														.println("---> refId: "
																+ segs_fwv[7]
																+ ", departureOffset: "
																+ umrechner(current_time));

											} else {

												FileReader vb = new FileReader(
														export
																+ "Verbindungen.trd");
												BufferedReader br_vb = new BufferedReader(
														vb);
												String zeile_vb = "";

												while ((zeile_vb = br_vb
														.readLine()) != null) {
													String[] segs_vb = zeile_vb
															.split("\"");

													if (segs_vb[1]
															.equals("1.0")) {
														continue;
													}

													if (segs_vb[1]
															.equals(pre_segs_fwv[7])
															& segs_vb[5]
																	.equals(segs_fwv[7])) {

														Element link = doc
																.createElement("link");
														route.appendChild(link);

														Attr attr_link = doc
																.createAttribute("refId");

														if (!segs_fwv[5]
																.equals(list_fa
																		.get(i)
																		.get(31))) {
															attr_link
																	.setValue("R_"
																			+ pre_segs_fwv[7]
																			+ "_"
																			+ segs_fwv[7]);
															link.setAttributeNode(attr_link);

															System.out
																	.println("----> route: R_"
																			+ pre_segs_fwv[7]
																			+ "_"
																			+ segs_fwv[7]);
															break;
														} else {

															attr_link
																	.setValue("R_"
																			+ pre_segs_fwv[7]
																			+ "_"
																			+ segs_fwv[7]);
															link.setAttributeNode(attr_link);

															System.out
																	.println("----> route: R_"
																			+ pre_segs_fwv[7]
																			+ "_"
																			+ segs_fwv[7]);

															Element link2 = doc
																	.createElement("link");
															route.appendChild(link2);

															Attr attr_link2 = doc
																	.createAttribute("refId");

															attr_link2
																	.setValue("R_"
																			+ segs_fwv[7]
																			+ "_END");
															link2.setAttributeNode(attr_link2);

															System.out
																	.println("----> route: R_"
																			+ pre_segs_fwv[7]
																			+ "_"
																			+ segs_fwv[7]);
															break;
														}
													}
												}
												br_vb.close();

												FileReader fz = new FileReader(
														export
																+ "Fahrzeiten.trd");
												BufferedReader br_fz = new BufferedReader(
														fz);
												String zeile_fz = "";
												while ((zeile_fz = br_fz
														.readLine()) != null) {
													String[] segs_fz = zeile_fz
															.split("\"");

													if (segs_fz[1]
															.equals("1.0")) {
														continue;
													}

													if (pre_segs_fwv[7]
															.equals(segs_fz[1])
															& segs_fwv[7]
																	.equals(segs_fz[5])
															& list_fa
																	.get(i)
																	.get(15)
																	.equals(segs_fz[9])) {

														current_time = addierer(
																current_time,
																segs_fz[11]);

														if (segs_fwv[9]
																.equals("1")) {
															Element stop = doc
																	.createElement("stop");
															routeProfile
																	.appendChild(stop);

															Attr attr_stop1 = doc
																	.createAttribute("refId");
															attr_stop1
																	.setValue("SF"
																			+ "_"
																			+ segs_fwv[7]
																			+ "_"
																			+ segs_fwv[1]
																			+ "_"
																			+ segs_fwv[3]
																			+ "_"
																			+ segs_fwv[5]);
															stop.setAttributeNode(attr_stop1);

															Attr attr_stop3 = doc
																	.createAttribute("arrivalOffset");
															attr_stop3
																	.setValue(umrechner(current_time));
															stop.setAttributeNode(attr_stop3);

															FileReader hphz = new FileReader(
																	export
																			+ "Haltepunkthaltezeiten.trd");
															BufferedReader br_hphz = new BufferedReader(
																	hphz);
															String zeile_hphz = "";

															if (!segs_fwv[5]
																	.equals(list_fa
																			.get(i)
																			.get(31))) {
																while ((zeile_hphz = br_hphz
																		.readLine()) != null) {
																	String[] segs_hphz = zeile_hphz
																			.split("\"");

																	if (segs_hphz[1]
																			.equals("1.0")) {
																		continue;
																	}

																	if (segs_hphz[1]
																			.equals(segs_fwv[7])
																			& segs_hphz[3]
																					.equals(list_fa
																							.get(i)
																							.get(15))) {
																		current_time = addierer(
																				current_time,
																				segs_hphz[5]);
																		break;
																	}

																}
																br_hphz.close();
																Attr attr_stop2 = doc
																		.createAttribute("departureOffset");
																attr_stop2
																		.setValue(umrechner(current_time));
																stop.setAttributeNode(attr_stop2);
																System.out
																		.println("---> refId: "
																				+ segs_fwv[7]
																				+ ", arrivalOffset: "
																				+ umrechner(current_time)
																				+ ", departureOffset: "
																				+ umrechner(current_time));
															} else {
																System.out
																		.println("---> refId: "
																				+ segs_fwv[7]
																				+ ", arrivalOffset: "
																				+ umrechner(current_time));

															}

														} else {

															FileReader hphz = new FileReader(
																	export
																			+ "Haltepunkthaltezeiten.trd");
															BufferedReader br_hphz = new BufferedReader(
																	hphz);
															String zeile_hphz = "";

															if (!segs_fwv[5]
																	.equals(list_fa
																			.get(i)
																			.get(31))) {
																while ((zeile_hphz = br_hphz
																		.readLine()) != null) {
																	String[] segs_hphz = zeile_hphz
																			.split("\"");

																	if (segs_hphz[1]
																			.equals("1.0")) {
																		continue;
																	}

																	if (segs_hphz[1]
																			.equals(segs_fwv[7])
																			& segs_hphz[3]
																					.equals(list_fa
																							.get(i)
																							.get(15))) {
																		current_time = addierer(
																				current_time,
																				segs_hphz[5]);
																		break;
																	}

																}
																br_hphz.close();
															}
														}

													}

												}
												br_fz.close();

											}

										}
										pre_segs_fwv = segs_fwv;

									}
									br_fwv.close();

									list_fa.remove(i);
									i -= 1;
								}

							}
						}
					}

				}
				br_fw.close();
			}
			br_li.close();

			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
					"http://www.matsim.org/files/dtd/transitSchedule_v1.dtd");
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}
	
	//-------------------------------------------------------------------------------
	
	public String umrechner(String zeit){ // Umrechnen von Sekunden in die Form HH:MM:SS

		int int_zeit = Integer.parseInt(zeit);
		

		int int_stunden = int_zeit/3600;		
		String string_stunden = String.valueOf(int_stunden);
		if(string_stunden.length()==1){
			string_stunden = "0"+string_stunden;
		}
		
		int int_minuten = (int_zeit - int_stunden * 3600) / 60;
		String string_minuten = String.valueOf(int_minuten);
		if(string_minuten.length()==1){
			string_minuten = "0"+string_minuten;
		}
		
		int int_sekunden = int_zeit - int_stunden * 3600 - int_minuten * 60;
		String string_sekunden = String.valueOf(int_sekunden);
		if(string_sekunden.length()==1){
			string_sekunden = "0"+string_sekunden;
		}
		

		return string_stunden + ":" + string_minuten + ":" + string_sekunden;
	}
	
	public String addierer(String ankunft, String warte){ // Addieren von Sekunden, speziell für Ankunftszeit + Haltestellenhaltezeit
		
		int int_ankunft = Integer.parseInt(ankunft);
		int int_warte = Integer.parseInt(warte);

		
		return String.valueOf(int_ankunft + int_warte);
		
	}

}
