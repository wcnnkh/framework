package io.basc.framework.mvc.action;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.web.pattern.HttpPattern;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

public interface Action extends MethodInvoker, AnnotatedElement {
	ParameterDescriptors getParameterDescriptors();

	Collection<HttpPattern> getPatternts();

	Iterable<ActionInterceptor> getActionInterceptors();

	TypeDescriptor getReturnType();
}
