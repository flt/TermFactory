package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

public class LocalTerm {
	private String name_en = null;
	private String name_zh = null;
	private List<SourceInfo> source = null;
	private List<String> tags = null;
	private List<String> stem_zh = null;
	private List<String> stem_en = null;
	private String localCode = null;
	private List<String> parentList = null;
	private List<String> subList = null;
	private List<String> synonym = null;
	private List<LocalTerm> mapping = null;
	private String inuse = null;
	public String getInuse() {
		return inuse;
	}

	public void setInuse(String inuse) {
		this.inuse = inuse;
	}

	public List<LocalTerm> getMapping() {
		return mapping;
	}

	public LocalTerm(LocalTerm lt){
		this.name_en = lt.name_en;
		this.name_zh = lt.name_zh;
		this.source = lt.source;
		this.tags = lt.tags;
		this.stem_en = lt.stem_en;
		this.stem_zh = lt.stem_zh;
		this.localCode = lt.localCode;
		this.parentList = lt.parentList;
		this.subList = lt.subList;
		this.synonym = lt.synonym;
		this.mapping = lt.mapping;
		this.inuse = lt.inuse;
	}
	
	public void deleteTerm(){
		this.setInuse("0");
	}
	
	public void setMapping(List<LocalTerm> mapping) {
		this.mapping = mapping;
	}

	private String cui = null;
	private String _id = null;
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public List<String> getSynonym() {
		return synonym;
	}

	public void setSynonym(List<String> synonym) {
		this.synonym = synonym;
	}

	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}
	
	public void addSynonym(String syn){
		if(this.synonym == null)
			this.synonym = new ArrayList<String>();
		this.synonym.add(syn);
	}

	public List<String> getParentList() {
		return parentList;
	}

	public void setParentList(List<String> parentList) {
		this.parentList = parentList;
	}
	
	public void addParentList(List<String> parentList){
		if (this.parentList == null)
			this.parentList = new ArrayList<String>();
		this.parentList.addAll(parentList);
	}

	public List<String> getSubList() {
		return subList;
	}

	public void setSubList(List<String> subList) {
		this.subList = subList;
	}
	
	public void addSubList(List<String> subList) {
		if (this.subList == null){
			this.subList = new ArrayList<String>();
		}
		this.parentList.addAll(subList);
	}
	
	public void addSubList(String subList) {
		if (this.subList == null){
			this.subList = new ArrayList<String>();
		}
		this.parentList.add(subList);
	}

	public LocalTerm(String name_en, String name_zh){
		this.name_en = name_en;
		this.name_zh = name_zh;
	}
	
	public LocalTerm(){
		
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getName_zh() {
		return name_zh;
	}

	public void setName_zh(String name_zh) {
		this.name_zh = name_zh;
	}

	public List<SourceInfo> getSource() {
		return source;
	}

	public void setSource(List<SourceInfo> source) {
		this.source = source;
	}

	public void addSource(String sysName, String sysCode, String link, String type){
		if(this.source == null){
			this.source = new ArrayList<SourceInfo>();
		}
		SourceInfo curSource = new SourceInfo(sysName, sysCode);
		curSource.setSourceLink(link);
		curSource.setSourceType(type);
		this.source.add(curSource);
	}
	
	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void addTags(List<String> tags){
		if (this.tags == null){
			this.tags = new ArrayList<String>();
		}
		this.tags.addAll(tags);
	}
	
	public List<String> getStem_zh() {
		return stem_zh;
	}

	public void setStem_zh(List<String> stem_zh) {
		this.stem_zh = stem_zh;
	}

	public void addStem_zh(List<String> stem_zh){
		if (this.stem_zh == null)
			this.stem_zh = new ArrayList<String>();
		this.stem_zh.addAll(stem_zh);
	}
	
 	public List<String> getStem_en() {
		return stem_en;
	}

	public void setStem_en(List<String> stem_en) {
		this.stem_en = stem_en;
	}

	public void addStem_en(List<String> stem_en){
		if(this.stem_en == null)
			this.stem_en = new ArrayList<String>();
		this.stem_en.addAll(stem_en);
	}
	
	public String getLocalCode() {
		return localCode;
	}

	public void setLocalCode(String localCode) {
		this.localCode = localCode;
	}

	public String TermToJson(){
		String documentString = "";
		Gson gson = new Gson();
		documentString = gson.toJson(this);
		//System.out.println(documentString);
		return documentString;
	}
	
	public static LocalTerm getLocalTermFromJson(String json){
		Gson gson = new Gson();
		LocalTerm element = gson.fromJson(json, LocalTerm.class);
		return element;
	}

	public static void main(String[] args){
		LocalTerm term = new LocalTerm("test", "测试");
		term.setParentList(Arrays.asList("mother", "father", "papa", "mama"));
		term.setSubList(Arrays.asList("brother", "sister", "son", "daughter"));
		term.addSource("ICD10", "123", "http://123", "bioPortal");
		term.addSource("LOINC", "456", "http://456", "bioPortal");
		String document = term.TermToJson();
		System.out.print(document);
	}
}
