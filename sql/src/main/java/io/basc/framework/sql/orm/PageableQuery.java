package io.basc.framework.sql.orm;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.sql.Query;
import io.basc.framework.sql.Sql;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ElementsWrapper;
import io.basc.framework.util.Optional;
import io.basc.framework.util.Processor;

public class PageableQuery<T> extends Query<T> {
	private final SqlDialect sqlDialect;

	@SuppressWarnings("unchecked")
	public PageableQuery(ConnectionFactory connectionFactory, Sql sql, SqlDialect sqlDialect,
			TypeDescriptor resultType) {
		this(connectionFactory, sql, (e) -> (T) sqlDialect.convert(e, resultType), sqlDialect);
	}

	public PageableQuery(ConnectionFactory connectionFactory, Sql sql,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor, SqlDialect sqlDialect) {
		super(connectionFactory, sql, mapProcessor);
		this.sqlDialect = sqlDialect;
	}

	public Elements<T> getElements() {
		Sql iteratorSql = getSql();
		if (getLimit() > 0) {
			iteratorSql = sqlDialect.toLimitSql(getSql(), getCursorId(), getLimit());
		}
		Elements<T> elements = getConnectionFactory().query(iteratorSql, getMapProcessor());
		return new Results(elements);
	}

	public PageableQuery<T> limit(long start, long count) {
		return jumpTo(start, count);
	}

	@Override
	public PageableQuery<T> jumpTo(Long cursorId, long count) {
		PageableQuery<T> query = new PageableQuery<>(getConnectionFactory(), getSql(), getMapProcessor(), sqlDialect);
		query.setCursorId(cursorId);
		query.setLimit(count);
		return query;
	}

	private class Results extends ElementsWrapper<T, Elements<T>> {

		public Results(Elements<T> wrappedTarget) {
			super(wrappedTarget);
		}

		@Override
		public Stream<T> stream() {
			return getElements().stream();
		}

		@Override
		public Iterator<T> iterator() {
			return getElements().iterator();
		}

		@Override
		public long count() {
			Sql totalSql = sqlDialect.toCountSql(getSql());
			return getConnectionFactory().query(totalSql, (e) -> e.getLong(1)).first();
		}

		@Override
		public Optional<T> findFirst() {
			Sql firstSql = sqlDialect.toLimitSql(getSql(), 0, 1);
			return getConnectionFactory().query(firstSql, getMapProcessor()).findFirst();
		}

		@Override
		public Optional<T> findAny() {
			return findFirst();
		}

		// TODO 优化其他方法
	}
}
