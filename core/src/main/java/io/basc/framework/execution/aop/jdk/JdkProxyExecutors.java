package io.basc.framework.execution.aop.jdk;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.execution.Executors;
import io.basc.framework.util.Elements;
import lombok.Data;

/**
 * 可执行的代理
 * 
 * @author wcnnkh
 *
 */
@Data
public class JdkProxyExecutors implements Executors {
	private final JdkProxyExecutor jdkProxyExecutor;

	public JdkProxyExecutors(TypeDescriptor typeDescriptor, Class<?>[] interfaces, ExecutionInterceptor interceptor) {
		this.jdkProxyExecutor = new JdkProxyExecutor(typeDescriptor, interfaces,
				new ExecutionInterceptorToInvocationHandler(this, interceptor));
	}

	@Override
	public Elements<? extends JdkProxyExecutor> getExecutors() {
		return Elements.singleton(jdkProxyExecutor);
	}

	@Override
	public TypeDescriptor getSource() {
		return jdkProxyExecutor.getTypeDescriptor();
	}
}
