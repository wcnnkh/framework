package io.basc.framework.util.page;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.basc.framework.util.Assert;
import io.basc.framework.util.collection.Elements;

public class Pagination<T> implements Page<Long, T>, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private long cursorId;
	private long pageSize;
	private Long total;
	private Elements<T> elements = Elements.empty();

	public Pagination() {
	}

	public Pagination(Pagination<T> pagination) {
		this.cursorId = pagination.cursorId;
		this.pageSize = pagination.pageSize;
		this.total = pagination.total;
		this.elements = pagination.elements;
	}

	@Override
	public Pagination<T> clone() {
		return new Pagination<>(this);
	}

	public Long getCursorId() {
		return cursorId;
	}

	public void setCursorId(long cursorId) {
		Assert.isTrue(cursorId >= 0, "cursorId[" + cursorId + "] cannot be less than 0");
		this.cursorId = cursorId;
	}

	public long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long pageSize) {
		Assert.isTrue(pageSize > 0, "limit[" + pageSize + "] greater than 0 is required");
		this.pageSize = pageSize;
	}

	public long getTotal() {
		return total == null ? elements.count() : total;
	}

	public void setTotal(Long total) {
		Assert.isTrue(cursorId >= 0, "total[" + cursorId + "] cannot be less than 0");
		this.total = total;
	}

	public Elements<T> getElements() {
		return elements;
	}

	public void setElements(Elements<T> elements) {
		Assert.requiredArgument(elements != null, "elements");
		this.elements = elements;
	}

	/**
	 * 获取当前页码
	 * 
	 * @return
	 */
	public long getPageNumber() {
		return PageSupport.getPageNumber(getCursorId(), getPageSize());
	}

	/**
	 * 总页数
	 * 
	 * @return
	 */
	public long getPages() {
		return PageSupport.getPages(getTotal(), getPageSize());
	}

	public boolean hasPrevious() {
		return getPageNumber() > 1;
	}

	@Override
	public Long getNextCursorId() {
		if (!PageSupport.hasMore(getTotal(), cursorId, getPageSize())) {
			return null;
		}

		return PageSupport.getNextStart(cursorId, getPageSize());
	}

	private void writeObject(ObjectOutputStream output) throws IOException {
		output.writeLong(cursorId);
		output.writeLong(pageSize);
		output.writeLong(total);
		if (elements instanceof Serializable) {
			output.writeObject(elements);
		} else {
			output.writeObject(elements.toList());
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
		this.cursorId = input.readLong();
		this.pageSize = input.readLong();
		this.total = input.readLong();
		this.elements = (Elements<T>) input.readObject();
	}
}
