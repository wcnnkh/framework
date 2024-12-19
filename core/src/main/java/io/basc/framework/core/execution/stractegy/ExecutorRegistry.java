package io.basc.framework.core.execution.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Executor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.spi.ServiceRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExecutorRegistry<T extends Executor> extends AbstractExecutors<T> {
	private final ServiceRegistry<T> registry = new ServiceRegistry<>();
	private final TypeDescriptor returnTypeDescriptor;

	@Override
	public Elements<T> getElements() {
		return registry;
	}

}
