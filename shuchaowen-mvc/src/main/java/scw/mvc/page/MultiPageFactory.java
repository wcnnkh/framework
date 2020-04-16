package scw.mvc.page;

import java.util.LinkedList;

import scw.lang.UnsupportedException;

public class MultiPageFactory extends LinkedList<PageFactory> implements
		PageFactory {
	private static final long serialVersionUID = 1L;

	public Page getPage(String page) {
		for (PageFactory adapter : this) {
			if (adapter.isSupport((page))) {
				return adapter.getPage(page);
			}
		}
		throw new UnsupportedException("not support page: " + page);
	}

	public boolean isSupport(String page) {
		for (PageFactory adapter : this) {
			if (adapter.isSupport(page)) {
				return true;
			}
		}
		return false;
	}
}
