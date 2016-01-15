package DAO;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.LocalTerm;
import model.bioPortalSource;

public interface bioPortalSourceDao {
	List<bioPortalSource> getBioPortalSourceInfo(String CollectionName, bioPortalSource queryString);
	List<LocalTerm> getSubClass(LocalTerm lt);
	List<LocalTerm> getParentClass(LocalTerm lt);
	List<LocalTerm> getAncestorClass(LocalTerm lt);
	List<LocalTerm> getDecestorClass(LocalTerm lt);
	List<LocalTerm> getMappingClass(LocalTerm lt);
	LocalTerm getTermInfo(String queryString);
}
