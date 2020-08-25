package scw.complete.method.async;

import java.lang.reflect.Method;

import scw.complete.method.DefaultMethodCompleteTask;

public class AsyncMethodCompleteTask extends DefaultMethodCompleteTask {
	private static final long serialVersionUID = 1L;

	public AsyncMethodCompleteTask(Method method, String beanName, Object[] args) {
		super(method, beanName, args);
	}

	@Override
	public Object process() throws Exception {
		AsyncMethodInterceptor.startAsync();
		return super.process();
	}
}
