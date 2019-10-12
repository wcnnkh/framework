package scw.beans.tcc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.beans.annotation.TCC;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.instance.InstanceFactory;
import scw.core.reflect.SerializableMethodDefinition;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

/**
 * 只能受BeanFactory管理
 * 
 * @author shuchaowen
 *
 */
public final class TCCTransactionFilter implements Filter {
	private static Logger logger = LoggerUtils.getLogger(TCCTransactionFilter.class);
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

	private InstanceFactory instanceFactory;
	public TCCTransactionFilter(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	};

	private void transaction(Class<?> interfaceClz, Class<?> belongClass, Object rtnValue, Method method, Object[] args) {
		TCC tcc = method.getAnnotation(TCC.class);
		if (tcc == null) {
			return;
		}
		
		if(!instanceFactory.isSingleton(interfaceClz) || !instanceFactory.isInstance(interfaceClz)){
			logger.warn("[{}]不支持使用@TCC注解 {}", interfaceClz.getName(), method);
			return ;
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
		TCCService tccService = instanceFactory.getInstance(tcc.service());
		if (tccService == null) {
			return;
		}

		tccService.service(new InvokeInfo(rtnValue, tryMethod, confirmMethod, cancelMethod, args));
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain chain)
			throws Throwable {
		Object rtn = chain.doFilter(invoker, proxy, method, args);
		transaction(method.getDeclaringClass(), method.getDeclaringClass(), rtn, method, args);
		return rtn;
	}

}
