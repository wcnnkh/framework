package io.basc.framework.execution.aop.jdk;

import java.lang.reflect.InvocationHandler;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executable;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JdkProxyExecutor implements Executable {
	private final TypeDescriptor typeDescriptor;
	private final Class<?>[] interfaces;
	private final InvocationHandler invocationHandler;

	@Override
	public String getName() {
		return typeDescriptor.getName();
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	/**
	 * jdk只能代理接口，所以没有构造参数
	 */
	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		return Elements.empty();
	}

	@Override
	public Object execute(Elements<? extends Object> args) {
		if (!args.isEmpty()) {
			throw new UnsupportedException(typeDescriptor.toString());
		}

		return java.lang.reflect.Proxy.newProxyInstance(typeDescriptor.getType().getClassLoader(),
				interfaces == null ? new Class<?>[0] : interfaces, invocationHandler);
	}

}
