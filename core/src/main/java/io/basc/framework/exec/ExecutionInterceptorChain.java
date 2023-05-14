package io.basc.framework.exec;

import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public class ExecutionInterceptorChain extends AbstractExecutor {
	private final Iterator<? extends ExecutionInterceptor> iterator;
	private final Executable source;
	private final Executor executor;

	public ExecutionInterceptorChain(Executable source, Iterator<? extends ExecutionInterceptor> iterator,
			Executor executor) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(executor != null, "executor");
		this.source = source;
		this.iterator = iterator;
		this.executor = executor;
	}

	@Override
	public boolean isExecutable(Elements<? extends TypeDescriptor> types) {
		return executor.isExecutable(types);
	}

	@Override
	public Object execute(Elements<? extends Value> args) throws ExecutionException {
		if (iterator.hasNext()) {
			return iterator.next().intercept(source, executor, args);
		}
		return executor.execute(args);
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return executor.getTypeDescriptor();
	}

	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		return executor.getParameterDescriptors();
	}

}
