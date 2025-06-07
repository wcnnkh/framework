package run.soeasy.framework.core.execute;

public interface Execution {
	ExecutableMetadata getMetadata();

	/**
	 * Get the arguments as an array object. It is possible to change element values
	 * within this array to change the arguments.
	 * 
	 * @return the argument of the invocation
	 */
	Object[] getArguments();

	Object execute() throws Throwable;
}
