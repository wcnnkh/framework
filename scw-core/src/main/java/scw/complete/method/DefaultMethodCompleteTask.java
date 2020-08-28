package scw.complete.method;

import java.lang.reflect.Method;

public class DefaultMethodCompleteTask extends MethodCompleteTask {
	private static final long serialVersionUID = 1L;
	private final String beanName;

	public DefaultMethodCompleteTask(Method method, String beanName, Object[] args) {
		super(method, args);
		this.beanName = beanName;
	}

	@Override
	public Object getInstance() {
		return getBeanFactory().getInstance(beanName);
	}
}
