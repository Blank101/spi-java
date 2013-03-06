/**
 * Copyright (c) 2012 Sam MacPherson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
