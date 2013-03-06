package com.sandboxd.rpc;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * An SPI configuration. If setAPIKey is not called then SPIClient will act in development mode.
 */
public class SPIConfigImpl extends XmlRpcClientConfigImpl {
	
	private static final long serialVersionUID = 1L;
	
	private int gameid;
	private String apiKey;
	
	public void setAPIKey (int gameid, String apiKey) {
		this.gameid = gameid;
		this.apiKey = apiKey;
	}
	
	public int getGameId () {
		return gameid;
	}
	
	public String getAPIKey () {
		return apiKey;
	}

}
