package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import scw.beans.builder.BeanBuilder;

public interface BeanDefinition extends BeanBuilder {
	String getId();

	Collection<String> getNames();

	boolean isSingleton();

	AnnotatedElement getAnnotatedElement();
}
