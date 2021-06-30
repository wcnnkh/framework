package scw.instance;

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
}
