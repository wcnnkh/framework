package io.basc.framework.core.reflect;

import io.basc.framework.instance.NoArgsInstanceFactory;
import io.basc.framework.instance.supplier.NameInstanceSupplier;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class DefaultMethodInvoker implements MethodInvoker, Serializable, Cloneable, Supplier<Object> {
	private static final long serialVersionUID = 1L;
	private final MethodHolder methodHolder;
	private Supplier<?> instanceSupplier;
	private volatile Object instance;
	
	public DefaultMethodInvoker(Object instance, Class<?> sourceClass, Method method) {
		this(instance, sourceClass, method, false);
	}

	public DefaultMethodInvoker(NoArgsInstanceFactory instanceFactory, String instanceName, Class<?> sourceClass,
			Method method) {
		this(Modifier.isStatic(method.getModifiers()) ? null
				: new NameInstanceSupplier<Object>(instanceFactory, instanceName), new SimpleMethodHolder(sourceClass, method));
	}

	public DefaultMethodInvoker(Object instance, Class<?> sourceClass, Method method, boolean serialzerable) {
		this(instance, serialzerable ? new SerializableMethod(method) : new SimpleMethodHolder(sourceClass, method));
	}
	
	public DefaultMethodInvoker(Supplier<?> instanceSupplier, Class<?> sourceClass, Method method,
			boolean serialzerable) {
		this(instanceSupplier,
				serialzerable ? new SerializableMethod(method) : new SimpleMethodHolder(sourceClass, method));
	}
	
	public DefaultMethodInvoker(Object instance, MethodHolder methodHolder) {
		this.methodHolder = methodHolder;
		this.instance = instance;
	}
	
	public DefaultMethodInvoker(Supplier<?> instanceSupplier, MethodHolder methodHolder) {
		this.methodHolder = methodHolder;
		this.instanceSupplier = instanceSupplier;
	}
	
	private final AtomicBoolean get = new AtomicBoolean();
	public Object get() {
		if(instance == null && instanceSupplier != null && !get.get()){
			synchronized (this) {
				if(instance == null){
					if(get.compareAndSet(false, true)){
						instance = instanceSupplier.get();
					}
				}
			}
		}
		return instance;
	}
	
	public Object getInstance() {
		return get();
	}

	public Method getMethod() {
		return methodHolder.getMethod();
	}
	
	public Class<?> getDeclaringClass() {
		return methodHolder.getDeclaringClass();
	}
	
	public Object invoke(Object... args) throws Throwable {
		Method method = getMethod();
		ReflectionUtils.makeAccessible(method);
		return ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers()) ? null : getInstance(), args);
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
	
	@Override
	public DefaultMethodInvoker clone() {
		return new DefaultMethodInvoker(this, methodHolder);
	}
}
