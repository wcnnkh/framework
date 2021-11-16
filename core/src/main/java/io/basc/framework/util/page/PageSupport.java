package io.basc.framework.util.page;

import java.util.List;

import io.basc.framework.util.Assert;

public class PageSupport {
	private PageSupport() {
	}

	public static long getStart(long pageNumber, long limit) {
		Assert.isTrue(pageNumber > 0, "required pageNumber > 0");
		Assert.isTrue(limit > 0, "required limit > 0");
		return Math.max(0, (pageNumber - 1)) * limit;
	}

	/**
	 * 获取当前所在页码
	 * 
	 * @param start
	 * @param limit
	 * @return
	 */
	public static long getPageNumber(long start, long limit) {
		Assert.isTrue(limit > 0, "required limit > 0");
		Assert.isTrue(start >= 0, "required start >= 0");
		return (start / limit) + 1;
	}

	public static boolean hasMore(long total, long start, long limit) {
		Assert.isTrue(total >= 0, "required total >= 0");
		Assert.isTrue(limit > 0, "required limit > 0");
		Assert.isTrue(start >= 0, "required start >= 0");
		return start + limit < total;
	}

	public static long getPages(long total, long limit) {
		Assert.isTrue(total >= 0, "required total >= 0");
		Assert.isTrue(limit > 0, "required limit > 0");
		if (total <= limit || limit <= 0) {
			return 1;
		}

		return (long) Math.ceil(((double) total) / limit);
	}

	public static long getNextStart(long start, long limit) {
		Assert.isTrue(limit > 0, "required limit > 0");
		Assert.isTrue(start >= 0, "required start >= 0");
		return start + limit;
	}

	public static <T> SharedPage<T> toPage(long total, long pageNumber, long limit,
			List<T> list) {
		return new SharedPage<>(getStart(pageNumber, limit), list, limit, total);
	}
	
	public static <T> Pages<T> getPages(Page<T> page,
			CursorProcessor<Long, T> processor) {
		return new StreamPages<>(page, processor);
	}

	public static <K, T> Pageable<K, T> emptyPageable(K cursorId, long count) {
		return new SharedPageable<K, T>(cursorId, count);
	}

	public static <T> Page<T> emptyPage(long pageNumber, long count) {
		SharedPage<T> sharedPage = new SharedPage<>(count);
		sharedPage.setCursorId(getStart(pageNumber, count));
		return sharedPage;
	}

	public static <K, T> Pageables<K, T> emptyPageables(K cursorId, long count) {
		EmptyPageables<K, T> emptyPageables = new EmptyPageables<>();
		emptyPageables.setCursorId(cursorId);
		emptyPageables.setCount(count);
		return emptyPageables;
	}

	public static <T> Pages<T> emptyPages(long pageNumber, long count) {
		EmptyPages<T> emptyPages = new EmptyPages<>();
		emptyPages.setCursorId(getStart(pageNumber, count));
		emptyPages.setCount(count);
		return emptyPages;
	}
}
