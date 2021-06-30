package scw.instance;

import scw.core.utils.ObjectUtils;

public class NoArgsInstanceFactoryWrapper<I extends NoArgsInstanceFactory> extends AbstractNoArgsInstanceFactoryWrapper
		implements NoArgsInstanceFactory {
	private final I instanceFactory;

	public NoArgsInstanceFactoryWrapper(I instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	@Override
	protected I getTargetInstanceFactory() {
		return instanceFactory;
	}

	@Override
	public String toString() {
		return instanceFactory.toString();
	}

	@Override
	public int hashCode() {
		return instanceFactory.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof NoArgsInstanceFactoryWrapper) {
			return ObjectUtils.nullSafeEquals(instanceFactory, ((NoArgsInstanceFactoryWrapper<?>) obj).instanceFactory);
		}
		return false;
	}
}
