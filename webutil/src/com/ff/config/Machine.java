package com.ff.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Machine {
	
	private List<User> users = new ArrayList<User>();
	private List<Request> requests = new ArrayList<Request>();
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public List<Request> getRequests() {
		return requests;
	}
	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}
	
	public static Machine parseFrom(File file){
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
			DocumentBuilder db = dbf.newDocumentBuilder();  
			Document document = db.parse(file);  
			NodeList list = document.getElementsByTagName("machine");
			for(int i = 0; i < list.getLength(); i++)  
	        {  
	            Element machine = (Element)list.item(i); 
	            String run = machine.getAttribute("run"); 
	            if(!"true".equalsIgnoreCase(run))continue;
	            Machine m = new Machine();
	            NodeList usersElements = machine.getElementsByTagName("user");
	            for(int j = 0; j < usersElements.getLength(); j++)  
		        { 
	            	Element usersElement = (Element)usersElements.item(j); 
	            	m.getUsers().add(new User(usersElement.getAttribute("id"),usersElement.getAttribute("pwd")));
		        }
	            NodeList requests = machine.getElementsByTagName("request");
	            for(int j = 0; j < requests.getLength(); j++)  
		        { 
	            	Element usersElement = (Element)requests.item(j); 
	            	Request request = new Request();
	            	if(usersElement.getAttribute("url").length()>0){
	            		request.setUrl(usersElement.getAttribute("url"));
	            	}
	            	if(usersElement.getAttribute("method").length()>0){
	            		request.setMethod(usersElement.getAttribute("method"));
	            	}
	            	if(usersElement.getAttribute("charset").length()>0){
	            		request.setCharset(usersElement.getAttribute("charset"));
	            	}
	            	if(usersElement.getAttribute("url_depend").length()>0){
	            		request.setUrl_depend(usersElement.getAttribute("url_depend"));
	            	}
	            	if(usersElement.getAttribute("url_depend_prefix").length()>0){
	            		request.setUrl_depend_prefix(usersElement.getAttribute("url_depend_prefix"));
	            	}
	            	if(usersElement.getAttribute("url_depend_suffix").length()>0){
	            		request.setUrl_depend_suffix(usersElement.getAttribute("url_depend_suffix"));
	            	}
	            	NodeList headers = usersElement.getElementsByTagName("header");
	            	for(int k = 0; k < headers.getLength(); k++)  
	    	        {  
	    	            Element header = (Element)headers.item(k); 
	    	            request.getHeaders().put(header.getAttribute("name"), header.getAttribute("value"));
	    	        }
	            	NodeList params = usersElement.getElementsByTagName("param");
	            	for(int k = 0; k < params.getLength(); k++)  
	    	        {  
	    	            Element param = (Element)params.item(k); 
	    	            request.getParams().put(param.getAttribute("name"), param.getAttribute("value"));
	    	        }
	            	NodeList responses = usersElement.getElementsByTagName("response");
	            	if(responses.getLength()>0){
	            		Element response = (Element)responses.item(0);
	            		if(response.getAttribute("charset").length()>0){
	            			request.getReponse().setCharset(response.getAttribute("charset"));
	            		}
	            		if(response.getAttribute("filter").length()>0){
	            			request.getReponse().setFilter(new ExprNodeFilter(response.getAttribute("filter")));
	            		}
	            		if(response.getAttribute("type").length()>0){
	            			request.getReponse().setType(response.getAttribute("type"));
	            		}
	            		if(response.getAttribute("back").length()>0){
	            			request.getReponse().setBack(response.getAttribute("back"));
	            		}
	            	}
					m.getRequests().add(request );
		        }
	            return m;
	        }
			
		}catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}

}
