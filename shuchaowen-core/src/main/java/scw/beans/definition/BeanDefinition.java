package scw.beans.definition;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import scw.beans.definition.builder.BeanBuilder;

public interface BeanDefinition extends BeanBuilder {
	String getId();

	Collection<String> getNames();

	boolean isSingleton();

	AnnotatedElement getAnnotatedElement();
}
