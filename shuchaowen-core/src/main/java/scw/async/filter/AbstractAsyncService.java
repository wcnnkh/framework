package scw.async.filter;

import java.lang.reflect.Method;

import scw.async.AsyncExecutor;
import scw.async.AsyncRunnableMethod;
import scw.async.DefaultAsyncRunnableMethod;
import scw.core.instance.InstanceFactory;
import scw.core.reflect.SerializableMethodHolder;
import scw.core.utils.StringUtils;
import scw.lang.UnsupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractAsyncService implements AsyncService {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	private InstanceFactory instanceFactory;

	public AbstractAsyncService(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	protected AsyncRunnableMethod createAsyncExecute(Async async,
			Class<?> targetClass, Method method, Object[] args) {
		String beanName = async.beanName();
		if (StringUtils.isEmpty(beanName)) {
			beanName = targetClass.getName();
		}

		if (!instanceFactory.isInstance(beanName)) {
			throw new UnsupportedException(method.toString());
		}

		return new DefaultAsyncRunnableMethod(new SerializableMethodHolder(
				targetClass, method), beanName, args);
	}

	protected abstract AsyncExecutor getAsyncExecutor();

	public void service(Async async, Class<?> targetClass, Method method,
			Object[] args) throws Exception {
		getAsyncExecutor().execute(createAsyncExecute(async,
				targetClass, method, args));
	}

}
