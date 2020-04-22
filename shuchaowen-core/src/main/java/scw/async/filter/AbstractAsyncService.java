package scw.async.filter;

import scw.aop.Context;
import scw.async.AsyncExecutor;
import scw.beans.BeanFactory;
import scw.core.reflect.SerializableMethodHolder;
import scw.core.utils.StringUtils;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractAsyncService implements AsyncService {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	private BeanFactory beanFactory;

	public AbstractAsyncService(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected AsyncRunnableMethod createAsyncExecute(Async async, Context context) {
		String beanName = async.beanName();
		if (StringUtils.isEmpty(beanName)) {
			beanName = context.getTargetClass().getName();
		}

		if (!beanFactory.isInstance(beanName)) {
			throw new NotSupportedException(context.getMethod().toString());
		}

		return new DefaultAsyncRunnableMethod(
				new SerializableMethodHolder(context.getTargetClass(), context.getMethod()), beanName,
				context.getArgs());
	}

	protected abstract AsyncExecutor getAsyncExecutor();

	public void service(Async async, Context context) throws Exception {
		getAsyncExecutor().execute(createAsyncExecute(async, context));
	}

}
