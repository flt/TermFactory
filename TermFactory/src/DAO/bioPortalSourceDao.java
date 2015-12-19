package DAO;

import java.util.List;

import model.bioPortalSource;

public interface bioPortalSourceDao {
	List<String> getZhByEn(String CollectionName, String en);
	List<String> getEnByZh(String CollectionName, String zh);
	List<bioPortalSource> getBioPortalSourceInfo(String CollectionName, bioPortalSource queryString);
}
