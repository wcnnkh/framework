package shuchaowen.cms.result;

import java.io.Serializable;
import java.util.List;

import shuchaowen.cms.pojo.Content;

public class ContentTree implements Serializable{
	private static final long serialVersionUID = 1L;
	private Content content;
	private List<ContentTree> subList;
	
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}
	public List<ContentTree> getSubList() {
		return subList;
	}
	public void setSubList(List<ContentTree> subList) {
		this.subList = subList;
	}
}
