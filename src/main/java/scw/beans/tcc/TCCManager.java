package scw.beans.tcc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import scw.aop.jdk.JDKProxyUtils;
import scw.beans.BeanFactory;
import scw.beans.annotaion.TCC;
import scw.beans.async.AsyncCompleteFilter;
import scw.common.MethodDefinition;

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

	protected static void transaction(BeanFactory beanFactory, Class<?> interfaceClz, Object rtnValue, Method method,
			Object[] args) {
		TCC tcc = method.getAnnotation(TCC.class);
		if (tcc == null) {
			return;
		}

		ClassTCC info = getClassTCC(interfaceClz);
		if (info == null) {
			return;
		}

		MethodDefinition confirmMethod = info.getMethodDefinition(tcc.confirm());
		MethodDefinition cancelMethod = info.getMethodDefinition(tcc.cancel());
		MethodDefinition complateMethod = info.getMethodDefinition(tcc.complete());
		if (confirmMethod == null && cancelMethod == null && complateMethod == null) {
			return;
		}

		MethodDefinition tryMethod = new MethodDefinition(interfaceClz, method);
		TCCService tccService = beanFactory.get(tcc.service());
		if (tccService == null) {
			return;
		}

		tccService.service(new InvokeInfo(rtnValue, tryMethod, confirmMethod, cancelMethod, complateMethod, args));
	}

	@SuppressWarnings("unchecked")
	public static <T> T convertTransactionProxy(BeanFactory beanFactory, Class<T> interfaceClass, Object obj) {
		TCCTransactionFilter tccTransactionFilter = beanFactory.get(TCCTransactionFilter.class);
		AsyncCompleteFilter asyncCompleteFilter = beanFactory.get(AsyncCompleteFilter.class);
		return (T) JDKProxyUtils.newProxyInstance(obj, interfaceClass,
				Arrays.asList(tccTransactionFilter, asyncCompleteFilter));
	}
}
