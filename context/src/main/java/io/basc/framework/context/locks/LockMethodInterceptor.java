package io.basc.framework.context.locks;

import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.locks.LockFactory;
import io.basc.framework.locks.ReentrantLockFactory;
import io.basc.framework.mapper.ParameterDescriptor;

/**
 * 实现方法级别的锁
 * 
 * @author wcnnkh
 *
 */
public final class LockMethodInterceptor implements ExecutionInterceptor {
	private LockFactory lockFactory;

	public LockMethodInterceptor() {
		this(new ReentrantLockFactory());
	}

	public LockMethodInterceptor(LockFactory lockFactory) {
		this.lockFactory = lockFactory;
	}

	@Override
	public Object intercept(Executor executor, Object[] args) throws Throwable {
		LockConfig lockConfig = Annotations.getAnnotation(LockConfig.class, executor.getReturnTypeDescriptor());
		if (lockConfig == null) {
			return executor.execute(args);
		}

		StringBuilder sb = new StringBuilder(128);
		sb.append(executor.toString());
		ParameterDescriptor[] parameterDescriptors = executor.getParameterDescriptors();
		for (int i = 0; i < args.length; i++) {
			ParameterDescriptor parameterDescriptor = parameterDescriptors[i];
			Object arg = args[i];
			boolean b = lockConfig.all();
			LockParameter lockParameter = parameterDescriptor.getTypeDescriptor().getAnnotation(LockParameter.class);
			if (lockParameter != null) {
				b = lockParameter.value();
			}

			if (b) {
				sb.append(sb.length() == 0 ? "?" : "&");
				sb.append(parameterDescriptor.getName());
				sb.append("=");
				sb.append(JsonUtils.getSupport().toJsonString(arg));
			}
		}
		return lockFactory.process(sb.toString(), lockConfig.tryLockTime(), lockConfig.tryLockTimeUnit(),
				() -> executor.execute(args));
	}
}
