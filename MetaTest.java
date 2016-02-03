import java.util.ArrayList;
import java.util.List;

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

public class MetaTest {

	public static void main(String[] args) {
		MetaMapApi api = new MetaMapApiImpl();
		api.setOptions("-y");
		/*
		 * List<String> theOptions = new ArrayList<String>();
		 * theOptions.add("-y");// turn on Word Sense Disambiguation if
		 * (theOptions.size() > 0) { api.setOptions(theOptions); }
		 */
		String querystr = "A 40-year old male diagnosed with diabetes mellitus on Glucophage 500mg/day suffering from heavy breath. No chest pain is present. He is obese and does not exercise. He smokes heavily.";
		System.out.println("I am working");
		List<Result> resultList = api.processCitationsFromString(querystr);
		List<String> conceptsIdentified = new ArrayList<String>();
		String sparql = "SELECT ?x WHERE {";
		// List<String> conceptUI = new ArrayList<String>();
		List<String> listProc = new ArrayList<String>();
		Result result = resultList.get(0);
		System.out.println(result);
		try {
			for (Utterance utterance : result.getUtteranceList()) {
				System.out.println("Utterance:");
				System.out.println(" Id: " + utterance.getId());
				System.out.println(" Utterance text: " + utterance.getString());
				System.out.println(" Position: " + utterance.getPosition());
				for (PCM pcm : utterance.getPCMList()) {
					System.out.println("Phrase:");
					System.out.println(" text: " + pcm.getPhrase().getPhraseText());
					System.out.println("Mappings:");
					for (Mapping map : pcm.getMappingList()) {
						System.out.println(" Map Score: " + map.getScore());
						for (Ev mapEv : map.getEvList()) {
							System.out.println("   Score: " + mapEv.getScore());
							System.out.println("   Concept Id: " + mapEv.getConceptId());
							System.out.println("   Concept Name: " + mapEv.getConceptName());
							System.out.println("   Preferred Name: " + mapEv.getPreferredName());
							System.out.println("   Matched Words: " + mapEv.getMatchedWords());
							System.out.println("   Semantic Types: " + mapEv.getSemanticTypes());
							System.out.println("   MatchMap: " + mapEv.getMatchMap());
							System.out.println("   MatchMap alt. repr.: " + mapEv.getMatchMapList());
							System.out.println("   is Head?: " + mapEv.isHead());
							System.out.println("   is Overmatch?: " + mapEv.isOvermatch());
							System.out.println("   Sources: " + mapEv.getSources());
							System.out.println("   Positional Info: " + mapEv.getPositionalInfo());
							if (mapEv.getScore() <= -700) {
								String s = mapEv.getConceptName();
								// String w = mapEv.getConceptId();

								conceptsIdentified.add(s);
							}

						}

					}

					
					for (int x = 0; x < conceptsIdentified.size(); x++) {
						if (isNotPresent(conceptsIdentified.get(x), listProc)) {
							listProc.add(conceptsIdentified.get(x));

						}
					}
					/*
					 * for (int z = 0; z < listProc.size(); z++) { String mid =
					 * listProc.get(z).substring(listProc.get(z).indexOf("|"),
					 * listProc.get(z).length()); conceptUI.add(mid);
					 * System.out.println(listProc.get(z)); }
					 */
					
					}

				}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int count = 0;
		for (String s : listProc) {
			sparql = sparql + "\n OPTIONAL(?x ?y" + count + " ?" + s + ")";
			count++;
		}
System.out.println(sparql);
	
	}

	public static boolean isNotPresent(String s, List<String> w) {
		for (int i = 0; i < w.size(); i++) {
			if (w.get(i).equals(s)) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<String> getMetaMapCUI(String querystr) {
		MetaMapApi api = new MetaMapApiImpl();
		api.setOptions("-y -b");
		List<Result> resultList = api.processCitationsFromString(querystr);
		ArrayList<String> conceptUI = new ArrayList<String>();
		Result result = resultList.get(0);
		try {
			for (Utterance utterance : result.getUtteranceList()) {

				for (PCM pcm : utterance.getPCMList()) {

					for (Mapping map : pcm.getMappingList()) {

						for (Ev mapEv : map.getEvList()) {

							if (mapEv.getScore() <= -800) {

								String w = mapEv.getConceptId();
								conceptUI.add(w);

							}

						}

					}

					ArrayList<String> listProc = new ArrayList<String>();
					for (int x = 0; x < conceptUI.size(); x++) {
						if (isNotPresent(conceptUI.get(x), listProc)) {
							listProc.add(conceptUI.get(x));

						}
					}
					conceptUI = listProc;

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conceptUI;
	}

}
