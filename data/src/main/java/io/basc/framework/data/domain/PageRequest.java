package io.basc.framework.data.domain;

import java.io.Serializable;

import io.basc.framework.beans.factory.config.InheritableThreadLocalConfigurator;
import io.basc.framework.env.Sys;
import io.basc.framework.event.Observable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.page.PageSupport;
import lombok.Data;

/**
 * 分页请求
 * 
 * @author wcnnkh
 *
 */
@Data
public class PageRequest implements Serializable {
	private static final InheritableThreadLocalConfigurator<PageRequest> CONFIGURATOR = new InheritableThreadLocalConfigurator<>(
			PageRequest.class, Sys.getEnv());
	private static final Observable<Long> DEFAULT_PAGE_SIZE = Sys.getEnv().getProperties()
			.getObservable("data.page.request.size").map((e) -> e.or(10L).getAsLong());

	private static final long serialVersionUID = 1L;

	public static PageRequest build(long start, long limit) {
		return new PageRequest(PageSupport.getPageNumber(start, limit), limit);
	}

	public static void clearPageRequest() {
		CONFIGURATOR.remove();
	}

	public static InheritableThreadLocalConfigurator<PageRequest> getConfigurator() {
		return CONFIGURATOR;
	}

	public static PageRequest getPageRequest() {
		return CONFIGURATOR.get();
	}

	public static void startPageRequest(long start, long limit) {
		startPageRequest(build(start, limit));
	}

	public static void startPageRequest(PageRequest request) {
		CONFIGURATOR.set(request);
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

	public final long getPageSize() {
		return pageSize;
	}

	public final long getStart() {
		return PageSupport.getStart(pageNum, pageSize);
	}

	public void setPageNum(long pageNum) {
		Assert.requiredArgument(pageNum > 0, "pageNum");
		this.pageNum = pageNum;
	}

	public void setPageSize(long pageSize) {
		Assert.requiredArgument(pageSize > 0, "pageSize");
		this.pageSize = pageSize;
	}
}
