package scw.aop;

import java.lang.reflect.Method;

import scw.core.instance.InstanceUtils;
import scw.util.result.SimpleResult;

public final class ProxyUtils {

	private static final MultipleProxyAdapter PROXY_ADAPTER = new MultipleProxyAdapter();

	static {
		PROXY_ADAPTER.addAll(InstanceUtils
				.getSystemConfigurationList(ProxyAdapter.class));
	}

	private ProxyUtils() {
	};

	public static MultipleProxyAdapter getProxyAdapter() {
		return PROXY_ADAPTER;
	}

	private static int ignoreHashCode(Object obj) {
		return System.identityHashCode(obj);
	}

	private static String ignoreToString(Object obj) {
		return obj.getClass().getName() + "@"
				+ Integer.toHexString(ignoreHashCode(obj));
	}

	/**
	 * 如果返回空说明此方法不能忽略
	 * 
	 * @param obj
	 * @param method
	 * @param args
	 * @return
	 */
	public static SimpleResult<Object> ignoreMethod(Object obj, Method method,
			Object[] args) {
		if (args == null || args.length == 0) {
			if (method.getName().equals("hashCode")) {
				return new SimpleResult<Object>(true, ignoreHashCode(obj));
			} else if (method.getName().equals("toString")) {
				return new SimpleResult<Object>(true, ignoreToString(obj));
			}
		}

		if (args != null && args.length == 1
				&& method.getName().equals("equals")) {
			return new SimpleResult<Object>(true, obj == args[0]);
		}
		return new SimpleResult<Object>(false);
	}
}
