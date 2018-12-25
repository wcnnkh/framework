package scw.locks.filter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import net.sf.cglib.proxy.MethodProxy;
import scw.beans.BeanFilter;
import scw.beans.BeanFilterChain;
import scw.locks.Lock;
import scw.locks.LockFactory;

/**
 * 实现方法级别的分布试锁
 * @author shuchaowen
 *
 */
public class LockFilter implements BeanFilter {
	private LockFactory lockFactory;

	public LockFilter(LockFactory lockFactory) {
		this(lockFactory, "");
	}

	public LockFilter(LockFactory lockFactory, String keyPrefix) {
		this.lockFactory = lockFactory;
	}

	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy, BeanFilterChain beanFilterChain)
			throws Throwable {
		StringBuilder sb = new StringBuilder(512);
		sb.append(this.getClass().getName());
		sb.append("#");

		Parameter[] parameters = method.getParameters();
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				Parameter parameter = parameters[i];
				sb.append(parameter.getType().getName());
				sb.append("#");
			}
		}

		boolean wait = true;
		LockConfig lockConfig = method.getAnnotation(LockConfig.class);
		if (lockConfig != null) {
			if (lockConfig.keyIndex() != null) {
				for (int index : lockConfig.keyIndex()) {
					sb.append(args[index]).append(lockConfig.joinChars());
				}
			}

			wait = lockConfig.isWait();
		}

		String lockKey = sb.toString();
		Lock lock = lockFactory.getLock(lockKey);
		try {
			if (wait) {
				lock.lockWait();
			} else if (!lock.lock()) {
				throw new HasBeenLockedException(lockKey);
			}
			return beanFilterChain.doFilter(obj, method, args, proxy);
		} finally {
			lock.unlock();
		}
	}

}
