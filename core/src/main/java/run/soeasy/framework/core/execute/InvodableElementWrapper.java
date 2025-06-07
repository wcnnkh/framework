package run.soeasy.framework.core.execute;

import lombok.NonNull;

public interface InvodableElementWrapper<W extends InvodableElement>
		extends InvodableElement, ExecutableMetadataWrapper<W>, InvokableTemplateWrapper<W> {
	@Override
	default Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		return getSource().invoke(target, parameterTypes, args);
	}

	@Override
	default Object invoke(Object target, @NonNull Object... args) throws Throwable {
		return getSource().invoke(target, args);
	}
}
