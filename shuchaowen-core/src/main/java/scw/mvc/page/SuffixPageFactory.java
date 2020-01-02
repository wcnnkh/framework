package scw.mvc.page;

import java.util.LinkedList;

import scw.lang.NotFoundException;
import scw.mvc.servlet.page.JspPageFactory;

public class SuffixPageFactory implements PageFactory {
	private LinkedList<SuffixPageInfo> suffixPageInfos = new LinkedList<SuffixPageInfo>();

	{
		registerLast(".jsp", JspPageFactory.instance);
	}

	public void registerLast(String suffix, PageFactory pageFactory) {
		synchronized (suffixPageInfos) {
			suffixPageInfos.add(new SuffixPageInfo(suffix, pageFactory));
		}
	}

	public synchronized void registerFirst(String suffix, PageFactory pageFactory) {
		synchronized (suffixPageInfos) {
			suffixPageInfos.addFirst(new SuffixPageInfo(suffix, pageFactory));
		}
	}

	public Page getPage(String page) {
		for (SuffixPageInfo suffixPageInfo : suffixPageInfos) {
			if (page.endsWith(suffixPageInfo.getSuffix())) {
				return suffixPageInfo.getPageFactory().getPage(page);
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
