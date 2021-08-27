package io.basc.framework.instance;

public interface DefinitionFactory {
	InstanceDefinition getDefinition(String name);

	InstanceDefinition getDefinition(Class<?> clazz);
}
