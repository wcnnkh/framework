package run.soeasy.framework.core.invoke;

public interface Invocation extends Execution {
	Object getTarget();

	void setTarget(Object target);
}
