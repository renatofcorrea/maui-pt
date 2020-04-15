/**
 * 
 */
package com.entopix.maui.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import javax.json.Json;

import javax.json.stream.JsonParser; // <-- alteração

//import org.springframework.boot.json.JsonParser; //<-- import original

import com.entopix.maui.util.Topic;

/** Realiza consultas à tesauro de CI em servidor tematres
 * @author Renato Correa
 *
 */

public class TBCI {
	private static boolean debugon = false;
	
	//usando urlbase, há diferença no id dos conceitos e mais metatermos
	private static String urlbase = "https://www.vocabularyserver.com/tbci/services.php?";
	private static String aurlbase = "http://www.uel.br/revistas/informacao/tbci/vocab/services.php?";
	/**
	 * @return metaterms of the thesaurus
	 */
	public static Map<String,Integer> getTBCITopCategories(){
		//urlbase+"task=fetchTopTerms&output=json"
		  
		  Map<String,Integer> res = null;
		  res = new HashMap <String,Integer>();
		  String string = "";
		  String term_id = "";
		  try {
		  URL  url = new URL(urlbase+"task=fetchTopTerms&output=json");
		  
		  URLConnection urlConn = url.openConnection(); 
		  urlConn.setRequestProperty("Accept-Charset", "UTF-8");
		  urlConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
		  urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

		  urlConn.setDoInput(true);
		  urlConn.setUseCaches(false);
		  Charset charset = Charset.forName("UTF-8");
		  InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), charset);//url.openStream();//java 1.7 required
		  JsonParser parser = Json.createParser(is); 
		  while (parser.hasNext()) {
		          JsonParser.Event e = parser.next();//javax.json.stream.JsonParsingException: Unexpected char 60 at (line no=1, column no=1, offset=0)

                  //System.out.println("Putz: "+e.toString());

		          if (e == JsonParser.Event.KEY_NAME) {
		              switch (parser.getString()) {
		                  case "term_id":
		                    parser.next();
		                    term_id= parser.getString();
		                    if(debugon){
		                    System.out.print(term_id);
		                    System.out.print(": ");
		                    }
		                    break;
		                case "string":
		                    parser.next();
		                    string = parser.getString();
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    res.put(string,Integer.valueOf(term_id));
		                    break;
		             }
		         }
		     }//while
		  parser.close();
		  is.close();
		     } catch (MalformedURLException e) {
		         e.printStackTrace();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }catch (Exception e) {
		         e.printStackTrace();
		     }
		  return res;

	}//end getTBCITopCategories

	/**
	 * Obtem os top concepts de um termo id
	 * @param termid
	 * @return Up terms of a given term specified by termid param
	 * Apresenta somente um termo geral quando o termos possui mais de um termo geral
	 * Por exemplo: id=381,string=bibliotecarios
	 */
	public static Map<String,Integer> getTBCITopConcepts(String termid){
		//Retrieve hierarquical structure for one ID//Não pega múltiplos BT
		//urlbase+"task=fetchUp&arg="+id
		//Termos relacionados:
		//Retrieve related terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchRelated&arg=1
		//Retrieve simple related term data for some coma separated terms IDs (example: 3,6,98)
		//https://www.vocabularyserver.com/tbci/services.php?task=fetchRelatedTerms&arg=80,84,1614&output=json
		//Termos específicos://somente os diretos
		//Retrieve more specific terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDown&arg=1
		//Termos que se relacionam diretamente://não pega NT, mas pega múltiplos BT diretos
		//Retrieve alternative, related and direct hieraquical terms for one term_id
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDirectTerms&arg=1

		  Map<String,Integer> res = null;
		  res = new HashMap <String,Integer>();
		  String string = "";
		  String term_id = "";
		  try {
		  URL  url = new URL(urlbase+"task=fetchUp&arg="+termid+"&output=json");
		  //URL url = new URL(urlbase+"task=fetchTopTerms&output=json");
		  URLConnection urlConn = url.openConnection(); 
		  urlConn.setRequestProperty("Accept-Charset", "UTF-8");
		  urlConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
		  urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

		  urlConn.setDoInput(true);
		  urlConn.setUseCaches(false);
		  Charset charset = Charset.forName("UTF-8");
		  InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), charset);//url.openStream();//java 1.7 required
		  JsonParser parser = Json.createParser(is); 
		  while (parser.hasNext()) {
		          JsonParser.Event e = parser.next();//javax.json.stream.JsonParsingException: Unexpected char 60 at (line no=1, column no=1, offset=0)

                 // System.out.println("Putz: "+e.toString());

		          if (e == JsonParser.Event.KEY_NAME) {
		              switch (parser.getString()) {
		                  case "term_id":
		                    parser.next();
		                    term_id= parser.getString();
		                    if(debugon){
		                    System.out.print(term_id);
		                    System.out.print(": ");
		                    }
		                    break;
		                case "string":
		                    parser.next();
		                    string = parser.getString();
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    res.put(string,Integer.valueOf(term_id));
		                    break;
		             }
		         }
		     }//while
		  parser.close();
		  is.close();
		     } catch (MalformedURLException e) {
		         e.printStackTrace();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }catch (Exception e) {
		         e.printStackTrace();
		     }
		  return res;

	}//end getTBCITopCategories


	/**
	 * @param term string before () or :
	 * @return Term data for specified term param
	 */
	public static Map<String,Integer> getTBCITerm(String term){
		//Search and retrieve terms mapped in target vocabulary for a given term
		//urlbase+"task=fetchSourceTerms&arg="+term (não retorna nada)
		//Search and retrieve similar term for string search expression ($arg)
		//urlbase+"task=fetchSimilar&arg="+term (retorna somente o termo como string)
		//Search and retrieve terms using exact matching
		//https://www.vocabularyserver.com/tbci/services.php?task=fetch&arg=pesquisa&output=json (busca exata)
		//Usando pois json sem char(60) :
		//Search and retrieve terms
		//https://www.vocabularyserver.com/tbci/services.php?task=search&arg="+term+"&output=json"
		  Map<String,Integer> res = null;
		  res = new HashMap <String,Integer>();
		  String string = "";
		  String term_id = "";
		  try {
			  URL  url = new URL(urlbase+"task=search&arg="+URLEncoder.encode(term,"UTF-8")+"&output=json");//só funciona se url sem parametro!
		  
		  URLConnection urlConn = url.openConnection(); 
		  urlConn.setRequestProperty("Accept-Charset", "UTF-8");
		  urlConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
		  urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

		  urlConn.setDoInput(true);
		  urlConn.setUseCaches(false);
		  Charset charset = Charset.forName("UTF-8");
		  InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), charset);//url.openStream();//java 1.7 required
		  JsonParser parser = Json.createParser(is); 
		  boolean ex  = false;
		  while (parser.hasNext()) {
		          JsonParser.Event e = parser.next();//javax.json.stream.JsonParsingException: Unexpected char 60 at (line no=1, column no=1, offset=0)

                  //System.out.println("Putz: "+e.toString());
		          if(ex)
		        	  break;

		          if (e == JsonParser.Event.KEY_NAME) {
		              switch (parser.getString()) {
		                  case "term_id":
		                    parser.next();
		                    term_id= parser.getString();
		                    if(debugon){
		                    System.out.print(term_id);
		                    System.out.print(": ");
		                    }
		                    break;
		                case "string":
		                    parser.next();
		                    string = parser.getString();
		                    if(startsWithIgnoreCase(string,term)) {
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    res.put(string,Integer.valueOf(term_id));
		                    ex = true;}
		                    break;
		             }
		         }
		     }//while
		  parser.close();
		  is.close();
		     } catch (MalformedURLException e) {
		         e.printStackTrace();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }catch (Exception e) {
		         e.printStackTrace();
		     }
		  return res;

	} //end getTBCITerm

	/**
	 * Gets the term data for the specified term ID.
	 * @param idterm
	 * @return the term data
	 */
	public static Entry<String,Integer> getTBCITerm(int idterm){
		//Retrieve simple term data
		//https://www.vocabularyserver.com/tbci/services.php?task=fetchTerm&arg=84&output=json		  
		  Map<String,Integer> res = null;
		  res = new HashMap <String,Integer>();
		  String string = "";
		  String term_id = "";
		  try {
			  URL  url = new URL(urlbase+"task=fetchTerm&arg="+idterm+"&output=json");//só funciona se url sem parametro!
		  
		  URLConnection urlConn = url.openConnection(); 
		  urlConn.setRequestProperty("Accept-Charset", "UTF-8");
		  urlConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
		  urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

		  urlConn.setDoInput(true);
		  urlConn.setUseCaches(false);
		  Charset charset = Charset.forName("UTF-8");
		  InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), charset);//url.openStream();//java 1.7 required
		  JsonParser parser = Json.createParser(is); 
		  boolean ex  = false;
		  while (parser.hasNext()) {
		          JsonParser.Event e = parser.next();//javax.json.stream.JsonParsingException: Unexpected char 60 at (line no=1, column no=1, offset=0)

                  //System.out.println("Putz: "+e.toString());
		          if(ex)
		        	  break;

		          if (e == JsonParser.Event.KEY_NAME) {
		              switch (parser.getString()) {
		                  case "term_id":
		                    parser.next();
		                    term_id= parser.getString();
		                    if(debugon){
		                    System.out.print(term_id);
		                    System.out.print(": ");
		                    }
		                    break;
		                case "string":
		                    parser.next();
		                    string = parser.getString();
		          
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    res.put(string,Integer.valueOf(term_id));
		                    ex = true;
		                    break;
		             }
		         }
		     }//while
		  parser.close();
		  is.close();
		     } catch (MalformedURLException e) {
		         e.printStackTrace();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }catch (Exception e) {
		         e.printStackTrace();
		     }
		  return res.entrySet().iterator().next();

	}//end getTBCITopCategories

	
	/**
	 * @param term string before () or :
	 * @return Terms data that contain the string in term param
	 */
	public static Map<String,Integer> getTBCITerms(String term){
		//Simple search and retrieve terms who start with string (only string)	
		//http://www.vocabularyserver.com/tbci/services.php?task=suggest&arg=pea
		//Search and retrieve terms who start with string (term_id, term, and more data)
		//https://www.vocabularyserver.com/tbci/services.php?task=suggestDetails&arg=pesquisa&output=json
		//usando pois json sem char(60):
		////https://www.vocabularyserver.com/tbci/services.php?task=search&arg=pesquisa&output=json
				  
		  Map<String,Integer> res = null;
		  res = new HashMap <String,Integer>();
		  String string = "";
		  String term_id = "";
		  try {
		  URL  url = new URL(urlbase+"task=search&arg="+URLEncoder.encode(term,"UTF-8")+"&output=json");//só funciona se url sem parametro!
		  
		  URLConnection urlConn = url.openConnection(); 
		  urlConn.setRequestProperty("Accept-Charset", "UTF-8");
		  urlConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
		  urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

		  urlConn.setDoInput(true);
		  urlConn.setUseCaches(false);
		  Charset charset = Charset.forName("UTF-8");
		  InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), charset);//url.openStream();//java 1.7 required
		  JsonParser parser = Json.createParser(is); 
		  while (parser.hasNext()) {
		          JsonParser.Event e = parser.next();//javax.json.stream.JsonParsingException: Unexpected char 60 at (line no=1, column no=1, offset=0)

                  //System.out.println("Putz: "+e.toString());

		          if (e == JsonParser.Event.KEY_NAME) {
		              switch (parser.getString()) {
		                  case "term_id":
		                    parser.next();
		                    term_id= parser.getString();
		                    if(debugon){
		                    System.out.print(term_id);
		                    System.out.print(": ");
		                    }
		                    break;
		                case "string":
		                    parser.next();
		                    string = parser.getString();
		                    if(startsWithIgnoreCase(string,term)) {
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    res.put(string,Integer.valueOf(term_id));}
		                    break;
		             }
		         }
		     }//while
		  parser.close();
		  is.close();
		     } catch (MalformedURLException e) {
		         e.printStackTrace();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }catch (Exception e) {
		         e.printStackTrace();
		     }
		  return res;

	}//end getTBCITopCategories

	
	/**
	 * @param terms string
	 * @param filter 
	 * @return Up terms of a given set of term specified by terms string @param, <b> ordered descendant by frequency of occurrence
	 */
	public static ArrayList<Entry<String,Integer>> getTBCITopConceptsCount(String[] terms){
		//res term id e count
		HashMap<String,Integer> res = new HashMap<String,Integer>();
		
		Map<String, Integer> term = null;
		Integer termID = null;
		String strTermID = null;
		
		Set<Entry<String, Integer>> set = null;
		Iterator<Entry<String, Integer>> it = null;
		Entry<String, Integer> entry = null;
		
		for (int i = 0 ; i < terms.length ; i++) {
			
			term = getTBCITerm(terms[i]);
			//termID = term.get(terms[i]);//para casamento exato entre string
			termID = term.values().iterator().next();//permite casamento parcial
			
			if (termID == null) {
				System.out.println("termo não é do tbci: " + terms[i]);
				continue;
			} else {
				strTermID = termID.toString();
				//obter topterms
				if (!term.isEmpty()) term.putAll(getAllBroaderConcepts(strTermID)); //getTBCITopConcepts(sid);
				
				//incluindo o próprio termo
				
				//verificar se term incluindo no contador
				set = term.entrySet();
			    it = set.iterator();
			    while (it.hasNext()) {
			    	entry = (Entry) it.next();
			    	if (debugon) System.out.println(entry.getKey() + "\t\t"+entry.getValue());
				    termID = entry.getValue();//term id
				    strTermID = termID.toString();
				    if (res.get(termID.toString()) == null) {
				    	//não incluido
						res.put(strTermID, new Integer(1));
					} else {
						res.put(strTermID, new Integer(res.get(termID.toString()).intValue()+1));
					}      
				}
			}
		}
		
		//ordenando descendente de freq
		ArrayList<Map.Entry<String,Integer>> res5 = new ArrayList<>(res.entrySet());
		Collections.sort(res5, new Comparator<Entry<String, Integer>>() {
			   public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2){
			       return o2.getValue().compareTo(o1.getValue()); // natural order return o1.getValue().compareTo(o2.getValue());
			   }
			});
		/*removendo com freq = 1
		res5.removeIf(new Predicate<Entry<String, Integer>>() {
			public boolean test(Entry<String,Integer> t) { 
				return (t.getValue() < 2);
			}
		});*/
		
		//removendo não-topcategories
		final Collection<Integer>topids = getTBCITopCategories().values();
		res5.removeIf(new Predicate<Entry<String, Integer>>() {
			public boolean test(Entry<String,Integer> t) { 
				return (!topids.contains(Integer.parseInt(t.getKey())));
			}
		});
		return res5;
	}
	
	/**
	 * @param terms string
	 * @param filter 
	 * @return Up terms of a given set of term specified by terms string @param, ordered descendant by frequency of ocorrence 
	 */
	public static ArrayList<Map.Entry<String,Integer>> getTBCIMetaConceptsCount(String[] terms){
		//res termid e count
		HashMap<String,Integer> res = new HashMap<String,Integer> () ;
		for(int i=0;i<terms.length;i++){
			//localizar termo no tbci
			
			Map<String, Integer> t=null;
			Integer id = null;
			try {
				t = getTBCITerm(URLEncoder.encode(terms[i], "UTF-8"));
				id= t.get(terms[i]);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(id == null){
				System.out.println("termo não é do tbci:"+terms[i]);
				continue;
			}
			else{//termo do tbci
				String sid = id.toString();
				//obter topterms
				if(!t.isEmpty())
				t.putAll(getTBCIMetaConcepts(sid)); //getTBCITopConcepts(sid);
				//incluindo o próprio termo
				
				//verificar se term incluindo no contador
				Set<Entry<String, Integer>> set = t.entrySet();
			    Iterator it = set.iterator();
			    while(it.hasNext()){
				      Entry<String, Integer> entry = (Entry)it.next();
				      if(debugon)
				    	  System.out.println(entry.getKey() + "\t\t"+entry.getValue());
				      id = entry.getValue();//term id
				      sid = id.toString();
						if(res.get(id.toString())==null){
							//não incluido
							res.put(sid, new Integer(1));
						}else{
							res.put(sid, new Integer(res.get(id.toString()).intValue()+1));
						}
				      
				    }
				
			}
		}
		//ordenando
		ArrayList<Map.Entry<String,Integer>> res5 = new ArrayList<>(res.entrySet());
		Collections.sort(res5, new Comparator<Entry<String, Integer>>(){
			   public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2){
			       return o2.getValue().compareTo(o1.getValue()); // natural order return o1.getValue().compareTo(o2.getValue());
			   }
			});
		res5.removeIf( new Predicate<Entry<String, Integer>> (){ public boolean test(Entry<String,Integer> t){ if(t.getValue() < 2) return true; else return false;}});
		return res5;
	}
		
	/**
	 * Obtem termos diretamente relacionados a um termo id (gerais,específicos e relacionados)
	 * @param termid
	 * @return Up terms of a given term specified by termid param
	 * Apresenta todos os termos gerais, quando possui mais de um termo geral
	 * Por exemplo: id=381,string=bibliotecarios
	 */
	public static Map<String,Integer> getDirectRelations(String termid){
		//Retrieve hierarquical structure for one ID
		//urlbase+"task=fetchUp&arg="+id
		//Termos relacionados:
		//Retrieve related terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchRelated&arg=1
		//Retrieve simple related term data for some coma separated terms IDs (example: 3,6,98)
		//https://www.vocabularyserver.com/tbci/services.php?task=fetchRelatedTerms&arg=80,84,1614&output=json
		//Termos específicos://somente os diretos
		//Retrieve more specific terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDown&arg=1
		//Termos que se relacionam diretamente://não funciona char(60) não pega NT, mas pega múltiplos BT diretos
		//Retrieve alternative, related and direct hieraquical terms for one term_id
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDirectTerms&arg=1

		  Map<String,Integer> res = null;
		  res = new HashMap <String,Integer>();
		  //-coment*/
		  String string = "";
		  String term_id = "";
		  String relation = "";
		  try {
		  //URL  url = new URL(urlbase+"task=fetchDirectTerms&arg="+termid+"&output=json");
		  URL  url = new URL(urlbase+"task=fetchDirectTerms&arg="+termid+"&output=json");
		  //URL url = new URL(urlbase+"task=fetchTopTerms&output=json");
		  URLConnection urlConn = url.openConnection(); 
		  urlConn.setRequestProperty("Accept-Charset", "UTF-8");
		  urlConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
		  urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

		  urlConn.setDoInput(true);
		  urlConn.setUseCaches(false);
		  Charset charset = Charset.forName("UTF-8");
		  InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), charset);//url.openStream();//java 1.7 required
 		  String s = getStringFromInputStreamReader(is);
		  
		  Reader isr = new StringReader(s);

		  JsonParser parser = Json.createParser(isr); 
		  while (parser.hasNext()) {
		          JsonParser.Event e = parser.next();//javax.json.stream.JsonParsingException: Unexpected char 60 at (line no=1, column no=1, offset=0)

                 // System.out.println("Putz: "+e.toString());

		          if (e == JsonParser.Event.KEY_NAME) {
		              switch (parser.getString()) {
		                  case "term_id":
		                    parser.next();
		                    term_id= parser.getString();
		                    if(debugon){
		                    System.out.print(term_id);
		                    System.out.print(": ");
		                    }
		                    break;
		                case "string":
		                    parser.next();
		                    string = parser.getString();
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    break;
		                case "relation_type_id":
		                	parser.next();
		                    relation = parser.getString();
		                    //2-RT //3-BT 
		                    if(relation.equalsIgnoreCase("2")||relation.equalsIgnoreCase("3"))
		                    res.put(string,Integer.valueOf(term_id));
		                    break;
		             }
		         }
		     }//while
		  parser.close();
		  is.close();
		  isr.close();
		     } catch (MalformedURLException e) {
		         e.printStackTrace();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }catch (Exception e) {
		         e.printStackTrace();
		     }
		  	  
		  //res.putAll(getTBCITopConcepts(termid));
		  res.putAll(getTBCINarrowConcepts(termid));//adicionado pq o servidor não está retornando termos específicos
		  //res.putAll(getTBCIRelatedConcepts(termid));
		  
		  return res;

	}//end getDirectRelations

	

	/**
	 * Obtem termos gerais diretamente relacionados a um termo id
	 * @param termid
	 * @return Up terms of a given term specified by termid param
	 * Apresenta todos os termos gerais, quando possui mais de um termo geral
	 * Por exemplo: id=381,string=bibliotecarios
	 */
	public static Map<String,Integer> getTBCIMetaConcepts(String termid){
		//Retrieve hierarquical structure for one ID
		//urlbase+"task=fetchUp&arg="+id
		//Termos relacionados:
		//Retrieve related terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchRelated&arg=1
		//Retrieve simple related term data for some coma separated terms IDs (example: 3,6,98)
		//https://www.vocabularyserver.com/tbci/services.php?task=fetchRelatedTerms&arg=80,84,1614&output=json
		//Termos específicos://somente os diretos
		//Retrieve more specific terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDown&arg=1
		//Termos que se relacionam diretamente://não funciona char(60) não pega NT, mas pega múltiplos BT diretos
		//Retrieve alternative, related and direct hieraquical terms for one term_id
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDirectTerms&arg=1

		  Map<String,Integer> res = null;
		  res = new HashMap <String,Integer>();
		  //-coment*/
		  String string = null;
		  String term_id = null;
		  String relation = null;
		  String isMetaTerm = null;
		  try {
		  //URL  url = new URL(urlbase+"task=fetchDirectTerms&arg="+termid+"&output=json");
		  URL  url = new URL(urlbase+"task=fetchDirectTerms&arg="+termid+"&output=json");
		  //URL url = new URL(urlbase+"task=fetchTopTerms&output=json");
		  URLConnection urlConn = url.openConnection(); 
		  urlConn.setRequestProperty("Accept-Charset", "UTF-8");
		  urlConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
		  urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

		  urlConn.setDoInput(true);
		  urlConn.setUseCaches(false);
		  Charset charset = Charset.forName("UTF-8");
		  InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), charset);//url.openStream();//java 1.7 required
 		  String s = getStringFromInputStreamReader(is);
 		  is.close();
		  Reader isr = new StringReader(s);

		  JsonParser parser = Json.createParser(isr); 
		  while (parser.hasNext()) {
		          JsonParser.Event e = parser.next();//javax.json.stream.JsonParsingException: Unexpected char 60 at (line no=1, column no=1, offset=0)

                 // System.out.println("Putz: "+e.toString());

		          if (e == JsonParser.Event.KEY_NAME) {
		              switch (parser.getString()) {
		                  case "term_id":
		                    parser.next();
		                    term_id= parser.getString();
		                    if(debugon){
		                    System.out.print(term_id);
		                    System.out.print(": ");
		                    }
		                    break;
		                case "string":
		                    parser.next();
		                    string = parser.getString();
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    break;
		                case "code":
		                    parser.next();
		                    isMetaTerm = parser.getString();
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    break;    
		                case "relation_type_id":
		                	parser.next();
		                    relation = parser.getString();
		                    //2-RT //3-BT 
		                    if(relation.equalsIgnoreCase("3")){
		                    	if(!isMetaTerm.isEmpty())
		                    		res.put(string,Integer.parseInt(term_id));
		                    	else{
		                    		res.putAll(getTBCIMetaConcepts(term_id));
		                    	}
		                    	
		                    }
		                    break;
		             }
		         }
		     }//while
		  parser.close();
		  isr.close();
		     } catch (MalformedURLException e) {
		         e.printStackTrace();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }catch (Exception e) {
		         e.printStackTrace();
		     }
		   
		  return res;

	}//end getTBCIMetaConcepts
	
	/**
	 * Obtem termos gerais diretamente relacionados a um termo id
	 * @param termid
	 * @return Up terms of a given term specified by termid param
	 * Apresenta todos os termos gerais, quando possui mais de um termo geral
	 * Por exemplo: id=381,string=bibliotecarios
	 */
	public static Map<String,Integer> getTBCIBroaderConcepts(String termid){
		//Retrieve hierarquical structure for one ID
		//urlbase+"task=fetchUp&arg="+id
		//Termos relacionados:
		//Retrieve related terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchRelated&arg=1
		//Retrieve simple related term data for some coma separated terms IDs (example: 3,6,98)
		//https://www.vocabularyserver.com/tbci/services.php?task=fetchRelatedTerms&arg=80,84,1614&output=json
		//Termos específicos://somente os diretos
		//Retrieve more specific terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDown&arg=1
		//Termos que se relacionam diretamente://não funciona char(60) não pega NT, mas pega múltiplos BT diretos
		//Retrieve alternative, related and direct hieraquical terms for one term_id
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDirectTerms&arg=1

		  Map<String,Integer> res = null;
		  res = new HashMap <String,Integer>();
		  //-coment*/
		  String string = "";
		  String term_id = "";
		  String relation = "";
		  try {
		  //URL  url = new URL(urlbase+"task=fetchDirectTerms&arg="+termid+"&output=json");
		  URL  url = new URL(urlbase+"task=fetchDirectTerms&arg="+termid+"&output=json");
		  //URL url = new URL(urlbase+"task=fetchTopTerms&output=json");
		  URLConnection urlConn = url.openConnection(); 
		  urlConn.setRequestProperty("Accept-Charset", "UTF-8");
		  urlConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
		  urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

		  urlConn.setDoInput(true);
		  urlConn.setUseCaches(false);
		  Charset charset = Charset.forName("UTF-8");
		  InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), charset);//url.openStream();//java 1.7 required
 		  String s = getStringFromInputStreamReader(is);
		  
		  Reader isr = new StringReader(s);

		  JsonParser parser = Json.createParser(isr); 
		  while (parser.hasNext()) {
		          JsonParser.Event e = parser.next();//javax.json.stream.JsonParsingException: Unexpected char 60 at (line no=1, column no=1, offset=0)

                 // System.out.println("Putz: "+e.toString());

		          if (e == JsonParser.Event.KEY_NAME) {
		              switch (parser.getString()) {
		                  case "term_id":
		                    parser.next();
		                    term_id= parser.getString();
		                    if(debugon){
		                    System.out.print(term_id);
		                    System.out.print(": ");
		                    }
		                    break;
		                case "string":
		                    parser.next();
		                    string = parser.getString();
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    break;
		                case "relation_type_id":
		                	parser.next();
		                    relation = parser.getString();
		                    //2-RT //3-BT 
		                    if(relation.equalsIgnoreCase("3"))
		                    res.put(string,Integer.valueOf(term_id));
		                    break;
		             }
		         }
		     }//while
		  parser.close();
		  is.close();
		  isr.close();
		     } catch (MalformedURLException e) {
		         e.printStackTrace();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }catch (Exception e) {
		         e.printStackTrace();
		     }
		   
		  return res;

	}//end getTBCIBroaderConcepts


	/**
	 * Obtem todos os termos gerais diretamente de um termo id
	 * @param termid
	 * @return Up terms of a given term specified by termid param
	 * Apresenta todos os termos gerais, quando possui mais de um termo geral
	 * Por exemplo: id=381,string=bibliotecarios
	 */
	public static Map<String,Integer> getAllBroaderConcepts(String termid){
		
		Map<String,Integer> res = getTBCIBroaderConcepts(termid);
		if(res.isEmpty())
			return res;
		else{
			Map<String,Integer> res2= null;
			res2 = new HashMap <String,Integer>();
			Set<Entry<String, Integer>> set = res.entrySet();
		    Iterator<Entry<String, Integer>> it = set.iterator();
		    while(it.hasNext()){
			      Entry<String, Integer> entry = (Entry)it.next();
			      //System.out.println(entry.getKey() + "\t\t"+entry.getValue());
			      //res2.put(entry.getKey(),entry.getValue());
			      res2.putAll(getAllBroaderConcepts(entry.getValue().toString()));
			    }
		    res.putAll(res2);
			return res;
		}
	}
	
	//Elimina char BOM e erro em double quotes
	private static String getStringFromInputStreamReader(InputStreamReader is)
			throws IOException {
		BufferedReader br = new BufferedReader(is);

          String s="",t = null; // primeira linha

          while ((t = br.readLine()) != null) {
              //System.out.println(s);
              s += t+" ";
          }

          br.close();
          //remove BOM
          s= s.replace("\uFEFF", "");
          s = s.trim();//remove espaço adicionado a mais
          s= s.replaceAll("[“”]","\"");
          return s;
	}
	
	public static String[] getKeywords(ArrayList<Topic> keywords) {
		ArrayList<String> res = new ArrayList<String>();
		 for (Topic keyword : keywords) {
			 res.add(keyword.getTitle());
             System.out.println("Keyword: " + keyword.getTitle() + " " + keyword.getProbability());
         }
        
		return res.toArray(new String[0]);
	}

	/**
	 * Obtem os narrow concepts de um termo id
	 * @param termid
	 * @return Up terms of a given term specified by termid param
	 * Apresenta somente um termo geral quando o termos possui mais de um termo geral
	 * Por exemplo: id=381,string=bibliotecarios
	 */
	public static Map<String,Integer> getTBCINarrowConcepts(String termid){
		//Retrieve hierarquical structure for one ID//Não pega múltiplos BT
		//urlbase+"task=fetchUp&arg="+id
		//Termos relacionados:
		//Retrieve related terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchRelated&arg=1
		//Retrieve simple related term data for some coma separated terms IDs (example: 3,6,98)
		//https://www.vocabularyserver.com/tbci/services.php?task=fetchRelatedTerms&arg=80,84,1614&output=json
		//Termos específicos://somente os diretos
		//Retrieve more specific terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDown&arg=1
		//Termos que se relacionam diretamente://não pega NT, mas pega múltiplos BT diretos
		//Retrieve alternative, related and direct hieraquical terms for one term_id
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDirectTerms&arg=1

		  Map<String,Integer> res = null;
		  res = new HashMap <String,Integer>();
		  String string = "";
		  String term_id = "";
		  try {
		  URL  url = new URL(urlbase+"task=fetchDown&arg="+termid+"&output=json");
		  //URL url = new URL(urlbase+"task=fetchTopTerms&output=json");
		  URLConnection urlConn = url.openConnection(); 
		  urlConn.setRequestProperty("Accept-Charset", "UTF-8");
		  urlConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
		  urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

		  urlConn.setDoInput(true);
		  urlConn.setUseCaches(false);
		  Charset charset = Charset.forName("UTF-8");
		  InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), charset);//url.openStream();//java 1.7 required
		  JsonParser parser = Json.createParser(is); 
		  while (parser.hasNext()) {
		          JsonParser.Event e = parser.next();//javax.json.stream.JsonParsingException: Unexpected char 60 at (line no=1, column no=1, offset=0)

                 // System.out.println("Putz: "+e.toString());

		          if (e == JsonParser.Event.KEY_NAME) {
		              switch (parser.getString()) {
		                  case "term_id":
		                    parser.next();
		                    term_id= parser.getString();
		                    if(debugon){
		                    System.out.print(term_id);
		                    System.out.print(": ");
		                    }
		                    break;
		                case "string":
		                    parser.next();
		                    string = parser.getString();
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    res.put(string,Integer.valueOf(term_id));
		                    break;
		             }
		         }
		     }//while
		  parser.close();
		  is.close();
		     } catch (MalformedURLException e) {
		         e.printStackTrace();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }catch (Exception e) {
		         e.printStackTrace();
		     }
		  return res;

	}//end getTBCINarrowConcepts

	/**
	 * Obtem os related concepts de um termo id
	 * @param termid
	 * @return Up terms of a given term specified by termid param
	 * Apresenta somente um termo geral quando o termos possui mais de um termo geral
	 * Por exemplo: id=381,string=bibliotecarios
	 */
	public static Map<String,Integer> getTBCIRelatedConcepts(String termid){
		//Retrieve hierarquical structure for one ID//Não pega múltiplos BT
		//urlbase+"task=fetchUp&arg="+id
		//Termos relacionados:
		//Retrieve related terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchRelated&arg=1
		//Retrieve simple related term data for some coma separated terms IDs (example: 3,6,98)
		//https://www.vocabularyserver.com/tbci/services.php?task=fetchRelatedTerms&arg=80,84,1614&output=json
		//Termos específicos://somente os diretos
		//Retrieve more specific terms for one ID
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDown&arg=1
		//Termos que se relacionam diretamente://não pega NT, mas pega múltiplos BT diretos
		//Retrieve alternative, related and direct hieraquical terms for one term_id
		//http://www.vocabularyserver.com/tbci/services.php?task=fetchDirectTerms&arg=1

		  Map<String,Integer> res = null;
		  res = new HashMap <String,Integer>();
		  String string = "";
		  String term_id = "";
		  try {
		  URL  url = new URL(urlbase+"task=fetchRelated&arg="+termid+"&output=json");
		  //URL url = new URL(urlbase+"task=fetchTopTerms&output=json");
		  URLConnection urlConn = url.openConnection(); 
		  urlConn.setRequestProperty("Accept-Charset", "UTF-8");
		  urlConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
		  urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

		  urlConn.setDoInput(true);
		  urlConn.setUseCaches(false);
		  Charset charset = Charset.forName("UTF-8");
		  InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), charset);//url.openStream();//java 1.7 required
		  JsonParser parser = Json.createParser(is); 
		  while (parser.hasNext()) {
		          JsonParser.Event e = parser.next();//javax.json.stream.JsonParsingException: Unexpected char 60 at (line no=1, column no=1, offset=0)

                 // System.out.println("Putz: "+e.toString());

		          if (e == JsonParser.Event.KEY_NAME) {
		              switch (parser.getString()) {
		                  case "term_id":
		                    parser.next();
		                    term_id= parser.getString();
		                    if(debugon){
		                    System.out.print(term_id);
		                    System.out.print(": ");
		                    }
		                    break;
		                case "string":
		                    parser.next();
		                    string = parser.getString();
		                    if(debugon){
		                    System.out.println(string);
		                    System.out.println("---------");
		                    }
		                    res.put(string,Integer.valueOf(term_id));
		                    break;
		             }
		         }
		     }//while
		  parser.close();
		  is.close();
		     } catch (MalformedURLException e) {
		         e.printStackTrace();
		     } catch (IOException e) {
		         e.printStackTrace();
		     }catch (Exception e) {
		         e.printStackTrace();
		     }
		  return res;

	}//end getTBCIRelatedConcepts

    /**
     * @param str    a String
     * @param prefix a prefix
     * @return true if {@code start} starts with {@code prefix}, disregarding case sensitivity
     */
    private static boolean startsWithIgnoreCase(String str, String prefix)
    {
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    private static boolean endsWithIgnoreCase(String str, String suffix)
    {
        int suffixLength = suffix.length();
        return str.regionMatches(true, str.length() - suffixLength, suffix, 0, suffixLength);
    }

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//testando interface com tbci no tematres
				Map<String,Integer> res = getTBCITopCategories();
				Map<String,Integer> res2 = getTBCITerm("usabilidade");
				Map<String,Integer> res3 = getTBCITerms("publicações científicas");
				//Map<String,Integer> res4 = getTBCITopConcepts(res3.get("usabilidade").toString());
				Map<String,Integer> res4 = getTBCITopConcepts(res3.values().iterator().next().toString());
				System.out.println("Termos gerais para tag: pesquisa");
			    Set<Entry<String, Integer>> set = res4.entrySet();
			    Iterator it = set.iterator();
			   
			    System.out.println("Código\t\tValor");
			    //getKey() - recupera a chave do mapa 
			    //getValue() - recupera o valor do mapa
			 
			    while(it.hasNext()){
			      Entry<String, Integer> entry = (Entry)it.next();
			      System.out.println(entry.getKey() + "\t\t"+entry.getValue());
			    }
				
				String [] tags = new String[]{"conhecimento nas organizações",//1
						"gestão da informação",//gi
						"transferência da informação",//Comunicação e Acesso à Informação
						"gestão do conhecimento",//2
						"inteligência competitiva"};//3
						//new String[]{"pesquisa","agências de fomento","bolsas de pesquisa","formação profissional","bibliotecários"};
				System.out.print("Contagem de termos gerais para as "+tags.length+" tags: ");
				for(String tag: tags){
					System.out.print(tag+"; ");
				}
				System.out.println("");//capturando somente 3 de 5 porque os com contagem 1 são removidos
				ArrayList<Map.Entry<String,Integer>> res5 = getTBCITopConceptsCount(tags);//getTBCIMetaConceptsCount(tags);//getTBCITopConceptsCount(tags);
				System.out.println("Top Frequent Concept id= "+res5.get(0).getKey()+",freq= "+res5.get(0).getValue());
				System.out.println("Top Frequent Concept label= "+getTBCITerm(Integer.parseInt(res5.get(0).getKey())).getKey() );
				
				System.out.println("Top Frequent Concepts");
			    
			    it = res5.iterator();
			   
			    System.out.println("Código\tValor\tTermo");
			    //getKey() - recupera a chave do mapa 
			    //getValue() - recupera o valor do mapa
			 
			    while(it.hasNext()){
			      Entry<String, Integer> entry = (Entry)it.next();
			      System.out.println(entry.getKey() + "\t"+entry.getValue() + "\t"+ getTBCITerm(Integer.parseInt(entry.getKey())).getKey());
			    }
			    
			    
			    //Get direct relations
			    //O termo bibliotecários tem dois Términos genéricos:
			    //TG↑ pessoal da biblioteca
			    //TG↑ profissionais de informação
			    //só capturado o primeiro em ordem alfabética, bug do tbci
			    System.out.println("Relações diretas do Termo 381: bibliotecários.");
			    res4 = getDirectRelations("381");
			    //res4 = getDirectRelations("337");//uel
			    set = res4.entrySet();
			    it = set.iterator();
				   
			    while(it.hasNext()){
				      Entry<String, Integer> entry = (Entry)it.next();
				      System.out.println(entry.getKey() + "\t\t"+entry.getValue());
				    }
			    
			    System.out.println("Termos gerais do Termo 381: bibliotecários.");
			    res4 = getTBCIMetaConcepts("381");
			    //res4 = getAllBroaderConcepts("381");
			    //res4 = getDirectRelations("337");//uel
			    set = res4.entrySet();
			    it = set.iterator();
				   
			    while(it.hasNext()){
				      Entry<String, Integer> entry = (Entry)it.next();
				      System.out.println(entry.getKey() + "\t\t"+entry.getValue());
				    }
	}

}//end class TBCI


