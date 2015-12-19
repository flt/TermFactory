package DAOimpl;

import java.util.ArrayList;
import java.util.List;

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
		zhList = dbClient.searchData(CollectionName, documentString);
		System.out.println(zhList.toArray());
		return zhList;
	}
	public List<String> getEnByZh(String CollectionName, String zh){
		List<String> enList = new ArrayList<String>();
		MongoDBJDBC dbClient = MongoDBJDBC.getInstance();
		//生成DocumentString
		String documentString = "{\"name_zh\":\"" + zh + "\"}";
		enList = dbClient.searchData(CollectionName, documentString);
		System.out.println(enList.toArray());
		return enList;
	}
}
