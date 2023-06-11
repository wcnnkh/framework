package io.basc.framework.execution;

import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
final class ExecutionInterceptorChain implements Executable {
	private final Executables context;
	private final Executable executable;
	private final Iterator<? extends ExecutionInterceptor> iterator;

	public Object execute(Elements<? extends Object> args) throws Throwable {
		if (iterator.hasNext()) {
			return iterator.next().intercept(context, executable, args);
		}
		return executable.execute(args);
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return executable.getTypeDescriptor();
	}

	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		return executable.getParameterDescriptors();
	}

	@Override
	public String getName() {
		return executable.getName();
	}

}
