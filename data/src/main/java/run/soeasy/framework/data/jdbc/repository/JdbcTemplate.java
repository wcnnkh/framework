package run.soeasy.framework.data.jdbc.repository;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.exchange.Trace;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.data.jdbc.ConnectionFactory;
import run.soeasy.framework.data.jdbc.Sql;
import run.soeasy.framework.data.jdbc.convert.JdbcMapper;
import run.soeasy.framework.data.jdbc.dialect.CommonSqlDialect;
import run.soeasy.framework.data.jdbc.dialect.SqlDialect;
import run.soeasy.framework.data.orm.EntityDescriptor;

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

	public final <T> Signal delete(@NonNull Class<T> entityClass, @NonNull T entity) {
		return delete(TypeDescriptor.valueOf(entityClass), entity);
	}

	public <T> Signal delete(@NonNull TypeDescriptor typeDescriptor, @NonNull T entity) {
		EntityDescriptor<PropertyAccessor> entityDescriptor = jdbcMapper.getEntityDescriptor(typeDescriptor, entity);
		Sql sql = sqlDialect.toDeleteSql(entityDescriptor);
		return connectionFactory.executeUpdate(sql);
	}

	public final <T> Trace<Integer> insert(@NonNull Class<T> entityClass, @NonNull T entity) {
		return insert(TypeDescriptor.valueOf(entityClass), entity);
	}

	public <T> Trace<Integer> insert(@NonNull TypeDescriptor typeDescriptor, @NonNull T entity) {
		EntityDescriptor<PropertyAccessor> entityDescriptor = jdbcMapper.getEntityDescriptor(typeDescriptor, entity);
		Sql sql = sqlDialect.toInsertSql(entityDescriptor);
		return connectionFactory.executeUpdate(sql);
	}

	public final <T> Streamable<T> query(@NonNull Class<T> entityClass, @NonNull Sql sql) {
		return query(TypeDescriptor.valueOf(entityClass), sql);
	}

	public final <T> Streamable<T> query(@NonNull Class<T> entityClass, @NonNull String statement,
			@NonNull Object... params) {
		return query(entityClass, new Sql(statement, params));
	}

	public final <T> Streamable<T> query(@NonNull Class<T> entityClass, @NonNull T entity) {
		return query(TypeDescriptor.valueOf(entityClass), entity);
	}

	@SuppressWarnings("unchecked")
	public <T> Streamable<T> query(@NonNull TypeDescriptor typeDescriptor, @NonNull Sql sql) {
		return connectionFactory.query(sql, (resultSet) -> (T) jdbcMapper.convert(resultSet, typeDescriptor));
	}

	public final <T> Streamable<T> query(@NonNull TypeDescriptor typeDescriptor, @NonNull String statement,
			@NonNull Object... params) {
		return query(typeDescriptor, new Sql(statement, params));
	}

	public <T> Streamable<T> query(@NonNull TypeDescriptor typeDescriptor, @NonNull T entity) {
		EntityDescriptor<PropertyAccessor> entityDescriptor = jdbcMapper.getEntityDescriptor(typeDescriptor, entity);
		Sql sql = sqlDialect.toQuerySql(entityDescriptor);
		return query(typeDescriptor, sql);
	}

	public final <T> Streamable<T> queryAll(@NonNull Class<T> entityClass) {
		return queryAll(TypeDescriptor.valueOf(entityClass));
	}

	public <T> Streamable<T> queryAll(@NonNull TypeDescriptor typeDescriptor) {
		EntityDescriptor<PropertyDescriptor> entityDescriptor = jdbcMapper.getEntityDescriptor(typeDescriptor);
		Sql sql = sqlDialect.toQueryAllSql(entityDescriptor);
		return query(typeDescriptor, sql);
	}

	public final <T> Trace<Integer> update(@NonNull Class<T> entityClass, @NonNull T entity) {
		return update(TypeDescriptor.valueOf(entityClass), entity);
	}

	public <T> Trace<Integer> update(@NonNull TypeDescriptor typeDescriptor, @NonNull T entity) {
		EntityDescriptor<PropertyAccessor> entityDescriptor = jdbcMapper.getEntityDescriptor(typeDescriptor, entity);
		Sql sql = sqlDialect.toUpdateSql(entityDescriptor);
		return connectionFactory.executeUpdate(sql);
	}
}
