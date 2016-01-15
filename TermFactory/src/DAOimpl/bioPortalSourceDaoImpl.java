package DAOimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import DAO.bioPortalSourceDao;
import HttpConnection.NCBOUtils.NCBOUtils;
import model.LocalTerm;
import model.SourceInfo;
import model.bioPortalSource;

public class bioPortalSourceDaoImpl extends commonDaoImpl implements bioPortalSourceDao{
	
	private LocalTerm fromJsonNode(JsonNode node){
		LocalTerm lt = new LocalTerm();
		lt.setName_en(node.get("prefLabel").asText());
		String ontology_name = node.get("links").findValue("ontology").asText();
		String code = node.get("@id").asText();
		code = code.substring(code.lastIndexOf("/") + 1);
		ontology_name = ontology_name.substring(ontology_name.lastIndexOf('/') + 1);
		lt.addSource(ontology_name, code, node.get("@id").asText(), "bioPortal");
		System.out.println(lt.TermToJson());
		return lt;
	}
	
	public List<bioPortalSource> getBioPortalSourceInfo(String CollectionName, bioPortalSource queryString){
		List<bioPortalSource> result = new ArrayList<bioPortalSource>();
		return result;
	}
	
	public List<LocalTerm> getSubClass(LocalTerm lt){
		List<LocalTerm> subList = new ArrayList<LocalTerm>();
		List<SourceInfo> sourceList = lt.getSource();
		for(SourceInfo s:sourceList){
			String termLink = s.getSourceLink();
			List<JsonNode> nodes = NCBOUtils.listSubClasses(termLink);
			for(JsonNode node: nodes){
				subList.add(fromJsonNode(node));
			}
		}
		return subList;
	}
	
	public List<LocalTerm> getParentClass(LocalTerm lt){
		List<LocalTerm> parentList = new ArrayList<LocalTerm>();
		return parentList;
	}
	
	public List<LocalTerm> getAncestorClass(LocalTerm lt){
		List<LocalTerm> ancestorList = new ArrayList<LocalTerm>();
		return ancestorList;
	}
	
	public List<LocalTerm> getDecestorClass(LocalTerm lt){
		List<LocalTerm> decestorList = new ArrayList<LocalTerm>();
		return decestorList;
	}
	
	public List<LocalTerm> getMappingClass(LocalTerm lt){
		List<LocalTerm> mappingList = new ArrayList<LocalTerm>();
		return mappingList;
	}
	
	public LocalTerm getTermInfo(String queryString){
		LocalTerm lt = new LocalTerm();
		return lt;
	}
}
