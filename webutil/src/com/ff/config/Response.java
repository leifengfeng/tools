package com.ff.config;

import org.htmlparser.NodeFilter;

public class Response {
	
	private String charset = "UTF-8";
	private String type = "html";
	private NodeFilter filter = null;
	private String back = null;
	
	public String getBack() {
		return back;
	}

	public void setBack(String back) {
		this.back = back;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public NodeFilter getFilter() {
		return filter;
	}

	public void setFilter(NodeFilter filter) {
		this.filter = filter;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	

}
