package io.basc.framework.execution.aop.jdk;

import java.lang.reflect.InvocationHandler;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.AbstractExecutable;
import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 可执行的代理
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JdkProxyExecutable extends AbstractExecutable {
	private final TypeDescriptor typeDescriptor;
	private final Class<?>[] interfaces;
	private final InvocationHandler invocationHandler;

	public JdkProxyExecutable(TypeDescriptor typeDescriptor, Class<?>[] interfaces, ExecutionInterceptor interceptor) {
		this.typeDescriptor = typeDescriptor;
		this.interfaces = interfaces;
		this.invocationHandler = new ExecutionInterceptorToInvocationHandler(this, interceptor);
	}

	@Override
	public boolean isExecutable(Elements<? extends TypeDescriptor> types) {
		return types.isEmpty();
	}

	@Override
	public Object execute(Elements<? extends Value> args) {
		if (!args.isEmpty()) {
			throw new UnsupportedOperationException(getTypeDescriptor().toString());
		}
		return java.lang.reflect.Proxy.newProxyInstance(typeDescriptor.getType().getClassLoader(),
				interfaces == null ? new Class<?>[0] : interfaces, invocationHandler);
	}
}
