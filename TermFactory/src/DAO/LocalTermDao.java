package DAO;

import java.util.List;

import model.LocalTerm;
import model.SourceInfo;

public interface LocalTermDao {
	List<String> getZhByEn(String CollectionName, String en);
	List<String> getEnByZh(String CollectionName, String zh);
	List<SourceInfo> getSourceInfo(LocalTerm lt);
	List<LocalTerm> getBioPortalSourceInfo(String CollectionName, LocalTerm queryString);
	LocalTerm getLocalTermInfo(LocalTerm lt);
	void insertStem(String CollectionName, LocalTerm queryString);
}
