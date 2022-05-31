package io.basc.framework.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.util.Assert;

public class SimpleStructureFactory<S extends Structure<? extends Field>> implements StructureFactory<S> {
	private Map<Class<?>, S> map = new ConcurrentHashMap<>();

	@Override
	public boolean isStructureRegistred(Class<?> entityClass) {
		return map.containsKey(entityClass);
	}

	@Override
	public S getStructure(Class<?> entityClass) {
		return map.get(entityClass);
	}

	@Override
	public void registerStructure(Class<?> entityClass, S structure) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		Assert.requiredArgument(structure != null, "structure");
		map.put(entityClass, structure);
	}
}
