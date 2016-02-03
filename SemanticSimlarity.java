import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.data.BabelCategory;
import it.uniroma1.lcl.jlt.util.Language;
import gov.nih.nlm.uts.webservice.content.*;
import gov.nih.nlm.uts.webservice.content.Psf;
import gov.nih.nlm.uts.webservice.content.UtsFault_Exception;
import gov.nih.nlm.uts.webservice.security.*;
import gov.nih.nlm.uts.webservice.metadata.*;
import gov.nih.nlm.uts.webservice.finder.*;
import gov.nih.nlm.uts.webservice.history.*;
import gov.nih.nlm.uts.webservice.semnet.*;

public class SemanticSimlarity {
	ArrayList<String> clinNotesObjs;
	ArrayList<String> voiceObjs;
	private String username = "varun11s";
	private String password = "Supermoose1!";
	private String serviceName = "http://umlsks.nlm.nih.gov";
	int score;
	int simScore;
	int count = 0;
	final int i = 1000;
	final int j = 100;
	final int k = 10;
	final int m = 1;
	long weakConnection = 100000;

	public SemanticSimlarity(ArrayList<String> a, ArrayList<String> b) {
		clinNotesObjs = a;
		voiceObjs = b;
		score = 0;
		simScore = 0;
	}

	public int getMappingScore() {
		BabelNet bn = BabelNet.getInstance();
		ArrayList<List<BabelCategory>> cat1 = new ArrayList<List<BabelCategory>>();
		ArrayList<List<BabelCategory>> cat2 = new ArrayList<List<BabelCategory>>();
		// List<BabelSynset> by = new ArrayList<BabelSynset>();
		// List<BabelSynset> by2 = new ArrayList<BabelSynset>();
		List<List<BabelSynset>> syn1 = new ArrayList<List<BabelSynset>>();
		List<List<BabelSynset>> syn2 = new ArrayList<List<BabelSynset>>();
		List<BabelSense> bs = new ArrayList<BabelSense>();
		List<BabelSense> bs1 = new ArrayList<BabelSense>();

		for (String s : clinNotesObjs) {
			List<BabelSynset> by = bn.getSynsets(s);
			for (BabelSynset b1 : by) {
				bs.add(b1.getMainSense(Language.EN));
			}

			syn1.add(by);
			for (BabelSynset w : by) {
				List<BabelCategory> bcatClin = w.getCategories();
				cat1.add(bcatClin);
			}
		}
		for (String x : voiceObjs) {
			List<BabelSynset> by2 = bn.getSynsets(x);
			for (BabelSynset b2 : by2) {
				bs1.add(b2.getMainSense(Language.EN));
			}
			syn2.add(by2);
			for (BabelSynset d : by2) {
				List<BabelCategory> bcatVoice = d.getCategories();
				cat2.add(bcatVoice);
			}
		}
		for (List<BabelSynset> cSynList : syn1) {
			for (BabelSynset cSyn : cSynList) {
				for (List<BabelSynset> vSynList : syn2) {
					for (BabelSynset vSyn : vSynList) {
						if (vSyn.equals(cSyn)) {
							score = score + i;
						}
					}
				}
			}
		}
		if (score < weakConnection) {

			for (List<BabelCategory> cCatList : cat1) {
				for (BabelCategory cCat : cCatList) {
					for (List<BabelCategory> vCatList : cat2) {
						for (BabelCategory vCat : vCatList) {
							if (cCat.equals(vCat)) {
								score = score + j;
							}

						}
					}

				}
			}
			if (score < weakConnection) {
				for (BabelSense cSense : bs) {
					for (BabelSense vsense : bs1) {
						if (cSense.equals(vsense)) {
							score = score + k;
						}
					}
				}
			}

		}

		return score;
	}

