package edu.upenn.cis455.storage;

import static com.sleepycat.persist.model.Relationship.ONE_TO_MANY;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class XPath {
	
	@PrimaryKey
	private String xpathString;
	@SecondaryKey(relate=ONE_TO_MANY)
	private Set<String> articleUrls = new HashSet<String>();
	
	public String getXpathString() {
		return xpathString;
	}
	public void setXpathString(String xpathString) {
		this.xpathString = xpathString;
	}
	public Set<String> getArticleUrls() {
		return articleUrls;
	}
	public void setArticleUrls(Set<String> articleUrls) {
		this.articleUrls = articleUrls;
	}
	
}
