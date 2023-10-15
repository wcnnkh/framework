package io.basc.framework.execution.reflect;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Method;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReflectionMethod extends ReflectionExecutable<java.lang.reflect.Method> implements Method {
	private Aop aop;
	private Class<?>[] aopInterfaces;
	private ExecutionInterceptor executionInterceptor;
	private volatile String name;
	private final TypeDescriptor targetTypeDescriptor;

	public ReflectionMethod(java.lang.reflect.Method executable, TypeDescriptor targetTypeDescriptor) {
		super(executable);
		this.targetTypeDescriptor = targetTypeDescriptor;
	}

	@Override
	public Object execute(Object target, Elements<? extends Object> args) throws Throwable {
		Object value = ReflectionUtils.invoke(getExecutable(), target, args.toArray());
		if (aop == null) {
			return value;
		}
		Proxy proxy = aop.getProxy(getExecutable().getReturnType(), value);
		return proxy.execute();
	}

	@Override
	public String getName() {
		if (name == null) {
			synchronized (this) {
				if (name == null) {
					this.name = getExecutable().getName();
				}
			}
		}
		return name;
	}
}
