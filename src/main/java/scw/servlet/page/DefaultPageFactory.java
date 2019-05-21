package scw.servlet.page;

import java.util.LinkedList;

import scw.core.exception.NotFoundException;

public class DefaultPageFactory implements PageFactory {
	private LinkedList<SuffixPageFactory> suffixPageFactories = new LinkedList<SuffixPageFactory>();

	public synchronized void register(String suffix, PageFactory pageFactory) {
		suffixPageFactories.add(new SuffixPageFactory(suffix, pageFactory));
	}

	public Page create(String page) {
		for (SuffixPageFactory suffixFactory : suffixPageFactories) {
			if (page.endsWith(suffixFactory.getSuffix())) {
				return suffixFactory.getPageFactory().create(page);
			}
		}
		throw new NotFoundException("匹配不到指定的PageFactory：" + page);
	}
}

final class SuffixPageFactory {
	private final String suffix;
	private final PageFactory pageFactory;

	public SuffixPageFactory(String suffix, PageFactory pageFactory) {
		this.suffix = suffix;
		this.pageFactory = pageFactory;
	}

	public String getSuffix() {
		return suffix;
	}

	public PageFactory getPageFactory() {
		return pageFactory;
	}
}
