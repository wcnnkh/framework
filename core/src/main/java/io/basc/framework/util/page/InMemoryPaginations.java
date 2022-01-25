package io.basc.framework.util.page;

import io.basc.framework.util.Assert;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 在内存中分页
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public class InMemoryPaginations<T> implements Paginations<T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final List<T> source;
	private final int start;
	private final int count;

	public InMemoryPaginations(List<T> source, int start, int count) {
		Assert.requiredArgument(source != null, "source");
		Assert.isTrue(start >= 0, "start[" + start + "] cannot be less than 0");
		Assert.isTrue(count > 0, "count[" + count + "] greater than 0 is required");
		this.start = start;
		this.source = source;
		this.count = count;
	}

	@Override
	public long getTotal() {
		return source.size();
	}

	@Override
	public long getCount() {
		return count;
	}

	@Override
	public Long getCursorId() {
		return (long) start;
	}

	@Override
	public Long getNextCursorId() {
		int end = start + count;
		if (end <= 0 || end >= source.size()) {
			// 超过数量或int溢出了
			return null;
		}
		return (long) Math.min(end, source.size());
	}

	@Override
	public List<T> getList() {
		int end = Math.min(start + count, source.size());
		if (start >= end) {
			return Collections.emptyList();
		}

		return source.subList(start, end);
	}

	@Override
	public Iterator<T> iterator() {
		return getList().iterator();
	}

	@Override
	public Paginations<T> jumpTo(Long cursorId, long count) {
		Assert.requiredArgument(cursorId != null, "cursorId");
		Assert.isTrue(cursorId <= Integer.MAX_VALUE);
		Assert.isTrue(count <= Integer.MAX_VALUE);
		return new InMemoryPaginations<T>(source, cursorId.intValue(), (int) count);
	}
}