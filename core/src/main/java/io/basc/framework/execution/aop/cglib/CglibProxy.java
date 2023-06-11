package io.basc.framework.execution.aop.cglib;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executables;
import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.util.Elements;
import lombok.Data;
import net.sf.cglib.proxy.Enhancer;

@Data
public class CglibProxy implements Proxy, Executables {
	private final TypeDescriptor source;
	private final Enhancer enhancer;
	private volatile Elements<? extends CglibEnhanceExecutor> members;

	public CglibProxy(TypeDescriptor source, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
		this.source = source;
		this.enhancer = CglibUtils.createEnhancer(source.getType(), interfaces);
		this.enhancer.setCallback(new ExecutionInterceptorToMethodInterceptor(this, executionInterceptor));
	}

	@Override
	public TypeDescriptor getSource() {
		return source;
	}

	@Override
	public Elements<? extends CglibEnhanceExecutor> getMembers() {
		if (members == null) {
			synchronized (this) {
				if (members == null) {
					this.members = ReflectionUtils.getConstructors(source.getType()).map((constructor) -> {
						return new CglibEnhanceExecutor(source, enhancer, ParameterUtils.getParameters(constructor));
					}).toList();
				}
			}
		}
		return members;
	}
}