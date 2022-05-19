package io.basc.framework.orm.support;

import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.StructureRegistry;
import io.basc.framework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleStructureRegistry<S extends EntityStructure<? extends Property>>
		implements StructureRegistry<S> {
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
