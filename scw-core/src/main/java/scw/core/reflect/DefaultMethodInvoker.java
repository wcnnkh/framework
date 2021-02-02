package scw.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.instance.NoArgsInstanceFactory;
import scw.instance.supplier.NameInstanceSupplier;
import scw.lang.NestedExceptionUtils;
import scw.util.Supplier;

public class DefaultMethodInvoker implements MethodInvoker, Serializable, Cloneable, Supplier<Object> {
	private static final long serialVersionUID = 1L;
	private final Class<?> sourceClass;
	private final MethodHolder methodHolder;
	private Supplier<?> instanceSupplier;
	private volatile Object instance;
	
	public DefaultMethodInvoker(Object instance, Class<?> sourceClass, Method method) {
		this(instance, sourceClass, method, false);
	}

	public DefaultMethodInvoker(NoArgsInstanceFactory instanceFactory, String instanceName, Class<?> sourceClass,
			Method method) {
		this(Modifier.isStatic(method.getModifiers()) ? null
				: new NameInstanceSupplier<Object>(instanceFactory, instanceName), sourceClass, new SimpleMethodHolder(method));
	}

	public DefaultMethodInvoker(Object instance, Class<?> sourceClass, Method method, boolean serialzerable) {
		this(instance, sourceClass, serialzerable ? new SerializableMethod(method) : new SimpleMethodHolder(method));
	}
	
	public DefaultMethodInvoker(Supplier<?> instanceSupplier, Class<?> sourceClass, Method method,
			boolean serialzerable) {
		this(instanceSupplier, sourceClass,
				serialzerable ? new SerializableMethod(method) : new SimpleMethodHolder(method));
	}
	
	public DefaultMethodInvoker(Object instance, Class<?> sourceClass, MethodHolder methodHolder) {
		this.sourceClass = sourceClass;
		this.methodHolder = methodHolder;
		this.instance = instance;
	}
	
	public DefaultMethodInvoker(Supplier<?> instanceSupplier, Class<?> sourceClass, MethodHolder methodHolder) {
		this.sourceClass = sourceClass;
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
	
	public Class<?> getSourceClass() {
		return sourceClass == null ? getMethod().getDeclaringClass() : sourceClass;
	}
	
	public Object invoke(Object... args) throws Throwable {
		Method method = getMethod();
		ReflectionUtils.makeAccessible(method);
		try {
			return method.invoke(Modifier.isStatic(method.getModifiers()) ? null : getInstance(), args);
		} catch (Throwable e) {
			throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
		}
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
	
	@Override
	public DefaultMethodInvoker clone() {
		return new DefaultMethodInvoker(this, sourceClass, methodHolder);
	}
}
