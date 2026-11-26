package run.soeasy.framework.data.jdbc.repository;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exchange.Stage;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.data.Repository;

@Getter
@RequiredArgsConstructor
public class JdbcRepository<T> implements Repository<T> {
	@NonNull
	private final JdbcTemplate jdbcTemplate;
	@NonNull
	private final TypeDescriptor entityTypeDescriptor;

	public JdbcRepository(@NonNull JdbcTemplate jdbcTemplate, @NonNull Class<T> entityClass) {
		this(jdbcTemplate, TypeDescriptor.valueOf(entityClass));
	}

	@Override
	public Stage<Integer> delete(@NonNull T entity) {
		return jdbcTemplate.delete(entityTypeDescriptor, entity);
	}

	@Override
	public Stage<Integer> insert(@NonNull T entity) {
		return jdbcTemplate.insert(entityTypeDescriptor, entity);
	}

	@Override
	public Streamable<T> query(@NonNull T entity) {
		return jdbcTemplate.query(entityTypeDescriptor, entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Stream<T> stream() {
		return (Stream<T>) jdbcTemplate.queryAll(entityTypeDescriptor).stream();
	}

	@Override
	public Stage<Integer> update(@NonNull T entity) {
		return jdbcTemplate.update(entityTypeDescriptor, entity);
	}
}
