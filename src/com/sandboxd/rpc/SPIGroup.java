package com.sandboxd.rpc;

import java.util.List;

/**
 * Represents a group within SandBoxd.
 */
public class SPIGroup {
	
	public final int gid;
	public final String name;
	public final List<Integer> members;
	
	public SPIGroup (int gid, String name, List<Integer> members) {
		this.gid = gid;
		this.name = name;
		this.members = members;
	}
	
}
