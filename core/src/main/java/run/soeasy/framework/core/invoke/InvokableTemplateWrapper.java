package run.soeasy.framework.core.invoke;

import lombok.NonNull;

@FunctionalInterface
public interface InvokableTemplateWrapper<W extends InvokableTemplate> extends InvokableTemplate, ExecutableDescriptorWrapper<W> {
	@Override
	default Object invoke(Object target) throws Throwable {
		return getSource().invoke(target);
	}

	@Override
	default Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		return getSource().invoke(target, parameterTypes, args);
	}

}