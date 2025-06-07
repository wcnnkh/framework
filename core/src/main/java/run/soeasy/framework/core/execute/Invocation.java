package run.soeasy.framework.core.execute;

public interface Invocation extends Execution {
	Object getTarget();

	void setTarget(Object target);
}
