package io.basc.framework.sql.orm;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.sql.Sql;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Processor;
import io.basc.framework.util.page.Paginations;

public class Query<T> implements Paginations<T> {
	private final ConnectionFactory connectionFactory;
	private final SqlDialect sqlDialect;
	private final Sql sql;
	private final long cursorId;
	private final long limit;
	private final Processor<ResultSet, T, ? extends Throwable> mapProcessor;

	@SuppressWarnings("unchecked")
	public Query(ConnectionFactory connectionFactory, SqlDialect sqlDialect, Sql sql, TypeDescriptor resultType) {
		this(connectionFactory, sqlDialect, sql, (e) -> (T) sqlDialect.convert(e, resultType));
	}

	public Query(ConnectionFactory connectionFactory, SqlDialect sqlDialect, Sql sql,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		PageRequest request = PageRequest.getPageRequest();
		this.connectionFactory = connectionFactory;
		this.sqlDialect = sqlDialect;
		this.sql = request == null ? sql : sqlDialect.toLimitSql(sql, request.getStart(), request.getPageSize());
		this.cursorId = request == null ? 0 : request.getStart();
		this.limit = request == null ? 0 : request.getPageSize();
		this.mapProcessor = mapProcessor;
	}

	private Query(ConnectionFactory connectionFactory, SqlDialect sqlDialect, Sql sql, long cursorId, long limit,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		this.connectionFactory = connectionFactory;
		this.sqlDialect = sqlDialect;
		this.sql = sql;
		this.cursorId = cursorId;
		this.limit = limit;
		this.mapProcessor = mapProcessor;
	}

	private volatile Long total;

	@Override
	public long getTotal() {
		if (total == null) {
			synchronized (this) {
				if (total == null) {
					Sql totalSql = sqlDialect.toCountSql(sql);
					total = connectionFactory.query(totalSql, (e) -> e.getLong(1)).first();
				}
			}
		}
		return total == null ? 0 : total;
	}

	@Override
	public long getLimit() {
		return limit > 0 ? limit : getTotal();
	}

	@Override
	public Long getCursorId() {
		return cursorId;
	}

	@Override
	public long getPages() {
		return limit > 0 ? Paginations.super.getPages() : 1;
	}

	@Override
	public long getPageNumber() {
		return limit > 0 ? Paginations.super.getPageNumber() : 1;
	}

	@Override
	public Long getNextCursorId() {
		return limit > 0 ? Paginations.super.getNextCursorId() : null;
	}

	@Override
	public boolean hasNext() {
		return limit > 0 ? Paginations.super.hasNext() : false;
	}

	@Override
	public boolean isEmpty() {
		Sql sql = sqlDialect.toLimitSql(this.sql, 0, 1);
		return connectionFactory.query(sql, Processor.identity()).isEmpty();
	}

	public Elements<T> getElements() {
		Sql iteratorSql = sql;
		if (limit > 0) {
			iteratorSql = sqlDialect.toLimitSql(sql, cursorId, limit);
		}
		return connectionFactory.query(iteratorSql, mapProcessor);
	}

	@Override
	public final Stream<T> stream() {
		return getElements().stream();
	}

	@Override
	public final Iterator<T> iterator() {
		return getElements().iterator();
	}

	@Override
	public List<T> getList() {
		return getElements().toList();
	}

	public Query<T> limit(long start, long count) {
		return jumpTo(start, count);
	}

	@Override
	public Query<T> jumpTo(Long cursorId, long count) {
		return new Query<>(connectionFactory, sqlDialect, this.sql, cursorId, count, mapProcessor);
	}

	public Query<T> jumpTo(PageRequest request) {
		return jumpTo(request.getStart(), request.getPageSize());
	}
}
