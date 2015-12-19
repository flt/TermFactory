package NLP;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import NLP.GlobalDef;

public class AnsjStopWord {
	// get stopword 1
    @SuppressWarnings("rawtypes")
    public Map<String, List> ansjStopWord() throws Exception {
 
        Map<String, List> map = new HashMap<String, List>();
 
        String path_chinese = "resources/newWord/chinese_stopword.txt";
        String path_english = "resources/newWord/english_stopword.txt";
 
        AnsjStopWord AnsjStopWord = new AnsjStopWord();
 
        List<String> list_c = AnsjStopWord.readStopWord(path_chinese);
        List<String> list_e = AnsjStopWord.readStopWord(path_english);
 
        map.put(GlobalDef.STOP_CHINESE, list_c);
        map.put(GlobalDef.STOP_ENGLISH, list_e);
 
        return map;
    }
    
 // get stopword 2
    @SuppressWarnings("rawtypes")
    public Map<String, List> ansjStopWord(String path_chinese, String path_english) throws Exception {
 
        Map<String, List> map = new HashMap<String, List>();
 
        AnsjStopWord AnsjStopWord = new AnsjStopWord();
 
        List<String> list_c = AnsjStopWord.readStopWord(path_chinese);
        List<String> list_e = AnsjStopWord.readStopWord(path_english);
 
        map.put(GlobalDef.STOP_CHINESE, list_c);
        map.put(GlobalDef.STOP_ENGLISH, list_e);
 
        return map;
    }
    
 // read stopword from file
    @SuppressWarnings("resource")
    public List<String> readStopWord(String path) throws Exception {
 
        List<String> list = new ArrayList<String>();
 
        File file = new File(path);
        InputStreamReader isr = new InputStreamReader(
                new FileInputStream(file), GlobalDef.ENCODING);
        BufferedReader bf = new BufferedReader(isr);
 
        String stopword = null;
        while ((stopword = bf.readLine()) != null) {
            stopword = stopword.trim();
            list.add(stopword);
        }
 
        return list;
    }
 
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void main(String[] args) throws Exception {
        AnsjStopWord ansjStopWord = new AnsjStopWord();
        Map<String, List> map = ansjStopWord.ansjStopWord();
        List<String> list = map.get(GlobalDef.STOP_CHINESE);
 
        for (String str : list) {
            System.out.println(str);
        }
    }
}