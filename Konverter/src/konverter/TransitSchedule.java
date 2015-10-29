package konverter;



	
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.vehicles.Vehicle;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordImpl;
import org.matsim.core.utils.geometry.transformations.WGS84toCH1903LV03;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitScheduleFactory;
import org.matsim.pt.transitSchedule.api.TransitScheduleWriter;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

public class TransitSchedule {
		
		public void create(String input) throws IOException {

			Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.loadConfig("input/config.xml"));
			
			TransitScheduleFactory tfac = scenario.getTransitSchedule().getFactory();
			
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

			FileReader fhz = new FileReader(input + "Fahrthaltezeiten.trd");
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

			FileReader fa = new FileReader(input + "Fahrten.trd");
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

			FileReader li = new FileReader(input + "Linien.trd");
			BufferedReader br_li = new BufferedReader(li);

			FileReader fwv1 = new FileReader(input + "Fahrwegverlaeufe.trd");
			BufferedReader br_fwv1 = new BufferedReader(fwv1);

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

				//////////////////////////////////////
				// Beginn der TransitStopFacilities //
				//////////////////////////////////////
					
					FileReader or = new FileReader(input + "Orte.trd");
					BufferedReader br_or = new BufferedReader(or);
					String zeile_or = "";

					while ((zeile_or = br_or.readLine()) != null) {
						String[] segs_or = zeile_or.split("\"");

						if (segs_or[1].equals("1.0")) {
							continue;
						}

						if (pre_segs_fwv1[7].equals(segs_or[1])
								& pre_segs_fwv1[9].equals("1")) {
							
							Id<TransitStopFacility> id_tsf = Id.create("SF_" + pre_segs_fwv1[7] + "_" + pre_segs_fwv1[1] + "_" + pre_segs_fwv1[3] + "_" + pre_segs_fwv1[5], TransitStopFacility.class);
							TransitStopFacility tsf = tfac.createTransitStopFacility(id_tsf, new WGS84toCH1903LV03().transform(new CoordImpl(coord_convert(segs_or[9]), coord_convert(segs_or[11]))), false);

							if (!segs_fwv1[5].equals("1")) {
								Id<Link> refLink = Id.createLinkId("R_" + pre_segs_fwv1[7] + "_" + segs_fwv1[7]);
								tsf.setLinkId(refLink);
								

							} else {
								Id<Link> refLink = Id.createLinkId("R_" + pre_segs_fwv1[7] + "_END");
								tsf.setLinkId(refLink);

							}
							
							scenario.getTransitSchedule().addStopFacility(tsf);
						
							x = segs_or[9];
							y = segs_or[11];
						}
					}

					br_or.close();

