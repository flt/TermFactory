package DAOimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import DAO.LocalTermDao;
import DBConnection.MongoDBJDBC;
import fileOperation.TxtOperation;
import model.LocalTerm;
import model.SourceInfo;
import model.TransInfo;

public class LocalTermDaoImpl extends commonDaoImpl implements LocalTermDao{
	private MongoDBJDBC mongoClient;
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
	public void translateMix(String CollectionName){
		mongoClient = MongoDBJDBC.getInstance();
		//System.out.println(mongoClient.toString());
		FindIterable<Document> iterable;
		//iterable = mongoClient.selectAllDocument(CollectionName);
		iterable = mongoClient.iterateDocument(CollectionName, "{\"name_zh\":null}");
		iterable.forEach(new Block<Document>(){
			@Override
			public void apply(final Document document){
				String source = document.getString("name_en");
				List<String> stringList = mongoClient.searchData("TranslationLog", "{\"name_en\":\""+source+"\"}");
				if (stringList == null)
					return;
				String termString = stringList.get(0);
				for(int i = 1; i < stringList.size(); i++){
					mongoClient.deleteData("TranslationLog", stringList.get(i));
				}
				//System.out.println(termString);
				String zh = TransInfo.LocalTerm(termString).getName_zh();
				mongoClient.updateData(CollectionName, "{\"name_en\":\""+source+"\"}", "name_zh", zh);
			}
		});
	}
	public void getStemZhBatch(String CollectionName){
		
	}
	public void getStemZh(String CollectionName){
		
	}
	public void getStemEnBatch(String CollectionName){
		
	}
	public void getStemEn(String CollectionName){
		
	}
}
