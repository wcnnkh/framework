package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;

import scw.core.parameter.ParameterDescriptors;
import scw.http.server.HttpControllerDescriptor;
import scw.mvc.HttpChannel;

public interface Action{
	/**
	 * 返回合成后的注解元素
	 * @return
	 */
	AnnotatedElement getAnnotatedElement();
	
	Class<?> getTargetClass();
	
	/**
	 * 返回Class上的注解元素
	 * @return
	 */
	AnnotatedElement getTargetClassAnnotatedElement();
	
	Method getMethod();
	
	/**
	 * 返回方法上的注解元素
	 * @return
	 */
	AnnotatedElement getMethodAnnotatedElement();
	
	ParameterDescriptors getParameterDescriptors();
	
	Object doAction(HttpChannel httpChannel) throws Throwable;

	Collection<HttpControllerDescriptor> getHttpControllerDescriptors();

	Collection<HttpControllerDescriptor> getTargetClassHttpControllerDescriptors();

	Collection<HttpControllerDescriptor> getMethodHttpControllerDescriptors();
}
