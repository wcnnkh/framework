package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import scw.mvc.Channel;
import scw.mvc.action.filter.ActionFilterChain;

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
	
	Object doAction(Channel channel) throws Throwable;

	/**
	 * 这个action的filter chain
	 * @return 可能为空
	 */
	ActionFilterChain getActionFilterChain();
}
