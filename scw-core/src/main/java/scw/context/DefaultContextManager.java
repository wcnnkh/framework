package scw.context;

public abstract class DefaultContextManager extends AbstractContextManager<DefaultContext> {

	public abstract void setContext(DefaultContext defaultContext);

	public abstract void removeContext();

	@Override
	public void release(Context context) {
		if (context == null) {
			return;
		}

		DefaultContext current = getContext();
		if (current != context) {
			throw new ContextException("Context must be released sequentially(上下文必须顺序释放)");
		}

		try {
			super.release(context);
		} finally {
			if (current.getParent() == null) {
				removeContext();
			} else {
				setContext(current.getParent());
			}
		}
	}

	@Override
	public DefaultContext getContext(Propagation propagation) {
		DefaultContext context = getContext();
		switch (propagation) {
		case REQUIRED:
			context = new DefaultContext(context, context == null, context == null ? true : context.isActive());
			break;
		case SUPPORTS:
			context = new DefaultContext(context, context == null, context == null ? false : context.isActive());
			break;
		case MANDATORY:
			if (context == null) {
				throw new ContextMandatoryException("No context exists");
			}

			if (!context.isActive()) {
				throw new ContextMandatoryException("Context state error [" + context.isActive() + "]");
			}

			context = new DefaultContext(context, false, context.isActive());
			break;
		case REQUIRES_NEW:
			context = new DefaultContext(context, true, true);
			break;
		case NOT_SUPPORTED:
			context = new DefaultContext(context, context == null, false);
			break;
		case NEVER:
			if (context == null) {
				context = new DefaultContext(context, true, false);
			} else if (context.isActive()) {
				throw new ContextNeverException("Context state error [" + context.isActive() + "]");
			} else {
				context = new DefaultContext(context, false, false);
			}
			break;
		case NESTED:
			if (context == null) {
				context = new DefaultContext(context, true, true);
			} else if (context.isActive()) {
				context = new DefaultContext(context, true, context.isActive());
			} else {
				context = new DefaultContext(context, false, context.isActive());
			}
		}
		setContext(context);
		return context;
	}

}
