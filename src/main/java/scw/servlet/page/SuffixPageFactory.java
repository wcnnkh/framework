package scw.servlet.page;

import java.util.LinkedList;

import scw.core.exception.NotFoundException;

public class SuffixPageFactory implements PageFactory {
	private LinkedList<SuffixPageInfo> suffixPageInfos = new LinkedList<SuffixPageInfo>();

	public synchronized void register(String suffix, PageFactory pageFactory) {
		suffixPageInfos.add(new SuffixPageInfo(suffix, pageFactory));
	}

	public Page create(String page) {
		for (SuffixPageInfo suffixPageInfo : suffixPageInfos) {
			if (page.endsWith(suffixPageInfo.getSuffix())) {
				return suffixPageInfo.getPageFactory().create(page);
			}
		}
		throw new NotFoundException("匹配不到指定的PageFactory：" + page);
	}
}

final class SuffixPageInfo {
	private final String suffix;
	private final PageFactory pageFactory;

	public SuffixPageInfo(String suffix, PageFactory pageFactory) {
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
