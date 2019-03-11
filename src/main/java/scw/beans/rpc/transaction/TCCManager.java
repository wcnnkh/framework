package scw.beans.rpc.transaction;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.async.AsyncCompleteFilter;
import scw.beans.proxy.jdk.JDKProxyUtils;
import scw.common.MethodConfig;

public final class TCCManager {
	private TCCManager() {
	};

	private static volatile Map<Class<?>, ClassTCC> cacheMap = new HashMap<Class<?>, ClassTCC>();

	private static ClassTCC getClassTCC(Class<?> clz) {
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

	protected static void transaction(BeanFactory beanFactory, Class<?> interfaceClz, Object rtnValue, Object obj,
			Method method, Object[] args) {
		TCC tcc = method.getAnnotation(TCC.class);
		if (tcc == null) {
			return;
		}

		ClassTCC info = getClassTCC(interfaceClz);
		if (info == null) {
			return;
		}

		MethodConfig confirmMethod = info.getMethodConfig(tcc.confirm());
		MethodConfig cancelMethod = info.getMethodConfig(tcc.cancel());
		MethodConfig complateMethod = info.getMethodConfig(tcc.complete());
		if (confirmMethod == null && cancelMethod == null && complateMethod == null) {
			return;
		}

		MethodConfig tryMethod = new MethodConfig(interfaceClz, method);
		TCCService tccService = beanFactory.get(tcc.service());
		if (tccService == null) {
			return;
		}

		tccService.service(obj, new InvokeInfo(rtnValue, tryMethod, confirmMethod, cancelMethod, complateMethod, args));
	}

	@SuppressWarnings("unchecked")
	public static <T> T convertTransactionProxy(BeanFactory beanFactory, Class<T> interfaceClass, Object obj) {
		TCCTransactionFilter tccTransactionFilter = new TCCTransactionFilter(beanFactory, interfaceClass, obj);
		AsyncCompleteFilter asyncCompleteFilter = beanFactory.get(AsyncCompleteFilter.class);
		return (T) JDKProxyUtils.newProxyInstance(obj, interfaceClass,
				Arrays.asList(tccTransactionFilter, asyncCompleteFilter));
	}
}
