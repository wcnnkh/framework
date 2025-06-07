package run.soeasy.framework.core.execute;

import lombok.NonNull;

@FunctionalInterface
public interface ExecutableTemplateWrapper<W extends ExecutableTemplate>
		extends ExecutableTemplate, ExecutableDescriptorWrapper<W> {

	@Override
	default boolean canExecuted() {
		return getSource().canExecuted();
	}

	@Override
	default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		return getSource().execute(parameterTypes, args);
	}

}