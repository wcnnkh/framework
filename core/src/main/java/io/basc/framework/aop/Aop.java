package io.basc.framework.aop;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public interface Aop extends AopPolicy, ProxyFactory{
	MethodInterceptor getMethodInterceptor();
	
	Proxy getProxy(Class<?> clazz);
	
	Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, MethodInterceptor methodInterceptor);
	
	<T> Proxy getProxy(Class<? extends T> clazz, Supplier<? extends T> instanceSupplier, Class<?>[] interfaces, @Nullable MethodInterceptor methodInterceptor);
	
	<T> Proxy getProxy(Class<? extends T> clazz, Supplier<? extends T> instanceSupplier);
	
	<T> Proxy getProxy(Class<? extends T> clazz, T instance, Class<?>[] interfaces, @Nullable MethodInterceptor methodInterceptor);
	
	<T> Proxy getProxy(Class<? extends T> clazz, T instance);
	
	<T> MethodInvoker getProxyMethod(Class<? extends T> targetClass, Supplier<? extends T> instanceSupplier, Method method, @Nullable MethodInterceptor methodInterceptor);
	
	<T> MethodInvoker getProxyMethod(Class<? extends T> targetClass, Supplier<? extends T> instanceSupplier, Method method);
	
	<T> MethodInvoker getProxyMethod(Class<? extends T> targetClass, T instance, Method method, @Nullable MethodInterceptor methodInterceptor);
	
	<T> MethodInvoker getProxyMethod(Class<? extends T> targetClass, T instance, Method method);
	
	default boolean isProxy(Object instance){
		if(instance == null){
			return false;
		}
		
		return isProxy(instance.getClass());
	}
}