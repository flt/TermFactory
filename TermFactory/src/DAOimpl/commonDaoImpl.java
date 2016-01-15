package DAOimpl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import DBConnection.MongoDBJDBC;

public class commonDaoImpl {
	public void main(String[] args){
		getZhByEn("", "");
	}
	
	public List<String> getZhByEn(String CollectionName, String en){
		List<String> zhList = new ArrayList<String>();
		MongoDBJDBC dbClient = MongoDBJDBC.getInstance();
		//生成DocumentString
		String documentString = "{\"name_en\":\"" + en + "\"}";
		List<Document> zhListDocs = dbClient.searchData(CollectionName, documentString);
		for(Document zhdoc:zhListDocs){
			zhList.add(zhdoc.getString("name_zh"));
		}
		System.out.println(zhList.toArray());
		return zhList;
	}
	public List<String> getEnByZh(String CollectionName, String zh){
		List<String> enList = new ArrayList<String>();
		MongoDBJDBC dbClient = MongoDBJDBC.getInstance();
		//生成DocumentString
		String documentString = "{\"name_zh\":\"" + zh + "\"}";
		List<Document> enListDocs = dbClient.searchData(CollectionName, documentString);
		for(Document endoc:enListDocs){
			enList.add(endoc.getString("name_en"));
		}
		System.out.println(enList.toArray());
		return enList;
	}
}
