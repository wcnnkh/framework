package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EmptyExecutor implements Executor {
	private final String name;
	private final TypeDescriptor typeDescriptor;

	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		return Elements.empty();
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws ExecutionException, UnsupportedException {
		throw new CannotExecuteException();
	}

}
