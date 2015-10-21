package edu.upenn.cis455.storage;

import static com.sleepycat.persist.model.Relationship.ONE_TO_MANY;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Channel {
	@PrimaryKey
	private String channelPath;
	@SecondaryKey(relate=ONE_TO_MANY)
	private Set<String> xpaths = new HashSet<String>();
	public String getChannelPath() {
		return channelPath;
	}
	public void setChannelPath(String channelPath) {
		this.channelPath = channelPath;
	}
	public Set<String> getXpaths() {
		return xpaths;
	}
	public void setXpaths(Set<String> xpaths) {
		this.xpaths = xpaths;
	}	
	
}
