package scw.database;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;
import scw.common.ClassInfo;
import scw.common.utils.ClassUtils;
import scw.database.annoation.SelectCache;

/**
 * 事务处理的filter
 * 
 * @author shuchaowen
 *
 */
public class TransactionBeanFilter implements BeanFilter {
	private ClassInfo classInfo;

	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		if (obj instanceof ConnectionSource) {//数据库连接获取类，不用加上事务
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		if (classInfo == null) {
			classInfo = ClassUtils.getClassInfo(obj.getClass());
		}

		boolean isTransaction = DataBaseUtils.isTransaction(classInfo.getClz(), method);
		if (isTransaction) {
			TransactionContext.getInstance().begin();
			try {
				return selectCache(obj, method, args, proxy, beanFilterChain);
			} finally {
				TransactionContext.getInstance().end();
			}
		} else {
			return selectCache(obj, method, args, proxy, beanFilterChain);
		}
	}

	private Object selectCache(Object obj, Method method, Object[] args, MethodProxy proxy,
			BeanFilterChain beanFilterChain) throws Throwable {
		SelectCache selectCache = classInfo.getClz().getAnnotation(SelectCache.class);
		if (selectCache == null) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		} else {
			boolean isSelectCache = DataBaseUtils.isSelectCache(classInfo.getClz(), method);
			boolean oldIsSelectCache = TransactionContext.getInstance().isSelectCache();
			if (isSelectCache == oldIsSelectCache) {
				return beanFilterChain.doFilter(obj, method, args, proxy);
			} else {
				TransactionContext.getInstance().setSelectCache(isSelectCache);
				try {
					return beanFilterChain.doFilter(obj, method, args, proxy);
				} finally {
					TransactionContext.getInstance().setSelectCache(oldIsSelectCache);
				}
			}
		}
	}
}
