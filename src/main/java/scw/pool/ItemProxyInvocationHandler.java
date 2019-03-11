package scw.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ItemProxyInvocationHandler implements InvocationHandler {
	private int index;
	private long lastTime;
	private final Item target;

	public ItemProxyInvocationHandler(int index, Item target) {
		this.index = index;
		this.target = target;
		this.lastTime = System.currentTimeMillis();
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (args.length == 0) {
			if (method.getName().equals("getPoolIndex")) {
				return index;
			} else if (method.getName().equals("getTarget")) {
				return target;
			} else if (method.getName().equals("getLastTime")) {
				return lastTime;
			}
		} else if (args.length == 1) {
			if (method.getName().equals("setPoolIndex")
					&& (args[0] instanceof Integer)) {
				index = (Integer) args[0];
				return null;
			}
		}

		return method.invoke(target, args);
	}

}
