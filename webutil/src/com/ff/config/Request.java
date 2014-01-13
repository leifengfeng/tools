package com.ff.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Request {

	private String url = null;
	private String url_depend = null;
	private String url_depend_prefix = "";
	private String url_depend_suffix = "";
	private String method = "GET";
	private String charset = "UTF-8";
	private Map<String,String> headers = new HashMap<String,String>();
	private Map<String,String> params = new LinkedHashMap<String,String>();
	private Response reponse = new Response();
	
	public String getUrl_depend_prefix() {
		return url_depend_prefix;
	}

	public void setUrl_depend_prefix(String url_depend_prefix) {
		this.url_depend_prefix = url_depend_prefix;
	}

	public String getUrl_depend_suffix() {
		return url_depend_suffix;
	}

	public void setUrl_depend_suffix(String url_depend_suffix) {
		this.url_depend_suffix = url_depend_suffix;
	}

	public String getUrl_depend() {
		return url_depend;
	}

	public void setUrl_depend(String url_depend) {
		this.url_depend = url_depend;
	}

	
	public Request(String url){
		this.url = url;
	}
	
	public Request(){
	}
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public Response getReponse() {
		return reponse;
	}

	public void setReponse(Response reponse) {
		this.reponse = reponse;
	}
	
	
}
