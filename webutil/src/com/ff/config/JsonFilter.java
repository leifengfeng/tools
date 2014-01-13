package com.ff.config;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JsonFilter {

	static ScriptEngineManager mgr = new ScriptEngineManager(); 
	static ScriptEngine engine = mgr.getEngineByName("JavaScript");
	
	public static Object eval(String code,Object param){
		try {
			return engine.eval("var param="+param+"||'';function parseResult(str)"+code+";parseResult(param)");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws ScriptException {
		String html = "{\"data\":{\"loginCheck\":\"Y\"},\"httpstatus\":200,\"messages\":[],\"status\":true,\"validateMessages\":{},\"validateMessagesShowId\":\"_validatorMessage\"}";
		String filter = "{return str.data.loginCheck=='Y'}";
		System.out.println(eval(filter,""));
	}
	
}
