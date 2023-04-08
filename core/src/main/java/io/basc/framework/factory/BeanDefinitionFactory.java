package io.basc.framework.factory;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Stream;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.alias.AliasFactory;

public interface BeanDefinitionFactory extends AliasFactory {
	BeanDefinition getDefinition(String name);

	BeanDefinition getDefinition(Class<?> clazz);

	/**
	 * Check if this registry contains a bean definition with the given name.
	 * 
	 * @param beanName the name of the bean to look for
	 * @return if this registry contains a bean definition with the given name
	 */
	boolean containsDefinition(String beanName);

	String[] getDefinitionIds();

	default Stream<BeanDefinition> matchType(ResolvableType type) {
		String[] names = getDefinitionIds();
		if (names == null || names.length == 0) {
			return Stream.empty();
		}

		return Arrays.asList(names).stream().map((name) -> getDefinition(name))
				.filter((definition) -> definition != null
						&& definition.getTypeDescriptor().getResolvableType().isAssignableFrom(type));
	}

	default Stream<BeanDefinition> matchType(Type type) {
		return matchType(ResolvableType.forType(type));
	}
}
