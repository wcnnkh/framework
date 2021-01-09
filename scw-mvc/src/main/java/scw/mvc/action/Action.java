package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import scw.core.parameter.ParameterDescriptors;
import scw.core.reflect.MethodInvoker;
import scw.http.server.HttpControllerDescriptor;

public interface Action extends MethodInvoker {
	/**
	 * 一般情况 下返回方法上的注解元素(获取注解内容推荐使用此方法，因为Method的getAnnotation存在锁)<br/>
	 * 这是一个无锁的方法
	 * 
	 * @return
	 */
	AnnotatedElement getAnnotatedElement();

	ParameterDescriptors getParameterDescriptors();

	Collection<HttpControllerDescriptor> getHttpControllerDescriptors();

	Collection<HttpControllerDescriptor> getSourceClassHttpControllerDescriptors();

	Collection<HttpControllerDescriptor> getMethodHttpControllerDescriptors();

	Iterable<? extends ActionInterceptor> getActionInterceptors();
}
