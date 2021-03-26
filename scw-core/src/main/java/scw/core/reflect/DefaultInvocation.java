package scw.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.instance.NoArgsInstanceFactory;
import scw.lang.NestedExceptionUtils;
import scw.util.Supplier;

public class DefaultInvocation extends DefaultMethodInvoker implements
		Invocation, Serializable {
	private static final long serialVersionUID = 1L;
	private final Object[] args;

	public DefaultInvocation(Object instance, Class<?> sourceClass,
			Method method, Object[] args) {
		super(instance, sourceClass, method);
		this.args = args;
	}

	public DefaultInvocation(NoArgsInstanceFactory instanceFactory,
			String instanceName, Class<?> sourceClass, Method method,
			Object[] args) {
		super(instanceFactory, instanceName, sourceClass, method);
		this.args = args;
	}

	public DefaultInvocation(Object instance, Class<?> sourceClass,
			Method method, boolean serialzerable, Object[] args) {
		super(instance, sourceClass, method, serialzerable);
		this.args = args;
	}

	public DefaultInvocation(Supplier<?> instanceSupplier,
			Class<?> sourceClass, Method method, boolean serialzerable,
			Object[] args) {
		super(instanceSupplier, sourceClass, method, serialzerable);
		this.args = args;
	}

	public DefaultInvocation(Object instance, MethodHolder methodHolder,
			Object[] args) {
		super(instance, methodHolder);
		this.args = args;
	}

	public DefaultInvocation(Supplier<?> instanceSupplier,
			MethodHolder methodHolder, Object[] args) {
		super(instanceSupplier, methodHolder);
		this.args = args;
	}

	public Object[] getArgs() {
		return args.clone();
	}

	public Object call() throws Exception {
		try {
			return invoke(args);
		} catch (Throwable e) {
			ReflectionUtils.handleThrowable(e);
		}
		throw NestedExceptionUtils.shouldNeverGetHere();
	}
}
