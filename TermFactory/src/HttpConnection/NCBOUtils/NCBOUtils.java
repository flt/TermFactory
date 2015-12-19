package HttpConnection.NCBOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import DBConnection.MongoDBJDBC;
import model.LocalTerm;

public class NCBOUtils {
	static final String REST_URL = "http://data.bioontology.org";
	static final String API_KEY = "983b29ce-b158-4be7-b534-3412cf3f4e89";
	static final ObjectMapper mapper = new ObjectMapper();
	static final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
	private static JsonNode jsonToNode(String json) {
        JsonNode root = null;
        try {
            root = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    private static String get(String urlToGet) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToGet);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
            conn.setRequestProperty("Accept", "application/json");
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, String> listOntologies() {
        // Get the available resources
        String resourcesString = get(REST_URL + "/");
        JsonNode resources = jsonToNode(resourcesString);

        // Follow the ontologies link by looking for the media type in the list of links
        String link = resources.get("links").findValue("ontologies").asText();

        // Get the ontologies from the link we found
        JsonNode ontologies = jsonToNode(get(link));
        
        Map<String, String> ontologyAddress = new HashMap<String, String>();
        // Get the name and ontology id from the returned list
        List<String> ontNames = new ArrayList<String>();
        for (JsonNode ontology : ontologies) {
            ontNames.add(ontology.get("name").asText() + "\n" + ontology.get("@id").asText() + "\n\n");
            ontologyAddress.put(ontology.get("name").asText(), ontology.get("@id").asText());
        }

        // Print the names and ids
        for (Entry<String, String> pair: ontologyAddress.entrySet()) {
            System.out.println(pair.getKey() + " : " + pair.getValue());
        }
        return ontologyAddress;
    }

    public static List<JsonNode> listClassesInOntology(String ontologyURL) {
    	String resourcesString = get(ontologyURL);
        JsonNode resources = jsonToNode(resourcesString);

        // Follow the ontologies link by looking for the media type in the list of links
        String link = resources.get("links").findValue("classes").asText();

        // Get the ontologies from the link we found
        JsonNode classesPage = jsonToNode(get(link));

        // Get the number of classes
        String curPage = classesPage.get("page").asText();
        String totalPage = classesPage.get("pageCount").asText();
        System.out.println(curPage + " out of " + totalPage);
        ArrayList<JsonNode> classNodeList = new ArrayList<JsonNode>();
    	
        // Get the classes list
        for(int i = Integer.parseInt(curPage); i <= Integer.parseInt(totalPage); i++){
        	System.out.println(i);
        	JsonNode classesContent = jsonToNode(get(ontologyURL + "/classes?page=" + i)).get("collection");
        	for(JsonNode classContent : classesContent) {
        		System.out.println(classContent.get("prefLabel").asText());
        		System.out.println(classContent.get("@id").asText());
        		classNodeList.add(classContent);
        	}
        }
        System.out.println(classNodeList.size());
        return classNodeList;
    }
    
    public static int insertClassesInOntology(String ontologyURL) {
    	String resourcesString = get(ontologyURL);
        JsonNode resources = jsonToNode(resourcesString);

        // Follow the ontologies link by looking for the media type in the list of links
        String link = resources.get("links").findValue("classes").asText();

        // Get the ontologies from the link we found
        JsonNode classesPage = jsonToNode(get(link));

        // Get the number of classes
        String curPage = classesPage.get("page").asText();
        //String curPage = "5431";
        String totalPage = classesPage.get("pageCount").asText();
        System.out.println(curPage + " out of " + totalPage);
        int sum = 0;
        String ontologyName = ontologyURL.substring(ontologyURL.lastIndexOf("/") + 1);
    	MongoDBJDBC mongo = new MongoDBJDBC();
    	mongo.createConnection("localhost", 27017, null, null);
        // Get the classes list
        for(int i = Integer.parseInt(curPage); i <= Integer.parseInt(totalPage); i++){
        	System.out.println(i);
        	JsonNode classesContent = jsonToNode(get(ontologyURL + "/classes?page=" + i)).get("collection");
        	for(JsonNode classContent : classesContent) {
        		System.out.println(classContent.get("prefLabel").asText());
        		System.out.println(classContent.get("@id").asText());
        		String code = classContent.get("@id").asText();
        		code = code.substring(code.lastIndexOf("/") + 1);
        		LocalTerm lt = new LocalTerm(classContent.get("prefLabel").asText(), null);
        		List<String> result = mongo.searchData(ontologyName, lt.TermToJson());
        		if (result != null)
        			continue;
        		lt.addSource(ontologyName, code, classContent.get("@id").asText(), "bioPoratl");
        		mongo.insertData(ontologyName, lt.TermToJson());
        		sum += 1;
        	}
        }
        return sum;
    }
    
    public static List<JsonNode> listSubClasses(String classURL){
    	String resourcesString = get(classURL);
    	JsonNode resources = jsonToNode(resourcesString);
    	String sublink = resources.get("links").findValue("children").asText();
    	//Get the children from sublink
    	JsonNode subClassPage = jsonToNode(get(sublink));
    	//
    	String curPage = subClassPage.get("page").asText();
    	String totalPage = subClassPage.get("pageCount").asText();
    	System.out.println(curPage + " out of " + totalPage);
    	List<JsonNode> classNodeList = new ArrayList<JsonNode>();
    	
    	for(int i = Integer.parseInt(curPage); i <= Integer.parseInt(totalPage); i++){
    		System.out.println(i);
    		JsonNode classesContent = jsonToNode(get(sublink + "?page=" + i)).get("collection");
    		for(JsonNode classContent: classesContent){
    			System.out.println(classContent.get("prefLabel").asText());
    			System.out.println(classContent.get("@id").asText());
    			classNodeList.add(classContent);
    		}
    	}
    	System.out.println(classNodeList.size());
        return classNodeList;
    }
    
    public static List<JsonNode> listParentClasses(String classURL){
    	String resourcesString = get(classURL);
    	JsonNode resources = jsonToNode(resourcesString);
    	String parlink = resources.get("links").findValue("parents").asText();
    	//Get the children from sublink
    	JsonNode parClassPage = jsonToNode(get(parlink));
    	//
    	String curPage = parClassPage.get("page").asText();
    	String totalPage = parClassPage.get("pageCount").asText();
    	System.out.println(curPage + " out of " + totalPage);
    	List<JsonNode> classNodeList = new ArrayList<JsonNode>();
    	
    	for(int i = Integer.parseInt(curPage); i <= Integer.parseInt(totalPage); i++){
    		System.out.println(i);
    		JsonNode classesContent = jsonToNode(get(parlink + "?page=" + i)).get("collection");
    		for(JsonNode classContent: classesContent){
    			System.out.println(classContent.get("prefLabel").asText());
    			System.out.println(classContent.get("@id").asText());
    			classNodeList.add(classContent);
    		}
    	}
    	System.out.println(classNodeList.size());
        return classNodeList;
    }
    
    public static String getSingleValue(JsonNode node, String key){
    	if (node == null)
    		return null;
    	if (node.get(key) != null)
    		return node.get(key).asText();
    	return null;
    }
    
    public static List<String> getCollectionValue(JsonNode node, String key){
    	if(node == null)
    		return null;
    	if(node.get(key) != null){
    		JsonNode contentlist = node.get(key);
    		List<String> contents = new ArrayList<String>();
    		for(JsonNode content:contentlist){
    			contents.add(content.asText());
    		}
    		return contents;
    	}
    	return null;
    }
    
    public static List<JsonNode> searchClass(List<String> terms){
    	ArrayList<JsonNode> searchResults = new ArrayList<JsonNode>();
        for (String term : terms) {
            JsonNode searchResult = jsonToNode(get(REST_URL + "/search?q=" + term)).get("collection");
            searchResults.add(searchResult);
        }

        for (JsonNode result : searchResults) {
            try {
				System.out.println(writer.writeValueAsString(result));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
        }
        return searchResults;
    }
    
    public static JsonNode searchClass(String term){
    	JsonNode searchResult = jsonToNode(get(REST_URL + "search?q=" + term)).get("collection");
    	if(searchResult == null){
    		return null;
    	}
    	else{
    		return searchResult;
    	}
    }
    
    public static void main(String[] args){
    	//Map<String, String> ontoCatalogue = NCBOUtils.listOntologies();
    	//System.out.println(NCBOUtils.insertClassesInOntology("http://data.bioontology.org/ontologies/ICD10"));
    	//System.out.println(NCBOUtils.insertClassesInOntology("http://data.bioontology.org/ontologies/CPT"));
    	//System.out.println(NCBOUtils.insertClassesInOntology("http://data.bioontology.org/ontologies/LOINC"));
    	System.out.println(NCBOUtils.insertClassesInOntology("http://data.bioontology.org/ontologies/RXNORM"));
    	
    	//List<JsonNode> classList = NCBOUtils.listClassesInOntology("http://data.bioontology.org/ontologies/ICD10");
    	//String classURL = classList.get(0).get("@id").asText();
    	//List<JsonNode> subList = NCBOUtils.listSubClasses(classURL);
    	//List<JsonNode> parList = NCBOUtils.listParentClasses(classURL);
    }
}
