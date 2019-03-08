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
		Try t = method.getAnnotation(Try.class);
		if (t == null) {
			return;
		}

		ClassTCC tcc = getClassTCC(interfaceClz);
		if (tcc == null) {
			return;
		}

		MethodConfig confirmMethod = tcc.getMethodConfig(t.name(), StageType.Confirm);
		MethodConfig cancelMethod = tcc.getMethodConfig(t.name(), StageType.Cancel);
		MethodConfig complateMethod = tcc.getMethodConfig(t.name(), StageType.Complate);
		if (confirmMethod == null && cancelMethod == null && complateMethod == null) {
			return;
		}

		MethodConfig tryMethod = tcc.getMethodConfig(t.name(), StageType.Try);
		if (tryMethod == null) {
			return;
		}

		TCCService tccService = beanFactory.get(t.service());
		if (tccService == null) {
			return;
		}

		tccService.service(obj, new InvokeInfo(rtnValue, tryMethod, confirmMethod, cancelMethod, complateMethod, args),
				t.name());
	}
}
