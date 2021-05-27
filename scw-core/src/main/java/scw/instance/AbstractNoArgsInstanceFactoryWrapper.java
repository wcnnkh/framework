package scw.instance;

import scw.core.utils.ObjectUtils;

public abstract class AbstractNoArgsInstanceFactoryWrapper implements NoArgsInstanceFactory {
	protected abstract NoArgsInstanceFactory getTargetInstanceFactory();

	@Override
	public ClassLoader getClassLoader() {
		return getTargetInstanceFactory().getClassLoader();
	}

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return getTargetInstanceFactory().getInstance(clazz);
	}

	@Override
	public boolean isInstance(Class<?> clazz) {
		return getTargetInstanceFactory().isInstance(clazz);
	}

	@Override
	public boolean isInstance(String name) {
		return getTargetInstanceFactory().isInstance(name);
	}

	@Override
	public <T> T getInstance(String name) {
		return getTargetInstanceFactory().getInstance(name);
	}

	@Override
	public String toString() {
		return getTargetInstanceFactory().toString();
	}

	@Override
	public int hashCode() {
		return getTargetInstanceFactory().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof AbstractNoArgsInstanceFactoryWrapper) {
			return ObjectUtils.nullSafeEquals(getTargetInstanceFactory(),
					((AbstractNoArgsInstanceFactoryWrapper) obj).getTargetInstanceFactory());
		}
		return false;
	}
}
