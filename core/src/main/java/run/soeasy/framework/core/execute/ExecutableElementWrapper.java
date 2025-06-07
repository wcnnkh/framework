package run.soeasy.framework.core.execute;

import lombok.NonNull;

public interface ExecutableElementWrapper<W extends ExecutableElement>
		extends ExecutableElement, ExecutableMetadataWrapper<W>, ExecutableTemplateWrapper<W> {
	@Override
	default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		return getSource().execute(parameterTypes, args);
	}

	@Override
	default Object execute(@NonNull Object... args) throws Throwable {
		return getSource().execute(args);
	}

}
