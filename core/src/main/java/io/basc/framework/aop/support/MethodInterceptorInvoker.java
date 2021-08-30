package io.basc.framework.aop.support;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.reflect.MethodInvoker;
import io.basc.framework.reflect.MethodInvokerWrapper;

public class MethodInterceptorInvoker extends MethodInvokerWrapper{
	private static final long serialVersionUID = 1L;
	private final MethodInterceptor methodInterceptor;
	
	public MethodInterceptorInvoker(MethodInvoker methodInvoker, MethodInterceptor methodInterceptor){
		super(methodInvoker);
		this.methodInterceptor = methodInterceptor;
	}
	
	@Override
	public Object invoke(Object... args) throws Throwable {
		if(methodInterceptor == null){
			return super.invoke(args);
		}
		
		return methodInterceptor.intercept(getSource(), args);
	}
}
