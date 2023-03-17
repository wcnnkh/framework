package io.basc.framework.context.locks;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.MethodInterceptorAccept;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterUtils;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.locks.LockFactory;
import io.basc.framework.locks.ReentrantLockFactory;

/**
 * 实现方法级别的锁
 * 
 * @author wcnnkh
 *
 */
@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public final class LockMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private LockFactory lockFactory;

	public LockMethodInterceptor() {
		this(new ReentrantLockFactory());
	}

	public LockMethodInterceptor(LockFactory lockFactory) {
		this.lockFactory = lockFactory;
	}

	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return Annotations.getAnnotation(LockConfig.class, invoker.getMethod(), invoker.getSourceClass()) != null;
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		LockConfig lockConfig = Annotations.getAnnotation(LockConfig.class, invoker.getMethod(),
				invoker.getSourceClass());
		if (lockConfig == null) {
			return invoker.invoke(args);
		}

		StringBuilder sb = new StringBuilder(128);
		sb.append(invoker.getMethod().toString());
		ParameterDescriptor[] configs = ParameterUtils.getParameters(invoker.getMethod());
		for (int i = 0; i < configs.length; i++) {
			ParameterDescriptor config = configs[i];
			boolean b = lockConfig.all();
			LockParameter lockParameter = config.getAnnotation(LockParameter.class);
			if (lockParameter != null) {
				b = lockParameter.value();
			}

			if (b) {
				sb.append(i == 0 ? "?" : "&");
				sb.append(config.getName());
				sb.append("=");
				sb.append(JsonUtils.getSupport().toJsonString(args[i]));
			}
		}
		return lockFactory.process(sb.toString(), lockConfig.tryLockTime(), lockConfig.tryLockTimeUnit(),
				() -> invoker.invoke(args));
	}
}
