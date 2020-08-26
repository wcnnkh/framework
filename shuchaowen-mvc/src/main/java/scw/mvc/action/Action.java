package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import scw.aop.MethodInvoker;
import scw.core.parameter.ParameterDescriptors;
import scw.http.server.HttpControllerDescriptor;

public interface Action extends MethodInvoker{
	/**
	 * 返回合成后的注解元素
	 * @return
	 */
	AnnotatedElement getAnnotatedElement();
	
	/**
	 * 返回Class上的注解元素
	 * @return
	 */
	AnnotatedElement getTargetClassAnnotatedElement();
	
	/**
	 * 返回方法上的注解元素
	 * @return
	 */
	AnnotatedElement getMethodAnnotatedElement();
	
	ParameterDescriptors getParameterDescriptors();
	
	Collection<HttpControllerDescriptor> getHttpControllerDescriptors();

	Collection<HttpControllerDescriptor> getTargetClassHttpControllerDescriptors();

	Collection<HttpControllerDescriptor> getMethodHttpControllerDescriptors();
	
	Iterable<? extends ActionInterceptor> getActionInterceptors();
}
