package com.ff.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;

public class ExprNodeFilter implements NodeFilter {
	private static final long serialVersionUID = 1L;
	
	NodeFilter filter = null;
	String expr = null;

	public String getExpr() {
		return expr;
	}

	public ExprNodeFilter(String expr){
		if(expr.matches("\\{.+\\}")){
			this.expr = expr;
		}else if(expr.indexOf(",")>0){
			String[] str = expr.split(",");
			for(String s : str){
				if(this.filter==null){
					filter = new ExprNodeFilter(s);
				}else{
					this.orFilter(new ExprNodeFilter(s));
				}
			}
		}else if(expr.indexOf(" ")>0){
			String[] str = expr.split(" ");
			for(int i=str.length-1;i>=0;i--){
				if(this.filter==null){
					filter = new ExprNodeFilter(str[i]);
				}else{
					this.andFilter(new HasParentFilter(new ExprNodeFilter(str[i])));
				}
			}
		}else{
			 if(expr.matches("^\\w+$")){
				filter = new TagNameFilter(expr);
			}else if(expr.matches("^#[^\\s]+$")){
				String id = expr.substring(1);
				filter = new HasAttributeFilter("id",id);
			}else if(expr.matches("^\\.[^\\s]+$")){
				filter = new CssSelectorNodeFilter(expr);
			}else if(expr.matches("(\\[\\w+(=[^\\]]*)?\\])+")){
				Pattern p = Pattern.compile("\\[\\w+(=[^\\]]*)?\\]");
				Matcher matcher = p.matcher(expr);
				while(matcher.find()){
					String str= matcher.group().replaceAll("[\\[\\]]", "");
					String[] kv = str.split("=");
					if(this.filter==null){
						filter = kv.length==1?new HasAttributeFilter(kv[0]):new HasAttributeFilter(kv[0],kv[1]);
					}else{
						this.andFilter(kv.length==1?new HasAttributeFilter(kv[0]):new HasAttributeFilter(kv[0],kv[1]));
					}
				}
			}else if(expr.matches("([#\\.]?\\w+)(\\[\\w+(=[^\\]]*)?\\])+")){
				Pattern p = Pattern.compile("[#\\.]?\\w+");
				Matcher matcher = p.matcher(expr);
				if(matcher.find()){
					filter = new ExprNodeFilter(matcher.group());
				}
				p = Pattern.compile("\\[\\w+(=[^\\]]*)?\\]");
				matcher = p.matcher(expr);
				while(matcher.find()){
					String str= matcher.group().replaceAll("[\\[\\]]", "");
					String[] kv = str.split("=");
					this.andFilter(kv.length==1?new HasAttributeFilter(kv[0]):new HasAttributeFilter(kv[0],kv[1]));
				}
			}
		}
		
	}
	
	private void andFilter(NodeFilter filter){
		this.filter = new AndFilter(this.filter,filter);
	}
	
	private void orFilter(NodeFilter filter){
		this.filter = new OrFilter(this.filter,filter);
	}

	@Override
	public boolean accept(Node arg0) {
		return filter==null?true:filter.accept(arg0);
	}

}
