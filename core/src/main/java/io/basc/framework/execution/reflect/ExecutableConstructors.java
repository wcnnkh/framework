package io.basc.framework.execution.reflect;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executables;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import lombok.Data;

@Data
public class ExecutableConstructors implements Executables {
	private final TypeDescriptor source;
	private volatile Elements<? extends ConstructorExecutor> members;

	public ExecutableConstructors(TypeDescriptor source) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
	}

	@Override
	public Elements<? extends ConstructorExecutor> getMembers() {
		if (members == null) {
			synchronized (this) {
				if (members == null) {
					List<ConstructorExecutor> constructorExecutors = new ArrayList<>();
					for (Constructor<?> constructor : ReflectionUtils.getConstructors(source.getType())) {
						constructorExecutors.add(new ConstructorExecutor(constructor));
					}
					this.members = Elements.forArray(constructorExecutors.toArray(new ConstructorExecutor[0]));
				}
			}
		}
		return members;
	}
}
