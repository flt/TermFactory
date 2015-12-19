package model;

import java.util.Date;

import com.google.gson.Gson;

public class bioPortalSource {
	private String name_en;
	private String name_zh;
	private String link;
	private String type;
	private Date updatedTime;
	
	public bioPortalSource(String name_en, String name_zh, String link, String type){
		this.name_en = name_en;
		this.name_zh = name_zh;
		this.link = link;
		this.type = type;
	}
	
	public String TermToJson(){
		String documentString = "";
		Gson gson = new Gson();
		documentString = gson.toJson(this);
		System.out.println(documentString);
		return documentString;
	}
	
	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
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
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
