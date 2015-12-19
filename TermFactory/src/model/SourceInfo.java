package model;

import java.util.Date;

import com.google.gson.Gson;

public class SourceInfo {
	private String sourceName = null;
	private String sourceCode = null;
	private String sourceLink = null;
	private String sourceType = null;
	private Date updateTime;
	
	public SourceInfo(String sourceName, String sourceCode){
		this.sourceCode = sourceCode;
		this.sourceName = sourceName;
		//this.updateTime = new Date();
	}
	
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
		this.updateTime = new Date();
	}
	public String getSourceCode() {
		return sourceCode;
	}
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
		this.updateTime = new Date();
	}
	public String getSourceLink() {
		return sourceLink;
	}
	public void setSourceLink(String sourceLink) {
		this.sourceLink = sourceLink;
		this.updateTime = new Date();
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
		this.updateTime = new Date();
	}
	
	public String TermToJson(){
		String documentString = "";
		Gson gson = new Gson();
		documentString = gson.toJson(this);
		System.out.println(documentString);
		return documentString;
	}
	
	public static SourceInfo JsonToTerm(String jsonString){
		SourceInfo curSource;
		Gson gson = new Gson();
		SourceInfo obj = gson.fromJson(jsonString, SourceInfo.class);
		return obj;
	}
	
}
