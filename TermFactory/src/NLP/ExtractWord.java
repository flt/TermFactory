package NLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

public class ExtractWord {
    // extract word
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<String> extracWord(String article, Map<String, List> map)
            throws Exception {
 
        List<String> list = new ArrayList<String>();
         
        List<String> list_c = map.get(GlobalDef.STOP_CHINESE);
        List<String> list_e = map.get(GlobalDef.STOP_ENGLISH);
 
        List<Term> parse = NlpAnalysis.parse(article);
 
        for (Term term : parse) {
             
            boolean flag = true;
 
            String str = term.getName().trim();
             
            for (String str_c : list_c) {
                if (str_c.equals(str))
                    flag = false;
            }
             
            for (String str_e : list_e) {
                if (str_e.equals(str))
                    flag = false;
            }
             
            if (str == "")
                flag = false;
             
            if (flag)
                list.add(str);
 
        }
 
        return list;
    }
 
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {
         
        ExtractWord extractWord = new ExtractWord();
        AnsjStopWord getStopWordList = new AnsjStopWord();
        //使用默认的停用词表
        Map<String, List> map = getStopWordList.ansjStopWord();
        String word = "Maven是基于项目对象模型(POM)，可以通过一小段描述信息来管理项目的构建，报告和文档的软件项目管理工具。Maven 除了以程序构建能力为特色之外，还提供高级项目管理工具。";
        //String enword = "Sodium ironedetate Oral Solution";
        List<String> list = extractWord.extracWord(word, map);
         
        for (String str : list) {
            System.out.println(str);
        }
    }
 
}