	public int getMetaMapSimilarity() {
		for (String s : clinNotesObjs) {
			for (String w : voiceObjs) {
				int indexClin = s.indexOf("/");
				int indexClin1 = s.indexOf("|");
				int indexVoice = w.indexOf("/");
				int indexVoice1 = w.indexOf("|");
				if (s.equals(w)) {
					score = score + i + j + k;
				} else if (s.substring(0, indexClin).equals(w.substring(0, indexVoice))) {
					score = score + i;
				} else if (s.substring(indexClin, indexClin1).equals(w.substring(indexVoice, indexVoice1))) {
					score = score + j;
				} else if (s.substring(indexClin1, s.length()).equals(w.substring(indexVoice1, w.length()))) {
					score = score + k;
				} else {
					score = score + m;
				}

			}

		}
		return score;

	}

	public int getUMLSSimilarity(ArrayList<String> vNotes, ArrayList<String> cNotes) {
		
		// initialize the security service
		UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService())
				.getUtsWsSecurityControllerImplPort();
		String ticketGrantingTicket = null;
		try {
			ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
		} catch (gov.nih.nlm.uts.webservice.security.UtsFault_Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// use the ticketGrantingTicket to generate a Single Use Ticket with
		// each call
		String ticket = null;
		try {
			ticket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
		} catch (gov.nih.nlm.uts.webservice.security.UtsFault_Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		UtsWsContentController utsContentService = null;
		UtsWsSecurityController utsSecurityService;

		// instantiate and handshake
		try {
			utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();

			utsSecurityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
		}

		catch (Exception e) {
			System.out.println("Error!!!" + e.getMessage());
		}

		Psf myPsf = new Psf();
		List<ConceptRelationDTO> myConceptRelationsDTO = new ArrayList<ConceptRelationDTO>();
		ArrayList<String> superClassesCNO = new ArrayList<String>();
		for (int i = 0; i < cNotes.size(); i++) {
			try {
				ticket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
			} catch (gov.nih.nlm.uts.webservice.security.UtsFault_Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				myConceptRelationsDTO = utsContentService.getConceptConceptRelations(ticket, "2011AB",
						cNotes.get(i), myPsf);
			} catch (UtsFault_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i1 = 0; i1 < myConceptRelationsDTO.size(); i1++) {

				ConceptRelationDTO myConceptRelationDTO = myConceptRelationsDTO.get(i1);
				String otherConceptUi = myConceptRelationDTO.getRelatedConcept().getUi();
				String otherConceptName = myConceptRelationDTO.getRelatedConcept().getDefaultPreferredName();
				String otherConceptRel = myConceptRelationDTO.getRelationLabel();
				if (otherConceptRel.equals("IS")) {
					superClassesCNO.add(otherConceptName + "|" + otherConceptUi);
				}
			}
		
		}
		ArrayList<String> superClassesVNO = new ArrayList<String>();
		for (int i = 0; i < vNotes.size(); i++) {
			try {
				ticket = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
			} catch (gov.nih.nlm.uts.webservice.security.UtsFault_Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				myConceptRelationsDTO = utsContentService.getConceptConceptRelations(ticket, "2011AB",
						vNotes.get(i), myPsf);
			} catch (UtsFault_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i1 = 0; i1 < myConceptRelationsDTO.size(); i1++) {

				ConceptRelationDTO myConceptRelationDTO = myConceptRelationsDTO.get(i1);
				String otherConceptUi = myConceptRelationDTO.getRelatedConcept().getUi();
				String otherConceptName = myConceptRelationDTO.getRelatedConcept().getDefaultPreferredName();
				String otherConceptRel = myConceptRelationDTO.getRelationLabel();
				if (otherConceptRel.equals("IS")) {
					superClassesVNO.add(otherConceptName + "|" + otherConceptUi);
				}
			}
		}
		
		
		for(int z = 0; z< superClassesCNO.size(); z++)
		{
			for(int w = 0; w< superClassesVNO.size(); w++)
			{
				if(superClassesCNO.get(z).equals(superClassesVNO.get(w)))
				{
					simScore += i;
				}
			}
		}
	
		return simScore;
	}
	public int revisedSimilarity() throws IOException
	{
		int score = 0;
		//PerlTest p = new PerlTest();
		for(String s : clinNotesObjs)
		{
			for(String w: voiceObjs)
			{
				score = score + PerlTest.runPerlScript(s,w);
			}
			
		}
		return score;
	}
}
