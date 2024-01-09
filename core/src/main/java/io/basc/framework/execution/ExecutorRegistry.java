package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.observe.register.ServiceRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class ExecutorRegistry<T extends Executor> extends ServiceRegistry<T> implements Executors<T> {
	@NonNull
	private final TypeDescriptor returnTypeDescriptor;
}
