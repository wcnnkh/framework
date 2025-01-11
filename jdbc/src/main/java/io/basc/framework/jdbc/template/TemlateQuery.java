package io.basc.framework.jdbc.template;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;
import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.collections.ElementsWrapper;
import io.basc.framework.util.function.Function;
import io.basc.framework.util.page.Paginations;

public class TemlateQuery<T> extends Query<T> {
	private static final long serialVersionUID = 1L;
	private transient final ConnectionFactory connectionFactory;
	private transient final SqlDialect sqlDialect;
	private transient final Sql sql;
	private transient final Function<ResultSet, T, ? extends Throwable> mapProcessor;

	@SuppressWarnings("unchecked")
	public TemlateQuery(ConnectionFactory connectionFactory, Sql sql, SqlDialect sqlDialect, TypeDescriptor resultType) {
		this(connectionFactory, sql, (e) -> (T) sqlDialect.convert(e, resultType), sqlDialect);
	}

	public TemlateQuery(ConnectionFactory connectionFactory, Sql sql,
			Function<ResultSet, T, ? extends Throwable> mapProcessor, SqlDialect sqlDialect) {
		super(connectionFactory.operations().prepare(sql).query().rows(mapProcessor));
		this.sql = sql;
		this.connectionFactory = connectionFactory;
		this.sqlDialect = sqlDialect;
		this.mapProcessor = mapProcessor;
	}

	public Elements<T> getElements() {
		if (connectionFactory == null) {
			return super.getElements();
		}

		Sql iteratorSql = sql;
		if (getPageSize() > 0) {
			iteratorSql = sqlDialect.toLimitSql(iteratorSql, getCursorId(), getPageSize());
		}
		Elements<T> elements = connectionFactory.operations().prepare(sql).query().rows(mapProcessor);
		return new Results(elements);
	}

	@Override
	public Paginations<T> jumpTo(Long cursorId, long count) {
		if (connectionFactory != null) {
			return super.jumpTo(cursorId, count);
		}

		TemlateQuery<T> query = new TemlateQuery<>(connectionFactory, sql, mapProcessor, sqlDialect);
		query.setCursorId(cursorId);
		query.setPageSize(count);
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
			Sql totalSql = sqlDialect.toCountSql(sql);
			return connectionFactory.query(totalSql, (e) -> e.getLong(1)).getElements().first();
		}

		@Override
		public Optional<T> findFirst() {
			Sql firstSql = sqlDialect.toLimitSql(sql, 0, 1);
			return connectionFactory.query(firstSql, mapProcessor).getElements().findFirst();
		}

		@Override
		public Optional<T> findAny() {
			return findFirst();
		}

		// TODO 优化其他方法
	}
}
