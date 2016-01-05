package model;

import java.util.Date;

import com.google.gson.Gson;

public class TransInfo {
	private String name_en = null;
	private String name_zh = null;
	private String source = null;
	private Date updatedtime = null;
	
	public static void main(String[] args){
		TransInfo t = new TransInfo();
		t.setName_en("test \"Chinese\"");
		System.out.println(t.TermToJson());
	}
	
    public TransInfo(String name_en, String name_zh, String source){
		this.name_en = name_en;
		this.name_zh = name_zh;
		this.source = source;
	}
    public TransInfo(){
    	
    }
    
    public String TermToJson(){
		String documentString = "";
		Gson gson = new Gson();
		documentString = gson.toJson(this);
		return documentString;
	}
    
    public TransInfo JsonToTerm(String response){
    	TransInfo pair = new Gson().fromJson(response, TransInfo.class);
    	return pair;
    }
	public Date getUpdatedtime() {
		return updatedtime;
	}
	public void setUpdatedtime() {
		this.updatedtime = new Date();
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
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public static TransInfo LocalTerm(String json){
		Gson gson = new Gson();
		TransInfo element = gson.fromJson(json, TransInfo.class);
		return element;
	}
	@Override
	public boolean equals(Object o){
		if(! (o instanceof TransInfo))
			return false;
		TransInfo t = (TransInfo) o;
		return t.name_en.equals(name_en) && t.source.equals(source);
	}
}
