package scw.compensation;

import java.util.concurrent.locks.Lock;

public interface TaskManager {
	String register(Task task);
	
	boolean cancel(String id);
	
	boolean success(String id, Object result);
	
	boolean fail(String id, Throwable throwable);
	
	Task getTask(String id);
	
	Object getResult(String id);
	
	String getLastRetryId();
	
	Lock getRetryLock();
	
	boolean retrySuccess(String id, Object result);
	
	boolean retryFail(String id, Throwable throwable);
}
