package DBConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.json.JsonParseException;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import fileOperation.TxtOperation;

public class MongoDBJDBC {
	private static MongoClient mongoClient;
	private static MongoDBJDBC _instance = null;
	//private static final String IP = "166.111.82.18";
	private static final String IP = "localhost";
	public static String getIp() {
		return IP;
	}

	private MongoDatabase db;
	public static void main(String args[]){
		MongoDBJDBC databaseConnection = new MongoDBJDBC();
		databaseConnection.createConnection(IP, 27017, null,null);
		if(databaseConnection.db != null)
			System.out.println("Success");
		else
			System.out.println("Failed");
		//List<String> result = databaseConnection.searchData("TranslationLog", getDocumentString("name_en", "test"));
		//System.out.println(result.get(0));
		//String queryString = "{\"source\":{\"ICD10\":\"E10.4+\"}}}";
		//result = databaseConnection.searchData("ICDChineseInfo", queryString);
		//System.out.println(result.get(0));
		databaseConnection.writeAllDocument("RXNORM");
	}
	
	public static MongoDBJDBC getInstance()
	{
		if(_instance == null)
			_instance = new MongoDBJDBC();
	   return _instance;
	}  
	
	public MongoDBJDBC(){
		createConnection(IP, 27017, null,null);
		if(this.db != null)
			System.out.println("Success");
		else
			System.out.println("Failed");
	}
	
	public void createConnection(String address, int port, String username, String passw){
		try{
			mongoClient = new MongoClient(IP, 27017);
			db = mongoClient.getDatabase("TermFactory");
			System.out.println("Connect to database successfully");
		}catch(MongoTimeoutException e){
			System.out.println("Wrong authentication!");
		}catch(Exception e){
			System.err.println(e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public FindIterable<Document> selectAllDocument(String collectionName){
		FindIterable<Document> iterable = db.getCollection(collectionName).find();
		return iterable;
	}
	
	public FindIterable<Document> iterateDocument(String collectionName, String DocumentString){
		Document queryCondition = Document.parse(DocumentString);
		FindIterable<Document> iterable = db.getCollection(collectionName).find(queryCondition);
		return iterable;
	}
	
	public FindIterable<Document> writeAllDocument(String collectionName){
		FindIterable<Document> iterable = db.getCollection(collectionName).find();
		List<String> toWrite = new ArrayList<String>();
		iterable.forEach(new Block<Document>(){
			@Override
			public void apply(final Document document){
				System.out.println(document);
				String line = document.get("name_en").toString() + "@@$$@@";
				String sourceInfo = document.get("source").toString().split("=|,")[3];
				System.out.println(sourceInfo);
				line += sourceInfo;
				System.out.println(line);
				toWrite.add(line + "\n");
				//if(toWrite.size() >= 1000){
					//TxtOperation.writeToFile("data/"+collectionName+".txt", toWrite, "append");
					//toWrite.clear();
					//System.out.println("Over 1000");
				//}
			}
		});
		TxtOperation.writeToFile("data/"+collectionName+".txt", toWrite, "append");
		toWrite.clear();
		return iterable;
	}
	
	public void selectDadabase(String dbName){
		if(db != null && !db.getName().equals(dbName) ){
			db = mongoClient.getDatabase(dbName);
		}
	}
	
	public void insertData(String collectionName, String DocumentString){
		Document insertData = Document.parse(DocumentString);
		db.getCollection(collectionName).insertOne(insertData);
	}
	
	public boolean updateData(String collectionName, String conditionDocumentString, String newDocumentString){
		boolean flag = true;
		UpdateResult ur = db.getCollection(collectionName).replaceOne(Document.parse(conditionDocumentString), Document.parse(newDocumentString));
		if(ur.getModifiedCount() >= 1)
			flag = true;
		else
			flag = false;
		return flag;
	}
	
	public boolean updateData(String collectionName, String conditionDocumentString, String key, String value){
		boolean flag = true;
		UpdateResult ur = db.getCollection(collectionName).updateOne(Document.parse(conditionDocumentString), new Document("$set", new Document(key, value)));
		if(ur.getModifiedCount() >= 1)
			flag = true;
		else
			flag = false;
		return flag;
	}
	
	public static String getDocumentString(String key, String value){
		Document doc = null;
		if (key != null && value != null)
			doc = new Document(key, value);
		return doc.toJson();
	}
	
	public List<String> searchData(String collectionName, String DocumentString){
		List<String> relatedDocument = new ArrayList<String>();
		Document queryData;
		try{
			queryData = Document.parse(DocumentString);
		}catch(JsonParseException jpe){
			System.out.println(jpe.getMessage());
			System.out.println("ERROR!!!" + DocumentString);
			String toWrite = "~~~~~Wrong document:" + DocumentString + "\n";
			TxtOperation.writeToFile("data/ErrorLog/TranslationErrorEn.txt", toWrite, "append");
			return null;
		}
		FindIterable<Document> iterable = db.getCollection(collectionName).find(queryData);
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document){
				//System.out.println(document);
				relatedDocument.add(document.toJson());
			}
		});
		if (relatedDocument.size() != 0)
			return relatedDocument;
		else
			return null;
	}
	
	public long deleteData(String collectionName, String ConditionString){
		try{
			Document queryData = Document.parse(ConditionString);
			DeleteResult dr = db.getCollection(collectionName).deleteMany(queryData);
			return dr.getDeletedCount();
		}catch(JsonParseException jpe){
			System.out.println(jpe.getMessage() + "     "+ ConditionString);
			TxtOperation.writeToFile("data/ErrorLog/DocumentParse.txt", ConditionString, "append");
			return 0;
		}
	}
	
	public List<Object> searchData(String collectionName, String DocumentString, String queryValue){
		List<Object> relatedDocument = new ArrayList<Object>();
		Document queryData = Document.parse(DocumentString);
		FindIterable<Document> iterable = db.getCollection(collectionName).find(queryData);
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document){
				//System.out.println(document);
				relatedDocument.add(document.get(queryValue));
			}
		});
		if (relatedDocument.size() != 0)
			return relatedDocument;
		else
			return null;
	}
}
