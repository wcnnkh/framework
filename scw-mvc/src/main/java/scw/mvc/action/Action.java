package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import scw.core.parameter.ParameterDescriptors;
import scw.core.reflect.MethodInvoker;
import scw.http.server.pattern.HttpPattern;

public interface Action extends MethodInvoker, AnnotatedElement {
	ParameterDescriptors getParameterDescriptors();
	
	Collection<HttpPattern> getPatternts();

	Iterable<ActionInterceptor> getActionInterceptors();
}
