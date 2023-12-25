package io.basc.framework.core.type.share;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.type.MethodMetadata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(of = "method")
public class SharableMethodMetadata implements MethodMetadata {
	@NonNull
	private final Method method;

	private volatile MergedAnnotations mergedAnnotations;

	@Override
	public MergedAnnotations getAnnotations() {
		if (mergedAnnotations == null) {
			synchronized (this) {
				if (mergedAnnotations == null) {
					mergedAnnotations = MergedAnnotations.from(method);
				}
			}
		}
		return mergedAnnotations;
	}

	@Override
	public String getMethodName() {
		return method.getName();
	}

	@Override
	public String getDeclaringClassName() {
		return method.getDeclaringClass().getName();
	}

	@Override
	public String getReturnTypeName() {
		return method.getReturnType().getName();
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(method.getModifiers());
	}

	@Override
	public boolean isStatic() {
		return Modifier.isStatic(method.getModifiers());
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(method.getModifiers());
	}

	@Override
	public boolean isOverridable() {
		return !isStatic() && !isFinal() && !Modifier.isPrivate(method.getModifiers());
	}

}
