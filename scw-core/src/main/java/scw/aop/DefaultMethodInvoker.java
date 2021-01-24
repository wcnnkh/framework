package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.reflect.MethodHolder;
import scw.core.reflect.SerializableMethod;
import scw.core.reflect.SimpleMethodHolder;
import scw.instance.factory.NoArgsInstanceFactory;

public class DefaultMethodInvoker extends AbstractMethodInvoker implements Serializable {
	private static final long serialVersionUID = 1L;
	private final MethodHolder methodHolder;
	private final InstanceGetter instanceGetter;

	public DefaultMethodInvoker(Object instance, Class<?> sourceClass, Method method) {
		this(instance, sourceClass, method, false);
	}

	public DefaultMethodInvoker(NoArgsInstanceFactory instanceFactory, String instanceName, Class<?> sourceClass,
			Method method, boolean serialzerable) {
		this(Modifier.isStatic(method.getModifiers()) ? null
				: (instanceFactory.isInstance(instanceName)
						? new SimpleInstanceGetter(instanceFactory.getInstance(instanceName))
						: new InstanceNameGetter(instanceFactory, instanceName)),
				sourceClass, serialzerable ? new SerializableMethod(method) : new SimpleMethodHolder(method));
	}

	public DefaultMethodInvoker(Object instance, Class<?> sourceClass, Method method, boolean serialzerable) {
		this(instance == null ? null : new SimpleInstanceGetter(instance), sourceClass,
				serialzerable ? new SerializableMethod(method) : new SimpleMethodHolder(method));
	}

	public DefaultMethodInvoker(InstanceGetter instanceGetter, Class<?> sourceClass, Method method,
			boolean serialzerable) {
		this(instanceGetter, sourceClass,
				serialzerable ? new SerializableMethod(method) : new SimpleMethodHolder(method));
	}

	public DefaultMethodInvoker(InstanceGetter instanceGetter, Class<?> sourceClass, MethodHolder methodHolder) {
		super(sourceClass);
		this.methodHolder = methodHolder;
		this.instanceGetter = instanceGetter;
	}

	public Object getInstance() {
		return instanceGetter == null ? null : instanceGetter.get();
	}

	public Method getMethod() {
		return methodHolder.getMethod();
	}

	public interface InstanceGetter {
		Object get();
	}

	private static class InstanceNameGetter implements InstanceGetter, Serializable {
		private static final long serialVersionUID = 1L;
		private NoArgsInstanceFactory instanceFactory;
		private String name;

		public InstanceNameGetter(NoArgsInstanceFactory instanceFactory, String name) {
			this.instanceFactory = instanceFactory;
			this.name = name;
		}

		public Object get() {
			return instanceFactory.getInstance(name);
		}
	}

	private static class SimpleInstanceGetter implements InstanceGetter, Serializable {
		private static final long serialVersionUID = 1L;
		private final Object instance;

		public SimpleInstanceGetter(Object instance) {
			this.instance = instance;
		}

		public Object get() {
			return instance;
		}
	}
}
