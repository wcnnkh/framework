package scw.util.page;

import java.util.List;

import scw.core.Assert;

public class PageSupport {
	private PageSupport() {
	}

	public static long getStart(long pageNumber, long limit) {
		Assert.isTrue(pageNumber >= 0, "required pageNumber >= 0");
		Assert.isTrue(limit > 0, "required limit > 0");
		return Math.max(0, (pageNumber - 1)) * limit;
	}

	/**
	 * 获取当前所在页码
	 * 
	 * @param limit
	 * @param start
	 * @return
	 */
	public static long getPageNumber(long limit, long start) {
		Assert.isTrue(limit > 0, "required limit > 0");
		Assert.isTrue(start >= 0, "required start >= 0");
		return (start / limit) + 1;
	}

	public static boolean hasMore(long total, long limit, long start) {
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

	public static <T> Page<T> toPage(long total, long pageNumber, long limit,
			List<T> list) {
		return new SharedPage<>(getStart(pageNumber, limit), list, limit, total);
	}

	public static <K, T> Cursors<K, T> getCursors(Cursor<K, T> cursor,
			PageableProcessor<K, T> pageableProcessor) {
		return new JumpCursors<>(cursor, pageableProcessor);
	}

	public static <T> Pages<T> getPages(Page<T> page,
			CursorProcessor<Long, T> processor) {
		return new JumpPages<T>(page, processor);
	}

	public static <K, T> Pageable<K, T> emptyPageable(K cursorId, long count) {
		return new EmptyPageable<K, T>(cursorId, count);
	}

	public static <K, T> Cursor<K, T> emptyCursor(K cursorId, long count) {
		return new EmptyCursor<K, T>(cursorId, count);
	}

	public static <T> Page<T> emptyPage(long pageNumber, long count) {
		return new EmptyPage<T>(getStart(pageNumber, count), count);
	}

	public static <K, T> Pageables<K, T> emptyPageables(K cursorId, long count) {
		return new EmptyPageables<K, T>(cursorId, count);
	}

	public static <K, T> Cursors<K, T> emptyCursors(K cursorId, long count) {
		return new EmptyCursors<K, T>(cursorId, count);
	}

	public static <T> Pages<T> emptyPages(long pageNumber, long count) {
		return new EmptyPages<T>(getStart(pageNumber, count), count);
	}
}
