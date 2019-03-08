package scw.transaction.tcc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.common.MethodConfig;

public abstract class TCCManager {
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

	public static void transaction(BeanFactory beanFactory, Class<?> interfaceClz, Object rtnValue, Object obj,
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
		MethodConfig complateMethod = info.getMethodConfig(tcc.complate());
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
}
