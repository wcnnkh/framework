package io.basc.framework.sql.orm;

import java.sql.ResultSet;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.sql.Sql;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.page.Paginations;

public class Query<T> implements Paginations<T> {
	private final ConnectionFactory connectionFactory;
	private final SqlDialect sqlDialect;
	private final Sql sql;
	private final long cursorId;
	private final long count;
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
		this.count = request == null ? 0 : request.getPageSize();
		this.mapProcessor = mapProcessor;
	}

	private Query(ConnectionFactory connectionFactory, SqlDialect sqlDialect, Sql sql, long cursorId, long count,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		this.connectionFactory = connectionFactory;
		this.sqlDialect = sqlDialect;
		this.sql = sql;
		this.cursorId = cursorId;
		this.count = count;
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
	public long getCount() {
		return count > 0 ? count : getTotal();
	}

	@Override
	public Long getCursorId() {
		return cursorId;
	}

	@Override
	public long getPages() {
		return count > 0 ? Paginations.super.getPages() : 1;
	}

	@Override
	public long getPageNumber() {
		return count > 0 ? Paginations.super.getPageNumber() : 1;
	}

	@Override
	public Long getNextCursorId() {
		return count > 0 ? Paginations.super.getNextCursorId() : null;
	}

	@Override
	public boolean hasNext() {
		return count > 0 ? Paginations.super.hasNext() : false;
	}

	@Override
	public boolean isPresent() {
		Sql sql = sqlDialect.toLimitSql(this.sql, 0, 1);
		return connectionFactory.query(sql, Processor.identity()).isPresent();
	}

	@Override
	public Cursor<T> iterator() {
		Sql iteratorSql = sql;
		if (count > 0) {
			iteratorSql = sqlDialect.toLimitSql(sql, cursorId, count);
		}
		return connectionFactory.query(iteratorSql, mapProcessor).iterator();
	}

	@Override
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
