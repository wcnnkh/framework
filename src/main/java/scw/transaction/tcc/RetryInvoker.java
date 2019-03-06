package scw.transaction.tcc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import scw.common.Logger;
import scw.common.utils.CollectionUtils;

public class RetryInvoker extends TimerTask {
	private final Object tryRtnValue;
	private final Method tryMethod;
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
	public RetryInvoker(Object tryRtnValue, Method tryMethod, Object obj, Method method, Object[] args) {
		this(tryRtnValue, tryMethod, 30000L, obj, method, args);
	}

	/**
	 * 
	 * @param retryTime
	 *            重试时间 毫秒
	 * @param obj
	 * @param method
	 * @param args
	 */
	public RetryInvoker(Object tryRtnValue, Method tryMethod, long retryTime, Object obj, Method method,
			Object[] args) {
		this.tryRtnValue = tryRtnValue;
		this.tryMethod = tryMethod;
		this.obj = obj;
		this.method = method;
		this.args = args;
		this.timer = new Timer();
		timer.schedule(this, 0, retryTime);
	}

	@Override
	public void run() {
		Logger.info(this.getClass().getName(), method.getName());

		if (method.getParameterCount() == 0) {
			invoke(CollectionUtils.EMPTY_ARRAY);
		} else {
			LinkedList<Object> params = new LinkedList<Object>();
			int index = 0;
			for (Parameter parameter : method.getParameters()) {
				TryRtn tryRtn = parameter.getAnnotation(TryRtn.class);
				if (tryRtn == null) {
					params.add(args[index++]);
				} else {
					params.add(tryRtnValue);
				}
			}

			invoke(params.toArray());
		}
	}

	private void invoke(Object[] args) {
		try {
			method.invoke(obj, args);
			timer.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getTryRtnValue() {
		return tryRtnValue;
	}

	public Method getTryMethod() {
		return tryMethod;
	}
}
