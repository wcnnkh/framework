package io.basc.framework.execution.reflect;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executors;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import lombok.Data;

@Data
public class ConstructorExecutors implements Executors {
	private final TypeDescriptor source;
	private volatile Elements<? extends ConstructorExecutor> executors;

	public ConstructorExecutors(TypeDescriptor source) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
	}

	@Override
	public Elements<? extends ConstructorExecutor> getExecutors() {
		if (executors == null) {
			synchronized (this) {
				if (executors == null) {
					List<ConstructorExecutor> constructorExecutors = new ArrayList<>();
					for (Constructor<?> constructor : ReflectionUtils.getConstructors(source.getType())) {
						constructorExecutors.add(new ConstructorExecutor(constructor));
					}
					this.executors = Elements.forArray(constructorExecutors.toArray(new ConstructorExecutor[0]));
				}
			}
		}
		return executors;
	}
}
