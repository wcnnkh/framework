package io.basc.framework.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.execution.MergedParameterDescriptor;
import io.basc.framework.execution.test.Executor;
import io.basc.framework.web.pattern.HttpPattern;

public interface Action extends MethodInvoker, AnnotatedElement {
	MergedParameterDescriptor getParameterDescriptors();

	Collection<HttpPattern> getPatternts();

	Iterable<ActionInterceptor> getActionInterceptors();
	
	TypeDescriptor getReturnType();
}
