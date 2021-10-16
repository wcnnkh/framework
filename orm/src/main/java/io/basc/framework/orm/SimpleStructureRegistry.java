package io.basc.framework.orm;

import io.basc.framework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleStructureRegistry<S extends EntityStructure<? extends Property>>
		implements StructureRegistry<S> {
	private Map<Class<?>, S> map = new ConcurrentHashMap<>();

	@Override
	public boolean isRegistry(Class<?> entityClass) {
		return map.containsKey(entityClass);
	}

	@Override
	public S getStructure(Class<?> entityClass) {
		return map.get(entityClass);
	}

	@Override
	public void register(Class<?> entityClass, S structure) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		Assert.requiredArgument(structure != null, "structure");
		map.put(entityClass, structure);
	}

}
