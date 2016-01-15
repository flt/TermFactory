package DAOimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.simple.JSONObject;

import translationUtils.youdaoTranslator;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.MongoCursorNotFoundException;
import com.mongodb.client.FindIterable;

import DAO.LocalTermDao;
import DBConnection.MongoDBJDBC;
import NLP.AnsjStopWord;
import NLP.ExtractWord;
import fileOperation.TxtOperation;
import model.LocalTerm;
import model.SourceInfo;
import model.TransInfo;

public class LocalTermDaoImpl extends commonDaoImpl implements LocalTermDao{
	private MongoDBJDBC mongoClient = MongoDBJDBC.getInstance();
	private Map<String, List> NLPmap = null;
	private ExtractWord extractWord = null;
	private AnsjStopWord getStopWordList = null;
	public List<LocalTerm> getBioPortalSourceInfo(String CollectionName, LocalTerm queryString){
		List<LocalTerm> result = new ArrayList<LocalTerm>();
		return result;
	}
	public void updateZHBatch(String CollectionName, LocalTerm queryStringOfCode){
		mongoClient = MongoDBJDBC.getInstance();
		FindIterable<Document> iterable;
		if(queryStringOfCode == null)
			iterable = mongoClient.selectAllDocument(CollectionName);
		else{
			String query = queryStringOfCode.TermToJson();
			iterable = mongoClient.iterateDocument(CollectionName, query);
		}
		iterable.forEach(new Block<Document>(){
			@Override
			public void apply(final Document document){
				//System.out.println("CollectionName:" + CollectionName);
				//System.out.println("queryString:" + query);
				//System.out.println(document);
				//获取现有的中文和对应的code
				
				Document source = (Document) document.get("source");
				String code = source.getString("ICD10");
				String aftercode = code;
				if(code.endsWith("+"))
					aftercode = code.replace("+", "");
				if(code.endsWith("*"))
					aftercode = code.replace("*", "");
				if(code.contains("-"))
					aftercode += ".9";
				if(code.endsWith("."))
					aftercode = code.substring(0, code.length() - 1);
				if(code.endsWith("00"))
					aftercode = code.substring(0, code.length() - 2);
				String zh = document.getString("name_zh");
				//System.out.println("zh:" + zh + "   code: " + code);
				// 查到相关的ICD中的信息
				LocalTerm currentTerm = new LocalTerm();
				currentTerm.setSource(Arrays.asList(new SourceInfo("ICD10", aftercode)));
				//System.out.println(currentTerm.TermToJson());
				boolean isSuccess = updateZH("ICD10", currentTerm, zh);
				if(!isSuccess){
					System.out.println("~~~~~Wrong code:" + code + "    zh:" + zh);
					String toWrite = "~~~~~Wrong code:" + code + "    zh:" + zh + "\n";
					TxtOperation.writeToFile("data/NotIncludedChineseFromICD10.txt", toWrite, "append");
				}
				else{
					//System.out.println("OK! code:" + code);
				}
			}
		});
	}
	public boolean updateZH(String CollectionName, LocalTerm queryString, String zh){
		mongoClient = MongoDBJDBC.getInstance();
		//String query = queryString.TermToJson();
		String query = "{\"source.sourceCode\":\"" + queryString.getSource().get(0).getSourceCode() +"\"}";
		return mongoClient.updateData(CollectionName, query, "name_zh", zh);
	}
	public int translateMix(String CollectionName){
		mongoClient = MongoDBJDBC.getInstance();
		//System.out.println(mongoClient.toString());
		FindIterable<Document> iterable;
		//iterable = mongoClient.selectAllDocument(CollectionName);"name_zh" : "{ \"_id\" : 
		//iterable = mongoClient.iterateDocument(CollectionName, "{\"name_zh\":/_id/}");
		iterable = mongoClient.iterateDocument(CollectionName, "{\"name_zh\":null}");
		List<String> conditions = new ArrayList<String>();
		conditions.add("{\"name_zh\":null}");
		conditions.add("{\"name_zh\":/_id/}");
		//iterable = mongoClient.iterateLogicalDocument(CollectionName, conditions, "$or");
		try{
			iterable.forEach(new Block<Document>(){
				@Override
				public void apply(final Document document){
					String source = document.getString("name_en");
					LocalTerm lt = new LocalTerm();
					lt.setName_en(source);
					//List<String> stringList = mongoClient.searchData("TranslationLog", "{\"name_en\":\""+source+"\"}");
					List<Document> stringList = mongoClient.searchData("TranslationLog", lt.TermToJson());
					String zh = null;
					if (stringList != null){
						String termString = stringList.get(0).toJson();
						for(int i = 1; i < stringList.size(); i++){
							mongoClient.deleteData("TranslationLog", stringList.get(i).toJson());
						}
						//System.out.println(termString);
						zh = TransInfo.LocalTerm(termString).getName_zh();
					}
					if(zh == null){
						zh = youdaoTranslator.getTranslation(source);
					}
					//lt.set_id(strID);
					//System.out.println(lt.TermToJson());
					//System.out.println(zh);
					if(zh != null){
						boolean isUpdated = mongoClient.updateData(CollectionName, lt.TermToJson(), "name_zh", zh);
						mongoClient.updateData("TranslationLog", lt.TermToJson(), "name_zh", zh);
						//System.out.println(isUpdated);
					}
					else{
						String toWrite = "~~~~~English:" + source + "\n";
						TxtOperation.writeToFile("data/FailToTranslate.txt", toWrite, "append");
					}
				}
			});
		}catch(MongoCursorNotFoundException mce){
			System.out.println("Too long ! out of Cursor!!!");
			return -1;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return -1;
		}
		return 0;
	}
	public int insertStemBatch(String CollectionName){
		extractWord = new ExtractWord();
        getStopWordList = new AnsjStopWord();
        FindIterable<Document> iterable;
        mongoClient = MongoDBJDBC.getInstance();
		try {
			NLPmap = getStopWordList.ansjStopWord();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -2;
		}
		//iterable = mongoClient.selectAllDocument(CollectionName);
		iterable = mongoClient.iterateDocument(CollectionName, "{\"stem_zh\":null}");
		try{
			iterable.forEach(new Block<Document>(){
				@Override
				public void apply(final Document document){
					//LocalTerm lt = LocalTerm.getLocalTermFromJson(document.toJson());
					LocalTerm lt = new LocalTerm(document.getString("name_en"), document.getString("name_zh"));
					insertStem(CollectionName, lt);
				}
			});
		}catch(MongoCursorNotFoundException mce){
			System.out.println("Too long ! out of Cursor!!!");
			return -1;
		}catch(Exception e){
			System.out.println("123");
			
			System.out.println(e.getMessage());
			return -1;
		}
		return 0;
	}
	public List<String> trimList(List<String> org){
		if(org.size() < 1)
			return null;
		List<String> res = new ArrayList<String>();
		for(String str:org){
			if(str.length() >= 1)
				res.add(str);
		}
		return res;
	}
	public void insertStem(String CollectionName, LocalTerm queryString){
		extractWord = new ExtractWord();
        getStopWordList = new AnsjStopWord();
        mongoClient = MongoDBJDBC.getInstance();
		try {
			NLPmap = getStopWordList.ansjStopWord();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       try {
			String name_en = queryString.getName_en();
			String name_zh = queryString.getName_zh();
			List<String> enlist = null;
			List<String> zhlist = null;
			if(name_en != null){
				enlist = trimList(extractWord.extracWord(name_en, NLPmap));
				if(enlist == null){
					enlist = new ArrayList<String>();
					enlist.add(name_en);
				}
				boolean isenUpdated = mongoClient.updateData(CollectionName, queryString.TermToJson(), "stem_en", enlist);
				if(!isenUpdated){
					String toWrite = "~~~~~English:" + name_en + "\n";
					TxtOperation.writeToFile("data/WrongEnglishStem.txt", toWrite, "append");
				}
				else{
					System.out.println("add en stem list:" + isenUpdated);
				}
			}
				
			if(name_zh != null){
				zhlist = trimList(extractWord.extracWord(name_zh, NLPmap));
				if(zhlist == null){
					zhlist = new ArrayList<String>();
					zhlist.add(name_en);
				}
				boolean iszhUpdated = mongoClient.updateData(CollectionName, queryString.TermToJson(), "stem_zh", zhlist);
				if(!iszhUpdated){
					String toWrite = "~~~~~Chinese:" + name_zh + "\n";
					TxtOperation.writeToFile("data/WrongChineseStem.txt", toWrite, "append");
				}
				else{
					System.out.println("add en stem list:" + iszhUpdated);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<SourceInfo> getSourceInfo(LocalTerm lt){
		List<SourceInfo> sourceInfoList = new ArrayList<SourceInfo>();
		if(lt.getSource() != null && lt.getSource().size() > 0)
			return lt.getSource();
		else{
			if(lt.getName_en() != null){
				List<Object> source = mongoClient.searchData("SNOMEDCT", lt.TermToJson(), "source");
				//List<Object> source = mongoClient.searchData("SNOMEDCT", "{\"name_en\":/Microsporidia/}", "source");
				for(Object obj: source){
					List<Document> list = (List<Document>)obj;
					Document sourceDoc = list.get(0);
					System.out.println(sourceDoc.toJson());
					sourceInfoList.add(SourceInfo.JsonToTerm(sourceDoc.toJson()));
				}
			}
		}
		return sourceInfoList;
	}
	public LocalTerm getLocalTermInfo(LocalTerm lt){
		LocalTerm result = new LocalTerm();
		String collectionName = null;
		if(lt != null && lt.getSource()!=null && lt.getSource().size() > 0)
			collectionName = lt.getSource().get(0).getSourceName();
		else
			return null;
		String query = "{\"source.sourceName\":\"" + lt.getSource().get(0).getSourceName().replace("\"", "\\\"")+"\",\"source.sourceCode\":\"" + lt.getSource().get(0).getSourceCode()+"\"}";
		List<Document> ltStringList = mongoClient.searchData(collectionName, query);
		if(ltStringList == null)
			return result;
		for(Document doc:ltStringList){
			result = new LocalTerm();
			result.set_id(doc.getObjectId("_id").toString());
			result.setName_en(doc.getString("name_en"));
			result.setName_zh(doc.getString("name_zh"));
			break;
		}
		return result;
	}
}
