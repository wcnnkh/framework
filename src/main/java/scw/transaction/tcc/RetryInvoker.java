package scw.transaction.tcc;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import scw.common.Logger;

public class RetryInvoker extends TimerTask {
	private final Timer timer;
	private final Object obj;
	private final Method method;
	private final Object[] args;

	/**
	 * 如果出现异常30秒后重试
	 * 
	 * @param obj
	 * @param method
	 * @param args
	 */
	public RetryInvoker(Object obj, Method method, Object[] args) {
		this(30000L, obj, method, args);
	}

	/**
	 * 
	 * @param retryTime
	 *            重试时间 毫秒
	 * @param obj
	 * @param method
	 * @param args
	 */
	public RetryInvoker(long retryTime, Object obj, Method method, Object[] args) {
		this.obj = obj;
		this.method = method;
		this.args = args;
		this.timer = new Timer();
		timer.schedule(this, 0, retryTime);
	}

	@Override
	public void run() {
		Logger.info(this.getClass().getName(), method.getName());

		try {
			method.invoke(obj, args);
			timer.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
