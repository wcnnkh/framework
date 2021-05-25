package scw.instance;

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
}
