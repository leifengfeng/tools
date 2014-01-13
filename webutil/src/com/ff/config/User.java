package com.ff.config;

public class User {
	
	private String id = null;
	private String pwd = null;
	
	public User(String id,String pwd){
		this.id = id;
		this.pwd =  pwd;
	}
	
	public User(){
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	

}
