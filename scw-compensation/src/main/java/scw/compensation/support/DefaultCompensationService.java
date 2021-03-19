package scw.compensation.support;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import scw.compensation.CompensationException;
import scw.compensation.CompensationService;
import scw.compensation.Compensator;
import scw.compensation.Task;
import scw.compensation.TaskManager;
import scw.core.Assert;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.retry.RetryOperations;
import scw.retry.support.RetryCallable;
import scw.retry.support.RetryTemplate;

public class DefaultCompensationService extends TimerTask implements
		CompensationService {
	private static final Timer TIMER = new Timer(DefaultCompensationService.class.getSimpleName() + "-retry", true);
	private static Logger logger = LoggerFactory
			.getLogger(DefaultCompensationService.class);
	private final TaskManager taskManager;
	private RetryOperations retryOperations = new RetryTemplate();

	public DefaultCompensationService(TaskManager taskManager) {
		this.taskManager = taskManager;
		TIMER.schedule(this, TimeUnit.MINUTES.toSeconds(1), 1);
	}
	
	public RetryOperations getRetryOperations() {
		return retryOperations;
	}

	public void setRetryOperations(RetryOperations retryOperations) {
		Assert.requiredArgument(retryOperations != null, "retryOperations");
		this.retryOperations = retryOperations;
	}

	public Compensator register(Task task)
			throws CompensationException {
		String id = taskManager.register(task);
		RetryCallable<Object> retryCallable = new RetryCallable<Object>(retryOperations, task); 
		return new DefaultCompensator(retryCallable, taskManager, id);
	}
	
	protected Object processTask(Task task) throws Exception{
		RetryCallable<Object> retryCallable = new RetryCallable<Object>(retryOperations, task); 
		return retryCallable.call();
	}

	@Override
	public void run() {
		String lastId = null;
		Task lastTask = null;
		Lock lock = taskManager.getRetryLock();
		try {
			if (lock.tryLock()) {
				lastId = taskManager.getLastRetryId();
				lastTask = taskManager.getTask(lastId);
				Object result = processTask(lastTask);
				taskManager.retrySuccess(lastId, result);
			}
		} catch (Exception e) {
			if(lastId != null){
				taskManager.retryFail(lastId, e);
			}
			logger.error(e, "retry error id [{}] task [{}]", lastId, lastTask);
		} finally {
			lock.unlock();
		}
	}
}
