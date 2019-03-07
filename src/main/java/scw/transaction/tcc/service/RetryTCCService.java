package scw.transaction.tcc.service;

import java.util.Timer;
import java.util.TimerTask;

import scw.common.Logger;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;
import scw.transaction.tcc.Cancel;
import scw.transaction.tcc.Confirm;
import scw.transaction.tcc.InvokeInfo;
import scw.transaction.tcc.TCCService;

public class RetryTCCService implements TCCService {
	private final long retryTime;
	private final boolean debug;

	public RetryTCCService() {
		this(30000L, false);
	}

	public RetryTCCService(long retryTime, boolean debug) {
		this.retryTime = retryTime;
		this.debug = debug;
	}

	public void service(final Object obj, final InvokeInfo invokeInfo, final String name) {
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void beforeProcess() {
				new RetryInvoker(obj, invokeInfo, retryTime, true, debug, name).start();
			}

			@Override
			public void beforeRollback() {
				new RetryInvoker(obj, invokeInfo, retryTime, false, debug, name).start();
			}
		});
	}
}

class RetryInvoker extends TimerTask {
	private final Object obj;
	private final InvokeInfo invokeInfo;
	private Timer timer;
	private final long retryTime;
	private final boolean confirm;
	private final boolean debug;
	private final String name;

	/**
	 * 
	 * @param retryTime
	 *            重试时间 毫秒
	 * @param obj
	 * @param method
	 * @param args
	 */
	public RetryInvoker(Object obj, InvokeInfo invokeInfo, long retryTime, boolean confirm, boolean debug,
			String name) {
		this.obj = obj;
		this.invokeInfo = invokeInfo;
		this.retryTime = retryTime;
		this.confirm = confirm;
		this.debug = debug;
		this.name = name;
	}

	@Override
	public void run() {
		try {
			if (debug) {
				if (confirm) {
					Logger.debug(Confirm.class.getName(),
							"clz=" + invokeInfo.getTryMethod().getClz().getName() + ",name=" + name);
				} else {
					Logger.debug(Cancel.class.getName(),
							"clz=" + invokeInfo.getTryMethod().getClz().getName() + ",name=" + name);
				}
			}

			invokeInfo.invoke(confirm, obj);
			timer.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		if (!invokeInfo.hasCanInvoke(confirm)) {
			return;
		}

		this.timer = new Timer();
		timer.schedule(this, 0, retryTime);
	}
}
