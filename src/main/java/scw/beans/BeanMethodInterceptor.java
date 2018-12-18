package scw.beans;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.beans.annotaion.Retry;
import scw.common.ClassInfo;
import scw.common.FieldInfo;
import scw.common.Logger;
import scw.common.exception.BeansException;
import scw.common.utils.ClassUtils;
import scw.db.TransactionContext;

public class BeanMethodInterceptor implements MethodInterceptor, BeanFieldListen {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> changeColumnMap;
	private boolean startListen = false;
	private transient ClassInfo classInfo;
	private boolean isBeanListen;
	private List<BeanFilter> beanFilters;

	// 用于序列化
	public BeanMethodInterceptor() {
		this(null);
	}

	public BeanMethodInterceptor(List<BeanFilter> beanFilters) {
		this.beanFilters = beanFilters;
	}

	private void init(Class<?> type) {
		if (classInfo == null) {
			classInfo = ClassUtils.getClassInfo(type);
		}
		this.isBeanListen = BeanFieldListen.class.isAssignableFrom(classInfo.getClz());
	}

	private Object run(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilters);
		boolean isTransaction = BeanUtils.isTransaction(classInfo.getClz(), method);
		if (isTransaction) {
			TransactionContext.getInstance().begin();
			try {
				return beanFilterChain.doFilter(obj, method, args, proxy);
			} finally {
				TransactionContext.getInstance().end();
			}
		} else {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}
	}

	private Object invoke(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Retry retry = AnnotationBean.getRetry(classInfo.getClz(), method);
		if (retry == null || retry.errors().length == 0) {
			return run(obj, method, args, proxy);
		} else {
			for (int i = 0; i < Math.max(retry.maxCount() + 1, 1); i++) {
				if (i != 0 && retry.log()) {
					try {
						StringBuilder sb = new StringBuilder();
						sb.append("class:").append(classInfo.getName()).append(",");
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
		init(obj.getClass());
		if (args.length == 0) {
			if (BeanFieldListen.START_LISTEN.equals(method.getName())) {
				if (isBeanListen) {
					startListen = true;
					return invoke(obj, method, args, proxy);
				} else {
					start_field_listen();
					return null;
				}
			} else if (BeanFieldListen.GET_CHANGE_MAP.equals(method.getName())) {
				if (isBeanListen) {
					return invoke(obj, method, args, proxy);
				} else {
					return get_field_change_map();
				}
			}
		}

		if (startListen) {
			FieldInfo fieldInfo = classInfo.getFieldInfoBySetterName(method.getName());
			if (fieldInfo != null) {
				Object rtn;
				Object oldValue = null;
				oldValue = fieldInfo.forceGet(obj);
				rtn = invoke(obj, method, args, proxy);
				if (isBeanListen) {
					((BeanFieldListen) obj).field_change(fieldInfo, oldValue);
				} else {
					field_change(fieldInfo, oldValue);
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

	public void field_change(FieldInfo fieldInfo, Object oldValue) {
		if (changeColumnMap == null) {
			changeColumnMap = new HashMap<String, Object>();
		}
		changeColumnMap.put(fieldInfo.getName(), oldValue);
	}
}
