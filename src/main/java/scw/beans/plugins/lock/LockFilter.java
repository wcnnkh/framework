package scw.beans.plugins.lock;

import java.lang.reflect.Method;

import com.alibaba.fastjson.JSONArray;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;
import scw.common.utils.StringUtils;
import scw.database.TransactionContext;
import scw.utils.locks.Lock;
import scw.utils.locks.LockFactory;

/**
 * 实现方法级别的分布式锁
 * 
 * @author shuchaowen
 *
 */
public final class LockFilter implements BeanFilter {
	private LockFactory lockFactory;

	public LockFilter(LockFactory lockFactory) {
		this(lockFactory, "");
	}

	public LockFilter(LockFactory lockFactory, String keyPrefix) {
		this.lockFactory = lockFactory;
	}

	public Object doFilter(Object obj, Method method, Object[] args,
			MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		LockConfig lockConfig = method.getAnnotation(LockConfig.class);
		if (lockConfig == null) {
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}

		StringBuilder sb = new StringBuilder(128);
		if (StringUtils.isEmpty(lockConfig.prefix())) {
			sb.append(method.toString());
		} else {
			sb.append(lockConfig.prefix());
		}

		if (lockConfig.keyIndex().length != 0) {
			sb.append("#");
			JSONArray jarr = new JSONArray();
			for (int index : lockConfig.keyIndex()) {
				jarr.add(args[index]);
			}
			sb.append(jarr.toJSONString());
		}

		String lockKey = sb.toString();
		Lock lock = lockFactory.getLock(lockKey);
		try {
			if (lockConfig.isWait()) {
				lock.lockWait();
			} else if (!lock.lock()) {
				throw new HasBeenLockedException(lockKey);
			}
			TransactionContext.getConfig().setSelectCache(false);
			return beanFilterChain.doFilter(obj, method, args, proxy);
		} finally {
			lock.unlock();
		}
	}
}
