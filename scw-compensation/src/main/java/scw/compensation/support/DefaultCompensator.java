package scw.compensation.support;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import scw.compensation.Compensator;
import scw.compensation.TaskManager;
import scw.lang.NestedExceptionUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class DefaultCompensator extends FutureTask<Object> implements Compensator{
	private static Logger logger = LoggerFactory.getLogger(DefaultCompensator.class);
	private final TaskManager taskManager;
	private final String id;
	
	public DefaultCompensator(Callable<Object> callable, TaskManager taskManager, String id) {
		super(callable);
		this.taskManager = taskManager;
		this.id = id;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if(super.cancel(mayInterruptIfRunning)){
			return taskManager.cancel(id);
		}
		return false;
	}
	
	@Override
	protected void set(Object v) {
		super.set(v);
		taskManager.success(id, v);
	}

	//TODO 应该等待远程执行完成
	@Override
	protected void setException(Throwable t) {
		logger.error(NestedExceptionUtils.getRootCause(t), "execute error retry soon: {}", id);
		super.setException(t);
		taskManager.fail(id, t);
	}
}
