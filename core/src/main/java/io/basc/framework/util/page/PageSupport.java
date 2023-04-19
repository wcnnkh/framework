package io.basc.framework.util.page;

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
		return (total - start) <= limit;
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
		return Math.max(start, start + limit);
	}
}
