package scw.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.beans.annotaion.Retry;
import scw.beans.annotaion.SelectCache;
import scw.common.ClassInfo;
import scw.common.FieldInfo;
import scw.common.Logger;
import scw.common.exception.BeansException;
import scw.common.utils.ClassUtils;
import scw.database.TransactionContext;

public final class BeanMethodInterceptor implements MethodInterceptor, BeanFieldListen {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> changeColumnMap;
	private boolean startListen = false;
	private transient ClassInfo classInfo;
	private boolean isBeanListen;
	private transient List<BeanFilter> beanFilters;
	private transient BeanFactory beanFactory;

	// 用于序列化
	protected BeanMethodInterceptor() {
		this(null, null);
	}

	public BeanMethodInterceptor(BeanFactory beanFactory, List<BeanFilter> beanFilters) {
		this.beanFilters = beanFilters;
	}

	private void init(Class<?> type) {
		if (classInfo == null) {
			classInfo = ClassUtils.getClassInfo(type);
		}
		this.isBeanListen = BeanFieldListen.class.isAssignableFrom(classInfo.getClz());
	}

	private Object run(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		boolean isTransaction = BeanUtils.isTransaction(classInfo.getClz(), method);
		if (isTransaction) {
			TransactionContext.getInstance().begin();
			try {
				return selectCache(obj, method, args, proxy);
			} finally {
				TransactionContext.getInstance().end();
			}
		} else {
			return selectCache(obj, method, args, proxy);
		}
	}

	private Object selectCache(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		SelectCache selectCache = classInfo.getClz().getAnnotation(SelectCache.class);
		if (selectCache == null) {
			return filter(obj, method, args, proxy);
		} else {
			boolean isSelectCache = BeanUtils.isSelectCache(classInfo.getClz(), method);
			boolean oldIsSelectCache = TransactionContext.getInstance().isSelectCache();
			if (isSelectCache == oldIsSelectCache) {
				return filter(obj, method, args, proxy);
			} else {
				TransactionContext.getInstance().setSelectCache(isSelectCache);
				try {
					return filter(obj, method, args, proxy);
				} finally {
					TransactionContext.getInstance().setSelectCache(oldIsSelectCache);
				}
			}
		}
	}

	private Object filter(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (beanFilters == null) {
			return proxy.invokeSuper(obj, args);
		} else {
			BeanFilterChain beanFilterChain;
			List<BeanFilter> annotationFilterList = BeanUtils.getBeanFilterList(beanFactory, obj.getClass(), method);
			if (annotationFilterList == null) {
				beanFilterChain = new BeanFilterChain(beanFilters);
			} else {
				List<BeanFilter> list = new ArrayList<BeanFilter>(beanFilters.size() + annotationFilterList.size());
				list.addAll(beanFilters);
				list.addAll(annotationFilterList);
				beanFilterChain = new BeanFilterChain(list);
			}
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}
	}

	private Object retry(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Retry retry = AnnotationBean.getRetry(classInfo.getClz(), method);
		if (retry == null || retry.errors().length == 0) {
			return run(obj, method, args, proxy);
		} else {
			for (int i = 0; i < Math.max(retry.maxCount() + 1, 1); i++) {
				if(i != 0){
					if(retry.log()){
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
					
					if(retry.delayMillis() > 0 || retry.delayNanos() > 0){
						Thread.sleep(Math.abs(retry.delayMillis()), Math.abs(retry.delayNanos()));
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
					return retry(obj, method, args, proxy);
				} else {
					start_field_listen();
					return null;
				}
			} else if (BeanFieldListen.GET_CHANGE_MAP.equals(method.getName())) {
				if (isBeanListen) {
					return retry(obj, method, args, proxy);
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
				rtn = retry(obj, method, args, proxy);
				if (isBeanListen) {
					((BeanFieldListen) obj).field_change(fieldInfo, oldValue);
				} else {
					field_change(fieldInfo, oldValue);
				}
				return rtn;
			}
		}
		return retry(obj, method, args, proxy);
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
