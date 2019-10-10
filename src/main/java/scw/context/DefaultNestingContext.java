package scw.context;


public class DefaultNestingContext extends DefaultContext implements
		NestingContext {
	private final NestingContext parentContext;
	private final boolean newContext;

	/**
	 * @param parentContext
	 * @param newContext 是否是一个新的上下文
	 */
	public DefaultNestingContext(NestingContext parentContext, boolean newContext) {
		this.parentContext = parentContext;
		this.newContext = newContext;
	}

	@Override
	public Object getResource(Object name) {
		if (newContext || parentContext == null) {
			return super.getResource(name);
		}

		return parentContext.getResource(name);
	}

	@Override
	public Object bindResource(Object name, Object value) {
		if (newContext || parentContext == null) {
			return super.bindResource(name, value);
		}

		return parentContext.bindResource(name, value);
	}

	public NestingContext getParentContext() {
		return parentContext;
	}

	public boolean isNewContext() {
		return newContext;
	}

}
