package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DefaultExecutors implements Executable {
	private final TypeDescriptor source;
	private Elements<? extends Executor> executors;
}
