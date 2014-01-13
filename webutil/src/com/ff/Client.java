package com.ff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.ff.config.ExprNodeFilter;
import com.ff.config.Request;
import com.ff.config.User;
import com.ff.util.OCR;

public class Client extends Thread  {
	
	private HttpClient httpclient = wrapClient(new DefaultHttpClient(
			new ThreadSafeClientConnManager()));
	
	private static final int retryCount = 3;
	
	private User user = null;
	private List<Request> requests = null;
	private NodeList result = null;
	private String html = null;
	private int retry = 0;
	ScriptEngineManager mgr = new ScriptEngineManager(); 
	ScriptEngine engine = mgr.getEngineByName("JavaScript");
	
	public Client(User user,List<Request> requests){
		this.user = user;
		this.requests = requests;
	}
	
	
	public Boolean isOk(String code,String param){
		try {
			return (Boolean)engine.eval("var param="+param+"||'';function parseResult(str)"+code+";parseResult(param)");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void write(InputStream is,File sf) throws Exception {  
        // 1K的数据缓冲  
        byte[] bs = new byte[1024];  
        // 读取到的数据长度  
        int len;  
       if(!sf.getParentFile().exists()){  
           sf.getParentFile().mkdirs();  
       }  
       OutputStream os = new FileOutputStream(sf.getPath());  
        // 开始读取  
        while ((len = is.read(bs)) != -1) {  
          os.write(bs, 0, len);  
        }  
        // 完毕，关闭所有链接  
        os.close();  
        is.close();  
    }   
	
	public  org.apache.http.client.HttpClient wrapClient(org.apache.http.client.HttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
            ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
            return new DefaultHttpClient(mgr, base.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
	
	private int excuteReq(Request req){
		
		if(result!=null){
			if(result.size()==0){
				if(retry == retryCount){
					System.out.println("重试次数达到"+retryCount+"次，退出程序");
					return requests.size()+1;
				}
				retry++;
				result = null;
				if(req.getReponse().getBack()!=null){
					return Integer.parseInt(req.getReponse().getBack());
				}
				return -1;
			}
		}
		
		if(html!=null && req.getReponse().getFilter()!=null){
			if((html.indexOf("{")>=0 || html.indexOf("[")>=0)){
				if(!isOk(((ExprNodeFilter)req.getReponse().getFilter()).getExpr(),html)){
					if(retry == retryCount){
						System.out.println("重试次数达到"+retryCount+"次，退出程序");
						return requests.size()+1;
					}
					retry++;
					html = null;
					if(req.getReponse().getBack()!=null){
						return Integer.parseInt(req.getReponse().getBack());
					}
					return -1;
				}
			}
		}
		
		prepare(req);
		
		HttpRequestBase request = null;
		String m = req.getMethod();
		if("POST".equalsIgnoreCase(m)){
			request = new HttpPost(req.getUrl());
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for(Entry<String,String> entry:req.getParams().entrySet()){
					params.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
				}
				((HttpPost)request).setEntity(new UrlEncodedFormEntity(params,
						req.getCharset()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			StringBuffer params = new StringBuffer("?");
			for(String key:req.getParams().keySet()){
				params.append(key).append("=").append(req.getParams().get(key)).append("&");
			}
			if(params.length()>1){
				request = new HttpGet(req.getUrl()+params.substring(0, params.length()-1)); 
			}else{
				request = new HttpGet(req.getUrl()); 
			}
		}
		for(Entry<String,String> entry:req.getHeaders().entrySet()){
			request.setHeader(entry.getKey(),entry.getValue());
		}
		
		try {
			HttpResponse response = null; 
			System.out.println(request.getURI());
			response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			InputStream responStream = null;
			responStream = entity.getContent();
			
			if("img".equalsIgnoreCase(req.getReponse().getType())){
				File imageFile = new File("d:/temp/"+UUID.randomUUID());
				try {
					write(responStream,imageFile);
					html = new OCR().recognizeText(imageFile );
//					imageFile.delete();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					html = "";
				}
				System.out.println("验证码："+html);
				result = null;
			}else{
				BufferedReader reader = null;
				reader = new BufferedReader(new InputStreamReader(responStream,
						req.getReponse().getCharset()));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
//			System.out.println(sb.toString());
				try {
					Parser parser = new Parser();
					parser.setResource(sb.toString());
					result = parser.parse(req.getReponse().getFilter());
					html = null;
				} catch (ParserException e) {
					html = sb.toString();
					System.out.println(html);
					result = null;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		if(req.getReponse().getBack()!=null){
//			return Integer.parseInt(req.getReponse().getBack());
//		}
		return 1;
	}
	
	

	private void prepare(Request req) {
		if(req.getUrl_depend()!=null){
			req.setUrl(getValFromResult(req.getUrl_depend_prefix())+getValFromResult(req.getUrl_depend())+getValFromResult(req.getUrl_depend_suffix()));
		}
		Map<String,String> params = new LinkedHashMap<String,String>();
		for(String key:req.getParams().keySet()){
			String value = req.getParams().get(key);
			params.put(getValFromResult(key), getValFromResult(value));
		}
		if(params.size()>0){
			req.setParams(params);
		}
	}
	
	private String getValFromResult(String str){
		if(str == null || str.length()==0)return "";
		int i = 0;
		if(str.matches("\\d+:\\[.+\\]")){
			i = Integer.valueOf(str.split(":")[0]);
			Node ele = result.elementAt(i);
			if(ele instanceof TagNode){
				TagNode tag = (TagNode)ele;
				String p = str.split(":")[1];
				if(p.matches("\\[.+\\]")){
					p = p.substring(1, p.length()-1);
					return tag.getAttribute(p).replaceAll("&amp;", "&");
				}else if(p.matches("\\[\\]")){
					return getText(tag);
				}
			}
		}else if(str.matches("substr:\\d+(,\\d+)?")){
			String index = str.split(":")[1];
			String[] beginend = index.split(",");
			return beginend.length==1?html.substring(Integer.parseInt(beginend[0])):html.substring(Integer.parseInt(beginend[0],Integer.parseInt(beginend[1])));
		}
		return str;
	}
	
	private String getText(Node node){
		if(node instanceof RemarkNode){
			return "";
		}else if(node instanceof TextNode){
			return ((TextNode)node).getText();
		}else{
			StringBuffer sb = new StringBuffer();
			if(node.getChildren()!=null && node.getChildren().size()>0){
				for(int i=0;i<node.getChildren().size();i++){
					sb.append(getText(node.getChildren().elementAt(i)));
				}
			}
			return sb.toString();
		}
	}

	@Override
	public void run() {
		for(int i=0;i<requests.size();){
			i += excuteReq(requests.get(i));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(user!=null)System.out.println(user.getId()+" complete.");
	}
	
}
