package io.basc.framework.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import io.basc.framework.beans.factory.InstanceFactory;
import io.basc.framework.beans.factory.NameInstanceSupplier;

public class DefaultMethodInvoker implements MethodInvoker, Serializable, Cloneable, Supplier<Object> {
	private static final long serialVersionUID = 1L;
	private final MethodHolder methodHolder;
	private Supplier<?> instanceSupplier;
	private volatile Object instance;
	private final Class<?> sourceClass;

	public DefaultMethodInvoker(Object instance, Class<?> sourceClass, Method method) {
		this(instance, sourceClass, method, false);
	}

	public DefaultMethodInvoker(InstanceFactory instanceFactory, String instanceName, Class<?> sourceClass,
			Method method) {
		this(sourceClass, Modifier.isStatic(method.getModifiers()) ? null
				: new NameInstanceSupplier<Object>(instanceFactory, instanceName), () -> method);
	}

	public DefaultMethodInvoker(Object instance, Class<?> sourceClass, Method method, boolean serialzerable) {
		this(sourceClass, instance, serialzerable ? new SerializableMethod(method) : () -> method);
	}

	public DefaultMethodInvoker(Supplier<?> instanceSupplier, Class<?> sourceClass, Method method,
			boolean serialzerable) {
		this(sourceClass, instanceSupplier, serialzerable ? new SerializableMethod(method) : () -> method);
	}

	public DefaultMethodInvoker(Class<?> sourceClass, Object instance, MethodHolder methodHolder) {
		this.sourceClass = sourceClass;
		this.methodHolder = methodHolder;
		this.instance = instance;
	}

	public DefaultMethodInvoker(Class<?> sourceClass, Supplier<?> instanceSupplier, MethodHolder methodHolder) {
		this.sourceClass = sourceClass;
		this.methodHolder = methodHolder;
		this.instanceSupplier = instanceSupplier;
	}

	private final AtomicBoolean get = new AtomicBoolean();

	public Object get() {
		if (instance == null && instanceSupplier != null && !get.get()) {
			synchronized (this) {
				if (instance == null) {
					if (get.compareAndSet(false, true)) {
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

	public Class<?> getSourceClass() {
		return this.sourceClass;
	}

	public Object invoke(Object... args) throws Throwable {
		Method method = getMethod();
		ReflectionUtils.makeAccessible(method);
		return ReflectionUtils.invoke(method, Modifier.isStatic(method.getModifiers()) ? null : getInstance(), args);
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}

	@Override
	public DefaultMethodInvoker clone() {
		return new DefaultMethodInvoker(this.sourceClass, this, methodHolder);
	}
}
