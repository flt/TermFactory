package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import DBConnection.MongoDBJDBC;
import model.TransInfo;
import translationUtils.youdaoTranslator;

public class translationController {
	public static MongoDBJDBC database;
	public static void translateByYoudao(String filePath){
		File file = new File(filePath);
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(file));
			String tempString = "";
			int length = 0;
			while((tempString = reader.readLine()) != null){
				//判断是否有code
				if(tempString.contains("@@")){
					tempString = tempString.substring(0, tempString.indexOf("@@"));
					System.out.println(tempString);
				}
				String chinese = youdaoTranslator.getTranslation(tempString);
				if (chinese == null)
					continue;
				TransInfo curTrans = new TransInfo(tempString, chinese, "youdaoAPI");
				length ++;
				curTrans.setUpdatedtime();
				database.insertData("TranslationLog", curTrans.TermToJson());
				if(length >= 1000){
					Thread.sleep(60000);
					length = 0;
					System.out.println("time to pause");
				}
					
			}
		}catch(IOException | InterruptedException e){
			e.printStackTrace();
		}finally{
			if(reader != null){
				try{
					reader.close();
				}catch(IOException e1){
					
				}
			}
		}
	}
	public static void translateByToudao(List<String> textList){
		
	}
	public static void translateByMicrosoft(String filePath){
		
	}
	public static void translateByMicrosoft(List<String> filePath){
		
	}
	public static void main(String[] args){
		database = new MongoDBJDBC();
		System.out.println("hello");
		database.createConnection("localhost", 27017, null, null);
		translateByYoudao("data/RXNORMtemp.txt");
	}
}
