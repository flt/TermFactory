package DAO;

import java.util.List;

import model.SourceInfo;

public interface SourceInfoDao {
	List<String> getZhByEn(String CollectionName, String en);
	List<String> getEnByZh(String CollectionName, String zh);
	List<SourceInfo> getBioPortalSourceInfo(String CollectionName, SourceInfo queryString);
}
