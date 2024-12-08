package io.basc.framework.core.execution.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Executor;
import io.basc.framework.observe.service.ObservableServiceLoader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class ExecutorRegistry<T extends Executor> extends ObservableServiceLoader<T> implements Executors<T> {
	@NonNull
	private final TypeDescriptor returnTypeDescriptor;
}
