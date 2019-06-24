package scw.beans.tcc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowrite;
import scw.beans.annotation.TCC;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.reflect.SerializableMethodDefinition;
import scw.core.utils.ClassUtils;

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

	private void transaction(Class<?> interfaceClz, Class<?> belongClass, Object rtnValue, Method method, Object[] args) {
		TCC tcc = method.getAnnotation(TCC.class);
		if (tcc == null) {
			return;
		}

		ClassTCC info = getClassTCC(interfaceClz);
		if (info == null) {
			return;
		}

		SerializableMethodDefinition confirmMethod = info.getMethodDefinition(tcc.confirm());
		SerializableMethodDefinition cancelMethod = info.getMethodDefinition(tcc.cancel());
		if (confirmMethod == null && cancelMethod == null) {
			return;
		}

		SerializableMethodDefinition tryMethod = new SerializableMethodDefinition(belongClass, method);
		TCCService tccService = beanFactory.get(tcc.service());
		if (tccService == null) {
			return;
		}

		tccService.service(new InvokeInfo(rtnValue, tryMethod, confirmMethod, cancelMethod, args));
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain chain)
			throws Throwable {
		Object rtn = chain.doFilter(invoker, proxy, method, args);
		transaction(method.getDeclaringClass(), ClassUtils.getUserClass(proxy), rtn, method, args);
		return rtn;
	}

}
