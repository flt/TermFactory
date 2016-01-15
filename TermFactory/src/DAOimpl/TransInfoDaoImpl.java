package DAOimpl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import translationUtils.youdaoTranslator;
import fileOperation.TxtOperation;
import model.LocalTerm;
import model.TransInfo;
import DAO.TransInfoDao;
import DBConnection.MongoDBJDBC;

public class TransInfoDaoImpl extends commonDaoImpl implements TransInfoDao {
	MongoDBJDBC mongoClient = MongoDBJDBC.getInstance(); 
	
	public TransInfo getTransInfo(TransInfo lt){
		List<Document> stringList = mongoClient.searchData("TranslationLog", lt.TermToJson());
		if (stringList != null){
			String termString = stringList.get(0).toJson();
			for(int i = 1; i < stringList.size(); i++){
				mongoClient.deleteData("TranslationLog", stringList.get(i).toJson());
			}
			return TransInfo.LocalTerm(termString);
		}
		else{
			if(lt.getName_en() != null){
				String resultString = youdaoTranslator.getTranslation(lt.getName_en());
				if (resultString != null){
					lt.setName_zh(resultString.replace(" ", ""));
					lt.setSource("youdaoAPI");
					mongoClient.insertData("TranslationLog", lt.TermToJson());
				}
			}
			else if(lt.getName_zh() != null){
				String resultString = youdaoTranslator.getTranslation(lt.getName_zh());
				if (resultString != null){
					lt.setName_en(resultString);
					lt.setSource("youdaoAPI");
					mongoClient.insertData("TranslationLog", lt.TermToJson());
				}
			}
		}
		return lt;
	}
}
