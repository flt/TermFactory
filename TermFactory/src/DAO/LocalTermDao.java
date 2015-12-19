package DAO;

import java.util.List;

import model.LocalTerm;

public interface LocalTermDao {
	List<String> getZhByEn(String CollectionName, String en);
	List<String> getEnByZh(String CollectionName, String zh);
	List<LocalTerm> getBioPortalSourceInfo(String CollectionName, LocalTerm queryString);
}
