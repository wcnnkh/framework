package shuchaowen.core.http.server;

import java.lang.reflect.Constructor;

import shuchaowen.core.beans.BeanFactory;

public final class MethodParameter {
	private Class<?> type;
	private String name;
	
	private Constructor<Parameter> constructor;

	public MethodParameter(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public Object getParameter(BeanFactory beanFactory, Request request, Response response) throws Throwable {
		if(constructor != null){
			return beanFactory.getBeanInfo(type.getName()).newInstance(beanFactory, constructor, request);
		}
		
		if (Request.class.isAssignableFrom(type)) {
			return request;
		} else if (Response.class.isAssignableFrom(type)) {
			return response;
		} else if (Parameter.class.isAssignableFrom(type)) {
			Constructor<Parameter>[] constructors = (Constructor<Parameter>[]) type.getConstructors();
			for(Constructor<Parameter> constructor : constructors){
				if(constructor.getParameterCount() == 1 && Request.class.isAssignableFrom(constructor.getParameterTypes()[0])){
					this.constructor = constructor;
					constructor.setAccessible(true);
					return beanFactory.getBeanInfo(type.getName()).newInstance(beanFactory, constructor, request);
				}
			}
			return null;
		} else {
			return request.getParameter(type, name);
		}
	}
}
