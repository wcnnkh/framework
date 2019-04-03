package scw.beans.tcc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.beans.BeanFactory;
import scw.beans.annotation.Autowrite;
import scw.beans.annotation.TCC;
import scw.common.MethodDefinition;

/**
 * 只能受BeanFactory管理
 * 
 * @author shuchaowen
 *
 */
public final class TCCTransactionFilter implements Filter {
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

	@Autowrite
	private BeanFactory beanFactory;

	protected TCCTransactionFilter() {
	};

	private void transaction(Class<?> interfaceClz, Object rtnValue, Method method, Object[] args) {
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

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain chain)
			throws Throwable {
		Object rtn = chain.doFilter(invoker, proxy, method, args);
		transaction(method.getDeclaringClass(), rtn, method, args);
		return rtn;
	}

}
