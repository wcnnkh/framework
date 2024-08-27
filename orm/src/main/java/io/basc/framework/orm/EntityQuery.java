package io.basc.framework.orm;

import java.util.Map;

import io.basc.framework.data.domain.Query;
import io.basc.framework.util.Elements;

public class EntityQuery<E> extends Query<E> {
	private static final long serialVersionUID = 1L;
	private final EntityRepository<?> repository;
	private final EntityKeyGenerator entityKeyGenerator;
	private final Query<E> source;

	public EntityQuery(Query<E> source, EntityRepository<?> repository, EntityKeyGenerator entityKeyGenerator) {
		super(source);
		this.source = source;
		this.repository = repository;
		this.entityKeyGenerator = entityKeyGenerator;
	}

	// TODO 重写其他方法

	public Query<E> getSource() {
		return source;
	}

	public EntityRepository<?> getRepository() {
		return repository;
	}

	public EntityKeyGenerator getEntityKeyGenerator() {
		return entityKeyGenerator;
	}

	public <K> Map<K, E> toMap(Elements<? extends K> inPrimaryKeys, Object... primaryKeys) {
		Map<String, K> keyMap = entityKeyGenerator.getEntityKeyMap(repository, inPrimaryKeys.iterator(), primaryKeys);
		return getElements().toMap((e) -> {
			String key = entityKeyGenerator.getEntityKey(repository, e);
			return keyMap.get(key);
		});
	}
}
