package scw.transaction.tcc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public class TCCManager {
	private static volatile Map<Class<?>, ClassTCC> cacheMap = new HashMap<Class<?>, ClassTCC>();

	public static ClassTCC getClassTCC(Class<?> clz) {
		ClassTCC classTCC = cacheMap.get(clz);
		if (classTCC == null) {
			synchronized (cacheMap) {
				classTCC = cacheMap.get(clz);
				if (classTCC == null) {
					classTCC = new ClassTCC(clz);
					cacheMap.put(clz, classTCC);
				}
			}
		}
		return classTCC;
	}

	/**
	 * 把当前TCC加入到事务，如果没有事务就无视
	 * @param tryRtnValue try执行完后返回的值
	 * @param clz
	 * @param name
	 * @param obj
	 * @param args
	 */
	public static void transactionRollback(final Object tryRtnValue, final Method tryMethod, final Class<?> clz, final String name, final Object obj,
			final Object[] args) {
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void beforeProcess() {
				Method method = getClassTCC(clz).getMethod(name, StageType.Confirm);
				if (method == null) {
					return;
				}

				new RetryInvoker(tryRtnValue, tryMethod, obj, method, args);
			}

			@Override
			public void beforeRollback() {
				Method method = getClassTCC(clz).getMethod(name, StageType.Cancel);
				if (method == null) {
					return;
				}

				new RetryInvoker(tryRtnValue, tryMethod, obj, method, args);
			}
		});
	}
}