					pre_segs_fwv1 = segs_fwv1;
				}

				br_fwv1.close();

				Id<TransitStopFacility> id_tsf = Id.create("SF_" + pre_segs_fwv1[7] + "_" + pre_segs_fwv1[1] + "_" + pre_segs_fwv1[3] + "_" + pre_segs_fwv1[5], TransitStopFacility.class);
				TransitStopFacility tsf = tfac.createTransitStopFacility(id_tsf, new WGS84toCH1903LV03().transform(new CoordImpl(coord_convert(x), coord_convert(y))), false);
				
				Id<Link> refLink = Id.createLinkId("R_" + pre_segs_fwv1[7] + "_END");
				tsf.setLinkId(refLink);
				
				scenario.getTransitSchedule().addStopFacility(tsf);
				
				
				/////////////////////////////
				// Übergang zu transitLine //
				/////////////////////////////

				String last_trip = "";
				String zeile_li = "";
				while ((zeile_li = br_li.readLine()) != null) {
					String[] segs_li = zeile_li.split("\"");

					if (segs_li[1].equals("1.0")) {
						continue;
					}

					Id<TransitLine> id_tl = Id.create(segs_li[1], TransitLine.class);
					TransitLine tl = tfac.createTransitLine(id_tl);
					scenario.getTransitSchedule().addTransitLine(tl);
					
					FileReader fw = new FileReader(input + "Fahrwege.trd");
					BufferedReader br_fw = new BufferedReader(fw);

					String zeile_fw = "";
					while ((zeile_fw = br_fw.readLine()) != null) { // Fahrwege laden
						String[] segs_fw = zeile_fw.split("\"");

						if (segs_fw[1].equals("1.0")) {
							continue;
						}

						if (segs_fw[1].equals(segs_li[1])) { // Wenn Fahrweg = Linie
							

							for (int k = 0; k < list_fhz.size(); k++) { // fahrtbezogene Haltezeiten laden

								if (segs_li[1].equals(list_fhz.get(k).get(7))
										& segs_fw[3].equals(list_fhz.get(k).get(9))) { // Prüfen, ob fahrtbezogene Wartezeiten existieren
									

									if (list_fa.isEmpty()) { // Wenn keine Fahrten existieren, Schleife beenden. Ansonsten weiter.
										break;
									} else {
										

										for (int i = 0; i < list_fa.size(); i++) { // Für alle Fahrten

											if (list_fa.get(i).get(1).equals("1.0")) {
												continue;
											}

											if (!list_fa.get(i).get(19).equals("0")) { // Wenn keine Linienfahrt, weiter
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
																	.get(9))) { // Prüfen, ob Fahrt selbst fahrtbezogene Wartezeiten hat 

												if (!list_fa.get(i).get(19)
														.equals("0")) {
													continue;
												}
												
												Id<TransitRoute> id_tr = Id.create(segs_fw[3] + "_" + list_fhz.get(k).get(5) + "_" + list_fhz.get(k).get(7) + "_" + list_fhz.get(k).get(3), TransitRoute.class);
												// TransitRoute wird erstellt
												
												List<Id<Link>> l_idl = new ArrayList<Id<Link>>();
												List<Departure> departures = new ArrayList<Departure>();
												List<TransitRouteStop> l_trs = new ArrayList<TransitRouteStop>();
												String current_time = "0";

												ArrayList<ArrayList<String>> current_fhz = new ArrayList<ArrayList<String>>(); // fahrtbezogene Wartezeiten

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
														input
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
																	.equals(segs_wa[1]) // Prüfen, welcher Wagenumlauf zu der Fahrt gehört
													) {
														Id<Departure> id_d = Id.create(list_fa.get(i).get(7), Departure.class);  // Fahrt kann erstellt werden
														Departure departure = tfac.createDeparture(id_d, Double.parseDouble(list_fa.get(i).get(33)));
														Id<Vehicle> id_v = Id.create(segs_wa[1] + "_" + segs_wa[3]+ "_" + segs_wa[5], Vehicle.class);
														departure.setVehicleId(id_v);
														
														departures.add(departure);
														
													}

												}

												br_wa.close();


												FileReader fwv = new FileReader(
														input
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
															
															for(TransitStopFacility tsfl : scenario.getTransitSchedule().getFacilities().values()){
																if(tsfl.getId().toString().equals("SF_" + segs_fwv[7] + "_" + segs_fwv[1] + "_" + segs_fwv[3] + "_" + segs_fwv[5])){
																	TransitRouteStop trs = tfac.createTransitRouteStop(tsfl, Double.parseDouble(current_time), Double.parseDouble(current_time));																			
																	trs.setAwaitDepartureTime(true);
																	l_trs.add(trs);
																}
															}														

														} else {

															FileReader vb = new FileReader(
																	input
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
																	
																	

																	if (!segs_fwv[5]
																			.equals(list_fa
																					.get(i)
																					.get(31))) {
																		
																		Id<Link> id_l = Id.createLinkId("R_" + pre_segs_fwv[7] + "_" + segs_fwv[7]);
																		l_idl.add(id_l);
																		

																		break;
																	} else {

																		Id<Link> id_l = Id.createLinkId("R_" + pre_segs_fwv[7] + "_" + segs_fwv[7]);
																		l_idl.add(id_l);
																		
																		Id<Link> id_le = Id.createLinkId("R_" + segs_fwv[7] + "_END");
																		l_idl.add(id_le);
																		

																		break;
																	}
																}
															}
															br_vb.close();

															FileReader fz = new FileReader(
																	input
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
																			segs_fz[11]); // wenn fahrtbezogene Zeit, dann addieren

																	if (segs_fwv[9]
																			.equals("1")) {
																		
																		Double arrivalO = Double.parseDouble(current_time);
																																					

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
																				input
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
																			
																			for(TransitStopFacility tsfl : scenario.getTransitSchedule().getFacilities().values()){
																				if(tsfl.getId().toString().equals("SF_" + segs_fwv[7] + "_" + segs_fwv[1] + "_" + segs_fwv[3] + "_" + segs_fwv[5])){
																					TransitRouteStop trs = tfac.createTransitRouteStop(tsfl, arrivalO, Double.parseDouble(current_time));																			
																					trs.setAwaitDepartureTime(true);
																					l_trs.add(trs);
																				}
																			}
																		}else{
																			for(TransitStopFacility tsfl : scenario.getTransitSchedule().getFacilities().values()){
																				if(tsfl.getId().toString().equals("SF_" + segs_fwv[7] + "_" + segs_fwv[1] + "_" + segs_fwv[3] + "_" + segs_fwv[5])){
																					TransitRouteStop trs = tfac.createTransitRouteStop(tsfl, Double.parseDouble(current_time), Double.parseDouble(current_time));																			
																					trs.setAwaitDepartureTime(true);
																					l_trs.add(trs);
																				}
																			}
																		}

																	} else {
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
																				input
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

											
											
											String bb_line = "";

											for (int n = 0; n < list_bb.size(); n++) {
												String[] segs_bb = list_bb.get(n)
														.split("_");
												if (segs_bb[0].equals(segs_li[3])) {
													bb_line = segs_bb[1];
												}
											}
											

											Id<Link> linkStart = l_idl.remove(0);
											
											Id<Link> linkEnd= l_idl.remove(l_idl.size()-1);
											
											NetworkRoute nw = new LinkNetworkRouteImpl(linkStart, l_idl, linkEnd);
										
											
											TransitRoute tr = tfac.createTransitRoute(id_tr, nw, l_trs, bb_line);
											
											for(Departure departure : departures){
												tr.addDeparture(departure);
											}
											
											tr.setDescription(segs_fw[7]);
										
											tl.addRoute(tr);
										}
											
											

										}
									
									
//									String bb_line = "";
//
//									for (int i = 0; i < list_bb.size(); i++) {
//										String[] segs_bb = list_bb.get(i)
//												.split("_");
//										if (segs_bb[0].equals(segs_li[3])) {
//											bb_line = segs_bb[1];
//										}
//									}
//									
//
//									Id<Link> linkStart = l_idl.remove(0);
//									
//									Id<Link> linkEnd;
//									
//									if(l_idl.size()==1){
//										linkEnd = l_idl.remove(0);
//									}else{
//										linkEnd = l_idl.remove(l_idl.size()-1);
//									}
//									
//									NetworkRoute nw = new LinkNetworkRouteImpl(linkStart, l_idl, linkEnd);
//								
//									
//									TransitRoute tr = tfac.createTransitRoute(id_tr, nw, l_trs, bb_line);
//									
//									for(Departure departure : departures){
//										tr.addDeparture(departure);
//									}									
//								
//									tl.addRoute(tr);
									
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
							

							if (not_used) { // wenn Fahrt noch nicht genutzt, gehe weiter
								continue;

							} else {
															
								List<Departure> departures = new ArrayList<Departure>();
								List<Id<Link>> l_idl = new ArrayList<Id<Link>>();
								List<TransitRouteStop> l_trs = new ArrayList<TransitRouteStop>();
								
								Id<TransitRoute> id_tr = Id.create(segs_fw[3], TransitRoute.class);
								
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

											FileReader wa = new FileReader(input
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
												) {
													
													Id<Departure> id_d = Id.create(list_fa.get(i).get(7), Departure.class); 
													Departure departure = tfac.createDeparture(id_d, Double.parseDouble(list_fa.get(i).get(33)));
													Id<Vehicle> id_v = Id.create(segs_wa[1] + "_" + segs_wa[3]+ "_" + segs_wa[5], Vehicle.class);
													departure.setVehicleId(id_v);
													
													departures.add(departure);

												}
											}
											br_wa.close();
											
											list_fa.remove(i);
											i -= 1;	
											continue;
										}

										last_trip = list_fa.get(i).get(9) + "_"
												+ list_fa.get(i).get(13);
										

										String current_time = "0";

										FileReader wa = new FileReader(input
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
											) {
												
												Id<Departure> id_d = Id.create(list_fa.get(i).get(7), Departure.class); 
												Departure departure = tfac.createDeparture(id_d, Double.parseDouble(list_fa.get(i).get(33)));
												Id<Vehicle> id_v = Id.create(segs_wa[1] + "_" + segs_wa[3]+ "_" + segs_wa[5], Vehicle.class);
												departure.setVehicleId(id_v);
												
												departures.add(departure);

											}

										}

										br_wa.close();

										FileReader fwv = new FileReader(input
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
														i).get(25))) { // wenn erste Fahrt
													
													for(TransitStopFacility tsfl : scenario.getTransitSchedule().getFacilities().values()){
														if(tsfl.getId().toString().equals("SF_" + segs_fwv[7] + "_" + segs_fwv[1] + "_" + segs_fwv[3] + "_" + segs_fwv[5])){
															TransitRouteStop trs = tfac.createTransitRouteStop(tsfl, Double.parseDouble(current_time), Double.parseDouble(current_time));																			
															trs.setAwaitDepartureTime(true);
															l_trs.add(trs);
														}
													}												
													

												} else {

													FileReader vb = new FileReader(
															input
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


															if (!segs_fwv[5]
																	.equals(list_fa
																			.get(i)
																			.get(31))) {
																
																Id<Link> id_l = Id.createLinkId("R_" + pre_segs_fwv[7] + "_" + segs_fwv[7]);
																l_idl.add(id_l);

																break;
															} else {
																
																Id<Link> id_l = Id.createLinkId("R_" + pre_segs_fwv[7] + "_" + segs_fwv[7]);
																l_idl.add(id_l);
																
																Id<Link> id_le = Id.createLinkId("R_" + segs_fwv[7] + "_END");
																l_idl.add(id_le);

																break;
															}
														}
														
													}
													br_vb.close();
													

													FileReader fz = new FileReader(
															input
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
																
																Double arrivalO = Double.parseDouble(current_time);
																

																FileReader hphz = new FileReader(
																		input
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
																	
																	for(TransitStopFacility tsfl : scenario.getTransitSchedule().getFacilities().values()){
																		if(tsfl.getId().toString().equals("SF_" + segs_fwv[7] + "_" + segs_fwv[1] + "_" + segs_fwv[3] + "_" + segs_fwv[5])){
																			TransitRouteStop trs = tfac.createTransitRouteStop(tsfl, Double.parseDouble(current_time), arrivalO);																			
																			trs.setAwaitDepartureTime(true);
																			l_trs.add(trs);
																		}
																	}
																	

																} else {
																	for(TransitStopFacility tsfl : scenario.getTransitSchedule().getFacilities().values()){
																		if(tsfl.getId().toString().equals("SF_" + segs_fwv[7] + "_" + segs_fwv[1] + "_" + segs_fwv[3] + "_" + segs_fwv[5])){
																			TransitRouteStop trs = tfac.createTransitRouteStop(tsfl, Double.parseDouble(current_time), Double.parseDouble(current_time));																			
																			trs.setAwaitDepartureTime(true);
																			l_trs.add(trs);
																		}
																	}

																}

															} else {

																FileReader hphz = new FileReader(
																		input
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
								
								
								String bb_line = "";

								for (int j = 0; j < list_bb.size(); j++) {
									String[] segs_bb = list_bb.get(j)
											.split("_");
									if (segs_bb[0].equals(segs_li[3])) {
										bb_line = segs_bb[1];
									}
								}
								
								Id<Link> linkStart = l_idl.remove(0);
								
								Id<Link> linkEnd= l_idl.remove(l_idl.size()-1);
								
								NetworkRoute nw = new LinkNetworkRouteImpl(linkStart, l_idl, linkEnd);
								
								TransitRoute tr = tfac.createTransitRoute(id_tr, nw, l_trs, bb_line);
								
								for(Departure departure : departures){
									tr.addDeparture(departure);
								}
								
								tr.setDescription(segs_fw[7]);
								
								tl.addRoute(tr);
								
							}
																			
						}

					}
					br_fw.close();
				}
				br_li.close();

				
			//////////////////////////////////
				
			new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile("output_konverter/Planfall1/transitSchedule.xml");

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
		
		public Double coord_convert(String coord){
			
			Double grad = Double.parseDouble((coord.substring(0, 3)));
			Double min = Double.parseDouble(coord.substring(3, 5))/60;
			Double sek = Double.parseDouble(coord.substring(5, 7))/3600;
			Double rest = Double.parseDouble(coord.substring(7, 10))/3600000;
			
			Double result = grad + min + sek +rest;
			
			return result;
		}
}
