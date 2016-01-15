package utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.fasterxml.jackson.databind.JsonNode;

import DAOimpl.LocalTermDaoImpl;
import DBConnection.MongoDBJDBC;
import HttpConnection.NCBOUtils.NCBOUtils;
import Services.LocalTermServices;
import model.LocalTerm;
import model.bioPortalSource;

public class controller {
	public static MongoDBJDBC database;
	public static int insertBioPortalSource(){
		int num = 0;
		Map<String, String> source = NCBOUtils.listOntologies();
		num = source.size();
		System.out.println(num);
		for(Entry<String, String> pair:source.entrySet()){
			String name_en = pair.getKey();
			String name_zh = "";
			List<Document> storedChinese = database.searchData("TranslationLog", MongoDBJDBC.getDocumentString("name_en", name_en));
			if(storedChinese != null){
				for (Document chinese : storedChinese){
					name_zh = chinese.getString("name_zh");
				}
			}
			else{
				/*int looptime = 0;
				while(name_zh.equals("") || name_zh.startsWith("TranslateApiException:")){
					name_zh = microsoftTranslator.getTranslation(name_en);
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
						continue;
					}
					looptime +=1;
					System.out.println(looptime);
					if(looptime > 5){
						name_zh = null;
						break;
					}
				}
				*/
				name_zh = null;
				/*if(name_zh != null){
					//有查询结果就保存在数据库中
					database.insertData("TranslationLog", new TransInfo(name_en, name_zh, "microsoft API").TermToJson());
					System.out.println("get translation from microsoft");
				}*/
			}
			bioPortalSource cur = new bioPortalSource(pair.getKey(), name_zh, pair.getValue(), "bioPortal");
			System.out.println(pair.getKey()  + "中文：" + name_zh + " : " + pair.getValue());
			database.insertData("SourceInfo", cur.TermToJson());
		}
		return num;
	}
	
	public static void insertClasses(String databaseName, String ontologyName){
		//get ontology url from database
		List<Object> ontURL = database.searchData("SourceInfo", "{\"name_en\":\"" + ontologyName + "\"}", "link");
		//List<String> ontology = database.searchData("sourceInfo", new bioPortalSource(ontologyName, null,null,null).TermToJson());
		
		//collect classes info from bioportal
		System.out.println("get:" + ontURL.toString());
		List<JsonNode> classes = NCBOUtils.listClassesInOntology(ontURL.get(0).toString());
		for(JsonNode cla: classes){
			bioPortalSource sourceinfo = new bioPortalSource(cla.get("prefLabel").asText(), null, cla.get("@id").asText(), "bioPortal");
			//需要判断数据库中是否有数据
			List<Document> relatedResult = database.searchData(databaseName, "{\"name_en\":\"" + cla.get("prefLabel").asText() + "\"}");
			if(relatedResult == null || relatedResult.size() == 0){
				database.insertData(databaseName, sourceinfo.TermToJson());
				System.out.println("add new term:" + sourceinfo.TermToJson());
			}
			else{
				System.out.println("exist!: " + relatedResult.get(0));
			}
		}
	}
	
	public static void main(String[] args){
		database = new MongoDBJDBC();
		database.createConnection(MongoDBJDBC.getIp(), 27017, null, null);
		LocalTermDaoImpl ltdi=new LocalTermDaoImpl();
		//LocalTerm toUpdate = new LocalTerm("MSH6 (mutS homolog 6 [E. coli]) (eg, hereditary non-polyposis colorectal cancer, Lynch syndrome) gene analysis; full sequence analysis", "MSH6(傻瓜同族体6(E。杆菌)(如遗传即结直肠癌,林奇综合征)基因分析;完整的序列分析");
		//ltdi.updateZH("ICD10", toUpdate, "测试");
		//ltdi.updateZHBatch("ICDChineseInfo", null);
		//String termString = database.searchData("TranslationLog", "{\"name_en\":\"Drug not collected - \"too expensive\"}").get(0);
		//System.out.println(termString);
		/*LocalTerm lt = new LocalTerm();
		lt.setName_en("Diagnosis");
		
		ltService.getSubClass(lt);*/
		LocalTermServices ltService = new LocalTermServices();
		int result = -1;
		while(result == -1){
			//result = ltdi.translateMix("SNOMEDCT");
			//result = ltdi.translateMix("CPT");
			//result = ltdi.translateMix("LOINC");
			//result = ltdi.insertStemBatch("CPT");
			//ltdi.translateMix("RXNORM");
			//ltdi.insertStemBatch("RXNORM");
			//result = ltdi.insertStemBatch("SNOMEDCT");
			result = ltService.getSubClass("SNOMEDCT");
		}
		//System.out.println(insertBioPortalSource());
		//insertClasses("SNOMEDCT", "Systematized Nomenclature of Medicine - Clinical Terms");
		//LocalTerm term = new LocalTerm("test", "测试");
		//term.setParentList(Arrays.asList("mother", "father", "papa", "mama"));
		//term.setSubList(Arrays.asList("brother", "sister", "son", "daughter"));
		//term.addSource("ICD10", "123", "http://123", "bioPortal");
		//term.addSource("LOINC", "456", "http://456", "bioPortal");
		//String document = term.TermToJson();
		//database.insertData("TermInfo", document);
	}
}
