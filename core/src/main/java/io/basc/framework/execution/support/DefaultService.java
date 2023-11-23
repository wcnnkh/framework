package io.basc.framework.execution.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Service;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.NonNull;

@Data
public class DefaultService<E extends Executor> implements Service<E> {
	@NonNull
	private TypeDescriptor returnTypeDescriptor = TypeDescriptor.valueOf(Object.class);
	@NonNull
	private Elements<E> elements = Elements.empty();
	@NonNull
	private Elements<Parameter> parameters = Elements.empty();
}
