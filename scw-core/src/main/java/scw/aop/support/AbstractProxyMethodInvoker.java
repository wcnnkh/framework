package scw.aop.support;

import java.lang.reflect.Method;

import scw.aop.MethodInterceptor;
import scw.aop.ProxyInstanceTarget;
import scw.core.reflect.DefaultMethodInvoker;
import scw.core.reflect.MethodInvoker;
import scw.core.utils.ArrayUtils;
import scw.util.Supplier;

public abstract class AbstractProxyMethodInvoker extends DefaultMethodInvoker implements MethodInterceptor{
	private static final long serialVersionUID = 1L;
	
	public AbstractProxyMethodInvoker(Supplier<?> instanceSupplier,
			Class<?> sourceClass, Method method) {
		super(instanceSupplier, sourceClass, method, false);
	}
	
	public Object intercept(MethodInvoker invoker, Object[] args)
			throws Throwable {
		if(ArrayUtils.isEmpty(args) && ProxyInstanceTarget.class.isAssignableFrom(getDeclaringClass()) && invoker.getMethod().getName().equals(ProxyInstanceTarget.PROXY_TARGET_METHOD_NAME)){
			return getInstance();
		}
		
		MethodInterceptor methodInterceptor = getMethodInterceptor(invoker);
		if(methodInterceptor == null){
			return invoker.invoke(args);
		}
		
		return methodInterceptor.intercept(invoker, args);
	}
	
	protected abstract MethodInterceptor getMethodInterceptor(MethodInvoker invoker);

	@Override
	public Object invoke(Object... args) throws Throwable {
		return intercept(clone(), args);
	}
}
