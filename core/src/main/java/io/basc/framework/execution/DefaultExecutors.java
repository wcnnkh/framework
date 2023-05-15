package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DefaultExecutors extends AbstractExecutors {
	private final TypeDescriptor typeDescriptor;
	private Elements<? extends Executor> elements;
}
