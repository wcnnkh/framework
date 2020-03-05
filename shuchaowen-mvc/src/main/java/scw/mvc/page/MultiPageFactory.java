package scw.mvc.page;

import java.util.LinkedList;

import scw.lang.NotSupportException;

public class MultiPageFactory extends LinkedList<PageFactoryAdapter> implements PageFactory {
	private static final long serialVersionUID = 1L;

	public Page getPage(String page) {
		for (PageFactoryAdapter adapter : this) {
			if (adapter.isAdapte(page)) {
				return adapter.getPage(page);
			}
		}
		throw new NotSupportException("not support adapter:" + page);
	}
}
