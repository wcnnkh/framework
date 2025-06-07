package run.soeasy.framework.core.execute;

@FunctionalInterface
public interface InvocationWrapper<W extends Invocation> extends Invocation, ExecutionWrapper<W> {
	@Override
	default Object getTarget() {
		return getSource().getTarget();
	}

	@Override
	default void setTarget(Object target) {
		getSource().setTarget(target);
	}
}