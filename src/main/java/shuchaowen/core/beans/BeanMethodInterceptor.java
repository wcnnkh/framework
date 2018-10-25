package shuchaowen.core.beans;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import shuchaowen.core.beans.annotaion.Retry;
import shuchaowen.core.beans.exception.BeansException;
import shuchaowen.core.db.TransactionContext;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.Logger;

public class BeanMethodInterceptor implements MethodInterceptor, BeanListen {
	private static final long serialVersionUID = 1L;
	private final List<BeanFilter> beanFilters;
	private final Class<?> type;
	private transient Map<String, Object> changeColumnMap;
	private transient boolean startListen = false;
	private transient ClassInfo classInfo;
	private final boolean isBeanListen;

	public BeanMethodInterceptor(Class<?> type, List<BeanFilter> beanFilters) {
		this.type = type;
		this.beanFilters = beanFilters;
		this.isBeanListen = BeanListen.class.isAssignableFrom(type);
	}

	private Object run(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		boolean isTransaction = BeanUtils.isTransaction(type, method);
		if (isTransaction) {
			TransactionContext.getInstance().begin();
			try {
				BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilters);
				return beanFilterChain.doFilter(obj, method, args, proxy);
			} catch (Throwable e) {
				throw e;
			} finally {
				TransactionContext.getInstance().commit();
			}
		} else {
			BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilters);
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}
	}

	private ClassInfo getClassInfo() {
		if (classInfo == null) {
			classInfo = ClassUtils.getClassInfo(type);
		}
		return classInfo;
	}

	private Object invoke(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Retry retry = AnnotationBean.getRetry(type, method);
		if (retry == null || retry.errors().length == 0) {
			return run(obj, method, args, proxy);
		} else {
			for (int i = 0; i < Math.max(retry.maxCount() + 1, 1); i++) {
				if (i != 0 && retry.log()) {
					try {
						StringBuilder sb = new StringBuilder();
						sb.append("class:").append(type.getName()).append(",");
						sb.append("method:").append(method.getName()).append(",");
						sb.append("parameterTypes:").append(Arrays.toString(method.getParameterTypes())).append(",");
						sb.append("args:").append(Arrays.toString(args));
						Logger.info("@Retry", sb.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				try {
					return run(obj, method, args, proxy);
				} catch (Throwable e) {
					boolean find = false;
					for (Class<? extends Throwable> error : retry.errors()) {
						if (e.getClass().isAssignableFrom(error)) {
							find = true;
							break;
						}
					}

					if (find) {// 找到对应异常
						e.printStackTrace();
					} else {
						throw e;// 找不到就抛出异常，不再重试
					}
				}
			}
		}
		throw new BeansException();
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (args.length == 0) {
			if (BeanListen.START_LISTEN.equals(method.getName())) {
				if(isBeanListen){
					((BeanListen)obj).start_field_listen();
				}else{
					start_field_listen();
				}
				return null;
			} else if (BeanListen.GET_CHANGE_MAP.equals(method.getName())) {
				if(isBeanListen){
					return ((BeanListen)obj).get_field_change_map();
				}else{
					return get_field_change_map();
				}
			}
		}

		if (startListen) {
			FieldInfo fieldInfo = getClassInfo().getFieldInfoBySetterName(method.getName());
			if (fieldInfo != null) {
				Object rtn;
				Object oldValue = null;
				try {
					oldValue = fieldInfo.forceGet(obj);
					rtn = invoke(obj, method, args, proxy);
					if(isBeanListen){
						((BeanListen)obj).field_change(fieldInfo.getName(), oldValue);
					}else{
						field_change(fieldInfo.getName(), oldValue);
					}
				} catch (Throwable e) {
					throw e;
				}
				return rtn;
			}
		}
		return invoke(obj, method, args, proxy);
	}

	public Map<String, Object> get_field_change_map() {
		return changeColumnMap;
	}

	public void start_field_listen() {
		if (changeColumnMap != null && !changeColumnMap.isEmpty()) {
			changeColumnMap.clear();
		}
		startListen = true;
	}

	public void field_change(String field, Object oldValue) {
		if (changeColumnMap == null) {
			changeColumnMap = new HashMap<String, Object>();
		}
		changeColumnMap.put(field, oldValue);
	}
}
