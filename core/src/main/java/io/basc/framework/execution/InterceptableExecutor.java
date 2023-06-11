package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 可被拦截的执行器
 * 
 * @author wcnnkh
 *
 */
@RequiredArgsConstructor
@Getter
public class InterceptableExecutor implements Executable {
	private final Executables source;
	private final Executable executable;
	private final Iterable<? extends ExecutionInterceptor> interceptors;

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return executable.getTypeDescriptor();
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(source, this.executable,
				this.interceptors.iterator());
		return chain.execute(args);
	}

	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		return this.executable.getParameterDescriptors();
	}

	@Override
	public String getName() {
		return executable.getName();
	}
}
