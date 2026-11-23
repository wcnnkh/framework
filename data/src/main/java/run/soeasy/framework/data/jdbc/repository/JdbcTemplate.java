package run.soeasy.framework.data.jdbc.repository;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.exchange.Stage;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.data.jdbc.ConnectionFactory;
import run.soeasy.framework.data.jdbc.Sql;
import run.soeasy.framework.data.jdbc.convert.JdbcMapper;
import run.soeasy.framework.data.jdbc.dialect.CommonSqlDialect;
import run.soeasy.framework.data.jdbc.dialect.SqlDialect;

@Getter
@RequiredArgsConstructor
public class JdbcTemplate {
	@NonNull
	private final ConnectionFactory connectionFactory;
	@NonNull
	private final JdbcMapper jdbcMapper;
	@NonNull
	private final SqlDialect sqlDialect;

	public JdbcTemplate(@NonNull ConnectionFactory connectionFactory) {
		this(connectionFactory, new JdbcMapper(), new CommonSqlDialect());
	}

	public <T> Signal delete(@NonNull TypeDescriptor entityType, @NonNull T entity) {
		Streamable<PropertyAccessor> columns = jdbcMapper.getColumns(entityType, entity);
		Sql sql = sqlDialect.toDeleteSql(entityType, columns);
		return connectionFactory.executeUpdate(sql);
	}

	public <T> Stage<Integer> insert(@NonNull TypeDescriptor entityType, @NonNull T entity) {
		Streamable<PropertyAccessor> columns = jdbcMapper.getColumns(entityType, entity);
		Sql sql = sqlDialect.toInsertSql(entityType, columns);
		return connectionFactory.executeUpdate(sql);
	}

	public final <T> Streamable<T> query(@NonNull Class<T> entityType, @NonNull Sql sql) {
		return query(TypeDescriptor.valueOf(entityType), sql);
	}

	public final <T> Streamable<T> query(@NonNull Class<T> entityType, @NonNull String statement,
			@NonNull Object... params) {
		return query(entityType, new Sql(statement, params));
	}

	public final <T> Streamable<T> query(@NonNull Class<T> entityType, @NonNull T entity) {
		return query(TypeDescriptor.valueOf(entityType), entity);
	}

	@SuppressWarnings("unchecked")
	public <T> Streamable<T> query(@NonNull TypeDescriptor entityType, @NonNull Sql sql) {
		return connectionFactory.query(sql, (resultSet) -> (T) jdbcMapper.convert(resultSet, entityType));
	}

	public final <T> Streamable<T> query(@NonNull TypeDescriptor entityType, @NonNull String statement,
			@NonNull Object... params) {
		return query(entityType, new Sql(statement, params));
	}

	public <T> Streamable<T> query(@NonNull TypeDescriptor entityType, @NonNull T entity) {
		Streamable<PropertyAccessor> columns = jdbcMapper.getColumns(entityType, entity);
		Sql sql = sqlDialect.toQuerySql(entityType, columns);
		return query(entityType, sql);
	}

	public final <T> Streamable<T> queryAll(@NonNull Class<T> entityType) {
		return queryAll(TypeDescriptor.valueOf(entityType));
	}

	public <T> Streamable<T> queryAll(@NonNull TypeDescriptor entityType) {
		Streamable<PropertyDescriptor> columns = jdbcMapper.getColumns(entityType);
		Sql sql = sqlDialect.toQueryAllSql(entityType, columns);
		return query(entityType, sql);
	}

	public final <T> Stage<Integer> update(@NonNull Class<T> entityType, @NonNull T entity) {
		return update(TypeDescriptor.valueOf(entityType), entity);
	}

	public <T> Stage<Integer> update(@NonNull TypeDescriptor entityType, @NonNull T entity) {
		Streamable<PropertyAccessor> columns = jdbcMapper.getColumns(entityType, entity);
		Sql sql = sqlDialect.toUpdateSql(entityType, columns);
		return connectionFactory.executeUpdate(sql);
	}
}
