package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import scw.core.parameter.ParameterDescriptors;
import scw.core.reflect.MethodInvoker;
import scw.http.server.HttpControllerDescriptor;

public interface Action extends MethodInvoker, AnnotatedElement {
	ParameterDescriptors getParameterDescriptors();

	Collection<HttpControllerDescriptor> getHttpControllerDescriptors();

	Collection<HttpControllerDescriptor> getSourceClassHttpControllerDescriptors();

	Collection<HttpControllerDescriptor> getMethodHttpControllerDescriptors();

	Iterable<ActionInterceptor> getActionInterceptors();
}
