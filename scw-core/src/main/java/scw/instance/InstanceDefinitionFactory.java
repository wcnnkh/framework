package scw.instance;

public interface InstanceDefinitionFactory {
	InstanceDefinition getDefinition(String name);

	InstanceDefinition getDefinition(Class<?> clazz);
}
