package io.basc.framework.execution.reflect;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.AbstractExecutors;
import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public final class ConstructorExecutors extends AbstractExecutors {
	private final TypeDescriptor typeDescriptor;
	private volatile Elements<? extends ConstructorExecutor> elements;

	@Override
	public Elements<? extends Executor> getElements() {
		if (elements == null) {
			synchronized (this) {
				if (elements == null) {
					List<ConstructorExecutor> constructorExecutors = new ArrayList<>();
					for (Constructor<?> constructor : ReflectionUtils.getConstructors(typeDescriptor.getType())) {
						constructorExecutors.add(new ConstructorExecutor(constructor));
					}
					this.elements = Elements.forArray(constructorExecutors.toArray(new ConstructorExecutor[0]));
				}
			}
		}
		return elements;
	}
}
