package com.ff;

import java.io.File;

import com.ff.config.Machine;

public class WebMain{
	
	public static final String configFile = WebMain.class.getResource("/config.xml").getFile();

	public WebMain(){
		
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Machine m = Machine.parseFrom(new File(configFile));
		new Client(m.getUsers().get(0),m.getRequests()).start();
	}

}
