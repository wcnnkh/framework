package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;

import scw.mvc.Channel;
import scw.mvc.action.filter.ActionFilterChain;
import scw.net.Restful;
import scw.net.http.HttpMethod;

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
	
	Collection<ControllerDescriptor> getControllerDescriptors();

	Collection<ControllerDescriptor> getTargetClassControllerDescriptors();

	Collection<ControllerDescriptor> getMethodControllerDescriptors();

	public static final class ControllerDescriptor {
		private final String controller;
		private final HttpMethod httpMethod;
		private final Restful restful;

		public ControllerDescriptor(String controller, HttpMethod httpMethod) {
			this.restful = new Restful(controller);
			this.httpMethod = httpMethod;
			this.controller = controller;
		}

		public String getController() {
			return controller;
		}

		public HttpMethod getHttpMethod() {
			return httpMethod;
		}

		public Restful getRestful() {
			return restful;
		}

		@Override
		public int hashCode() {
			return controller.hashCode() + httpMethod.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj == this) {
				return true;
			}

			if (obj instanceof ControllerDescriptor) {
				ControllerDescriptor descriptor = (ControllerDescriptor) obj;
				return descriptor.getController().equals(this.getController())
						&& descriptor.getHttpMethod().equals(
								this.getHttpMethod());
			}
			return false;
		}
	}
}
