package com.sandboxd.rpc;

import java.util.List;
import java.util.Map;

/**
 * Represents a SandBoxd user.
 */
public class SPIUser {
	
	public final int uid;
	public final String sid;
	public final String name;
	public final String avatar;
	public final int gid;
	public final List<Integer> friends;
	public final Map<String, Integer> achievements;
	public final Map<String, String> stats;
	public final List<String> microids;
	
	public SPIUser (int uid, String sid, String name, String avatar, int gid, List<Integer> friends, Map<String, Integer> achievements, Map<String, String> stats, List<String> microids) {
		this.uid = uid;
		this.sid = sid;
		this.name = name;
		this.avatar = avatar;
		this.gid = gid;
		this.friends = friends;
		this.achievements = achievements;
		this.stats = stats;
		this.microids = microids;
	}
	
	public SPIUser (int uid, String sid, String name, String avatar, int gid, List<Integer> friends, Map<String, Integer> achievements, Map<String, String> stats) {
		this(uid, sid, name, avatar, gid, friends, achievements, stats, null);
	}
	
}
