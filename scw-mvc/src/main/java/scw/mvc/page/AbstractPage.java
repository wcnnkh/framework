package scw.mvc.page;

import java.util.LinkedHashMap;

public abstract class AbstractPage extends LinkedHashMap<String, Object> implements Page {
	private static final long serialVersionUID = 1L;
	private String page;

	public AbstractPage(String page) {
		this.page = page;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
	
	@Override
	public String toString() {
		if(super.isEmpty()){
			return page;
		}
		
		return page + " --> " + super.toString();
	}
}
