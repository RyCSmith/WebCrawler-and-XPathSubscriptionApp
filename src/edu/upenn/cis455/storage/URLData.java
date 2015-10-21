package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import static com.sleepycat.persist.model.Relationship.*;

import com.sleepycat.persist.model.SecondaryKey;
@Entity

public class URLData {
	public enum Type {XML, HTML, RSS}
	
	@PrimaryKey
	private String url;
	@SecondaryKey(relate=MANY_TO_ONE)
	private int contentSize;
	@SecondaryKey(relate=MANY_TO_ONE)
	private long lastAccessed;
	@SecondaryKey(relate=MANY_TO_ONE)
	Type type;
	@SecondaryKey(relate=MANY_TO_ONE)
	private String content;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getContentSize() {
		return contentSize;
	}
	public void setContentSize(int contentSize) {
		this.contentSize = contentSize;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getLastAccessed() {
		return lastAccessed;
	}
	public void setLastAccessed(long lastAccessed) {
		this.lastAccessed = lastAccessed;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
}
