package Services;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.MongoCursorNotFoundException;
import com.mongodb.client.FindIterable;

import DAO.LocalTermDao;
import DAO.TransInfoDao;
import DAO.bioPortalSourceDao;
import DAOimpl.LocalTermDaoImpl;
import DAOimpl.TransInfoDaoImpl;
import DAOimpl.bioPortalSourceDaoImpl;
import DBConnection.MongoDBJDBC;
import model.LocalTerm;
import model.SourceInfo;
import model.TransInfo;

public class LocalTermServices {
	static bioPortalSourceDao bioPortalDao = new bioPortalSourceDaoImpl();
	static LocalTermDao localtermDao = new LocalTermDaoImpl();
	static TransInfoDao transinfoDao = new TransInfoDaoImpl();
	static MongoDBJDBC mongoClient = MongoDBJDBC.getInstance();
	public LocalTermServices (){}
	public void getSubClass(LocalTerm lt){
		if(lt.getSource() == null || lt.getSource().size() == 0){
			List<SourceInfo> sourceList = localtermDao.getSourceInfo(lt);
			lt.setSource(sourceList);
		}
		List<LocalTerm> subList = bioPortalDao.getSubClass(lt);
		List<String> subStringList = new ArrayList<String>();
		//根据source信息去查找ID,然后增加
		System.out.println(subList.size());
		int index = 1;
		for(LocalTerm sub: subList){
			List<SourceInfo> querySourceInfo = new ArrayList<SourceInfo>();
			System.out.println(index + ":" + sub.TermToJson());
			index++;
			for(SourceInfo source:sub.getSource()){
				SourceInfo simpleSource = new SourceInfo(source.getSourceName(), source.getSourceCode());
				simpleSource.setSourceLink(source.getSourceLink());
				querySourceInfo.add(simpleSource);
			}
			sub.setSource(querySourceInfo);
			LocalTerm sub_full = localtermDao.getLocalTermInfo(sub);
			if(sub_full.get_id() == null || sub_full.get_id().length() < 1){
				//说明出现了之前没有出现的类，需要增加到数据库中
				sub.setSource(querySourceInfo);
				sub.getSource().get(0).setSourceType("bioPortal");
				TransInfo newClass = new TransInfo();
				newClass.setName_en(sub.getName_en());
				newClass = transinfoDao.getTransInfo(newClass);
				if(newClass.getName_zh() == null)
					continue;
				sub.setName_zh(newClass.getName_zh());
				mongoClient.insertData(sub.getSource().get(0).getSourceName(), sub.TermToJson());
				localtermDao.insertStem(sub.getSource().get(0).getSourceName(), sub);
				subStringList.add(sub.TermToJson());
			}
			else
				subStringList.add(sub_full.TermToJson());
		}
		lt.setSubList(subStringList);
		String ltID = localtermDao.getLocalTermInfo(lt).get_id();
		Document ltDoc = new Document();
		ltDoc.put("_id", new ObjectId(ltID));
		boolean result = mongoClient.updateDataByField(lt.getSource().get(0).getSourceName(), ltDoc.toJson(), new Document("subList", lt.getSubList()));
		System.out.println("insert subclass:" + result + " !!!!!!");
	}
	public int getSubClass(String collectionName){
		FindIterable<Document> classes = mongoClient.iterateDocument(collectionName,"{\"subList\":null}");
		boolean flag = false;
		try{
			classes.forEach(new Block<Document>(){
				@Override
				public void apply(final Document document){
					LocalTerm lt = new LocalTerm(document.getString("name_en"), document.getString("name_zh"));
					getSubClass(lt);
					System.out.println("update sublist:" + lt.toString());
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
}
