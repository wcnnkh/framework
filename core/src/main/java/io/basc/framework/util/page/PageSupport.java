package io.basc.framework.util.page;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Processor;

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

	public static <T> Pagination<T> toPage(long total, long pageNumber, long limit, List<T> list) {
		return new SharedPagination<>(getStart(pageNumber, limit), list, limit, total);
	}

	public static <K, T> Pageable<K, T> emptyPageable(K cursorId) {
		return new SharedPageable<K, T>(cursorId);
	}

	public static <K, T> Pageables<K, T> emptyPageables(K cursorId) {
		return new SharedPageables<K, T>(emptyPageables(cursorId), null);
	}

	public static <K, T> Page<K, T> emptyPage(K cursorId, long count) {
		return new SharedPage<K, T>(cursorId, Collections.emptyList(), count, 0);
	}

	public static <K, T> Pages<K, T> emptyPages(K cursorId, Long count) {
		return new SharedPages<K, T>(emptyPage(cursorId, count), null);
	}

	public static <T> Pagination<T> emptyPagination(long start, long count) {
		return new SharedPagination<T>(start, count);
	}

	public static <T> Paginations<T> emptyPaginations(long start, long count) {
		return new SharedPaginations<T>(emptyPagination(start, count), null);
	}

	public static <K, T, V> Collection<? extends Callable<V>> toGroupTasks(Pageables<K, T> pageables,
			Processor<? super Pageable<K, T>, ? extends V, ? extends Exception> processor) {
		return pageables.pages().map((e) -> {
			return new Callable<V>() {

				@Override
				public V call() throws Exception {
					return processor.process(e);
				}
			};
		}).collect(Collectors.toList());
	}
}
