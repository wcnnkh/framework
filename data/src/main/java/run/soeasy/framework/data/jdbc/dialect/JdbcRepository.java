package run.soeasy.framework.data.jdbc.dialect;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.type.ResolvableType;
import run.soeasy.framework.data.Repository;
import run.soeasy.framework.data.jdbc.ConnectionFactory;
import run.soeasy.framework.data.jdbc.Sql;
import run.soeasy.framework.data.jdbc.convert.JdbcMapper;

@Getter
@Setter
@RequiredArgsConstructor
public class JdbcRepository<T> extends JdbcMapper implements Repository<T> {
	@NonNull
	private SqlDialect sqlDialect = new CommonSqlDialect();

	@NonNull
	private TypeDescriptor entityDescriptor = TypeDescriptor
			.forType(ResolvableType.forType(getClass()).as(JdbcRepository.class).getActualTypeArgument(0));
	private final ConnectionFactory connectionFactory;

	@Override
	public Stream<T> stream() {
		Streamable<PropertyDescriptor> columns = getColumns(getEntityDescriptor());
		Sql sql = sqlDialect.toQueryAllSql(entityDescriptor, columns);
		return query(sql).stream();
	}

	private Streamable<T> query(Sql sql) {
		return Streamable.empty();
	}

	@Override
	public Signal delete(T entity) {
		Streamable<PropertyAccessor> columns = getProperties(entity, entityDescriptor);
		Sql sql = sqlDialect.toDeleteSql(entityDescriptor, columns);
		return Signal.call(() -> connectionFactory.newPipeline().prepareStatement(sql.getStatement())
				.setParams(sql.getArgs()).executeUpdate());
	}

	@Override
	public Signal update(T entity) {
		Streamable<PropertyAccessor> columns = getProperties(entity, entityDescriptor);
		Sql sql = sqlDialect.toUpdateSql(entityDescriptor, columns);
		return executeUpdate(sql);
	}

	private Signal executeUpdate(Sql sql) {
		return Signal.call(() -> {
			int rows = connectionFactory.newPipeline().prepareStatement(sql.getStatement()).setParams(sql.getArgs())
					.executeUpdate();
			if (rows <= 0) {
				throw new IllegalAccessError("No changes have occurred");
			}
			return rows;
		});
	}

	@Override
	public Signal insert(T entity) {
		Streamable<PropertyAccessor> columns = getProperties(entity, entityDescriptor);
		Sql sql = sqlDialect.toUpdateSql(entityDescriptor, columns);
		return executeUpdate(sql);
	}

	@Override
	public Streamable<T> query(T entity) {
		Streamable<PropertyAccessor> columns = getProperties(entity, entityDescriptor);
		Sql sql = sqlDialect.toQuerySql(entityDescriptor, columns);
		return query(sql);
	}

}
