package HttpConnection.NCBOUtils;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ClassesSearch {

    //static final String REST_URL = "http://data.bioontology.org";
	static final String REST_URL = "http://bioportal.bioontology.org/ontologies/CPT";
    static final String API_KEY = "983b29ce-b158-4be7-b534-3412cf3f4e89";
    static final ObjectMapper mapper = new ObjectMapper();
    static final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    public static void main(String[] args) throws Exception {
        ArrayList<String> terms = new ArrayList<String>();

        String currentDir = System.getProperty("user.dir");
        Scanner in = new Scanner(new FileReader(currentDir + "/src/classes_search_terms_cpt.txt"));

        while (in.hasNextLine()) {
            terms.add(in.nextLine());
        }
        in.close();
    }
}

