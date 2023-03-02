package io.basc.framework.data.domain;

import java.io.Serializable;

import io.basc.framework.env.BascObject;
import io.basc.framework.env.Sys;
import io.basc.framework.event.Observable;
import io.basc.framework.lang.NamedInheritableThreadLocal;
import io.basc.framework.util.Assert;
import io.basc.framework.util.page.PageSupport;

/**
 * 分页请求
 * 
 * @author wcnnkh
 *
 */
public class PageRequest extends BascObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Observable<Long> DEFAULT_PAGE_SIZE = Sys.getEnv().getProperties()
			.getObservable("data.page.request.size").map((e) -> e.or(10L).getAsLong());

	private static final ThreadLocal<PageRequest> LOCAL = new NamedInheritableThreadLocal<PageRequest>(
			PageRequest.class.getName());

	public static ThreadLocal<PageRequest> getLocal() {
		return LOCAL;
	}

	public static PageRequest getPageRequest() {
		return LOCAL.get();
	}

	public static void startPageRequest(PageRequest request) {
		LOCAL.set(request);
	}

	public static void startPageRequest(long start, long limit) {
		startPageRequest(build(start, limit));
	}

	public static void clearPageRequest() {
		LOCAL.remove();
	}

	public static PageRequest build(long start, long limit) {
		return new PageRequest(PageSupport.getPageNumber(start, limit), limit);
	}

	private long pageNum;
	private long pageSize;

	/**
	 * 默认的分页策略
	 */
	public PageRequest() {
		this(1, DEFAULT_PAGE_SIZE.get());
	}

	public PageRequest(long pageNum, long pageSize) {
		Assert.requiredArgument(pageNum > 0, "pageNum");
		Assert.requiredArgument(pageSize > 0, "pageSize");
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}

	public PageRequest(PageRequest request) {
		this.pageNum = request.pageNum;
		this.pageSize = request.pageSize;
	}

	public final long getPageNum() {
		return pageNum;
	}

	public void setPageNum(long pageNum) {
		Assert.requiredArgument(pageNum > 0, "pageNum");
		this.pageNum = pageNum;
	}

	public final long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long pageSize) {
		Assert.requiredArgument(pageSize > 0, "pageSize");
		this.pageSize = pageSize;
	}

	public final long getStart() {
		return PageSupport.getStart(pageNum, pageSize);
	}
}
