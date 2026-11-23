package run.soeasy.framework.data.jdbc.repository;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.data.Repository;

@Getter
@RequiredArgsConstructor
public class JdbcRepository<T> implements Repository<T> {
	@NonNull
	private final JdbcTemplate jdbcTemplate;
	@NonNull
	private final TypeDescriptor entityDescriptor;

	public JdbcRepository(@NonNull JdbcTemplate jdbcTemplate, @NonNull Class<T> entityClazz) {
		this(jdbcTemplate, TypeDescriptor.valueOf(entityClazz));
	}

	@Override
	public Signal delete(@NonNull T entity) {
		return jdbcTemplate.delete(entityDescriptor, entity);
	}

	@Override
	public Signal insert(@NonNull T entity) {
		return jdbcTemplate.insert(entityDescriptor, entity);
	}

	@Override
	public Streamable<T> query(@NonNull T entity) {
		return jdbcTemplate.query(entityDescriptor, entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Stream<T> stream() {
		return (Stream<T>) jdbcTemplate.queryAll(entityDescriptor).stream();
	}

	@Override
	public Signal update(@NonNull T entity) {
		return jdbcTemplate.update(entityDescriptor, entity);
	}
}
