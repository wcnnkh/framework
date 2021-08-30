package io.basc.framework.factory;

public interface DefinitionFactory {
	InstanceDefinition getDefinition(String name);

	InstanceDefinition getDefinition(Class<?> clazz);
}
