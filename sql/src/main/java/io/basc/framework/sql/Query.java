package io.basc.framework.sql;

import java.sql.ResultSet;

import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Processor;
import io.basc.framework.util.page.InMemoryPaginations;
import io.basc.framework.util.page.Paginations;
import lombok.Data;

@Data
public class Query<T> implements Paginations<T> {
	private final ConnectionFactory connectionFactory;
	private Sql sql;
	private final Processor<ResultSet, T, ? extends Throwable> mapProcessor;
	private long limit;
	private long cursorId;

	public Query(ConnectionFactory connectionFactory, Sql sql,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		this.connectionFactory = connectionFactory;
		this.mapProcessor = mapProcessor;
		this.sql = sql;
		PageRequest request = PageRequest.getPageRequest();
		if (request != null) {
			setCursorId(request.getStart());
			setLimit(request.getPageSize());
		}
	}

	@Override
	public Long getCursorId() {
		return cursorId;
	}

	public Elements<T> getElements() {
		return connectionFactory.query(sql, mapProcessor);
	}

	public long getLimit() {
		return limit;
	}

	@Override
	public Long getNextCursorId() {
		return limit > 0 ? Paginations.super.getNextCursorId() : null;
	}

	@Override
	public long getPageNumber() {
		return limit > 0 ? Paginations.super.getPageNumber() : 1;
	}

	@Override
	public long getPages() {
		return limit > 0 ? Paginations.super.getPages() : 1;
	}

	@Override
	public long getTotal() {
		return getElements().count();
	}

	@Override
	public boolean hasNext() {
		return limit > 0 ? Paginations.super.hasNext() : false;
	}

	@Override
	public Paginations<T> jumpTo(Long cursorId, long count) {
		return new InMemoryPaginations<>(getElements(), cursorId == null ? 0 : cursorId, count);
	}

	public final Paginations<T> jumpTo(PageRequest request) {
		return jumpTo(request.getStart(), request.getPageSize());
	}

	public void setCursorId(long cursorId) {
		this.cursorId = cursorId;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public void setSql(Sql sql) {
		this.sql = sql;
	}
}
