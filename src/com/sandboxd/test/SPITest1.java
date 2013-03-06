package com.sandboxd.test;

import java.net.URL;

import com.sandboxd.rpc.SPIClient;
import com.sandboxd.rpc.SPIConfigImpl;

public class SPITest1 {
	
	public static void main(String[] args) throws Exception {
		/*HttpURLConnection conn = (HttpURLConnection)new URL("http://" + args[0] + "/spi").openConnection();
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "text/xml");
		conn.setRequestMethod("POST");
		conn.getOutputStream().write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodCall><methodName>spi.stat</methodName><params><param><value><i4>2</i4></value></param><param><value>aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa</value></param><param><value><i4>1</i4></value></param><param><value><string>stat</string></value></param><param><value><double>1.0</double></value></param></params></methodCall>".getBytes());
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		//System.out.println(in.read());
		//System.out.println(in.read());
		//System.out.println(in.read());
		String inputLine;
		while ((inputLine = in.readLine()) != null) System.out.println(inputLine);
		in.close();
		for (Entry<String, List<String>> i : conn.getHeaderFields().entrySet()) {
			System.out.print(i.getKey() + ": ");
			for (String o : i.getValue()) {
				System.out.print(o + ",");
			}
			System.out.println();
		}*/
		
		SPIConfigImpl config = new SPIConfigImpl();
	    config.setServerURL(new URL("http://" + args[0] + "/spi"));
	    config.setAPIKey(2, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
	    config.setReplyTimeout(600);
	    
	    SPIClient client = new SPIClient();
	    client.setConfig(config);
	    
	    System.out.println(client.getUser(1).name);
	}

}
