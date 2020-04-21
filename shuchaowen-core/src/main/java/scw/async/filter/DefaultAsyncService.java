package scw.async.filter;

import java.util.concurrent.TimeUnit;

import scw.async.AsyncExecutor;
import scw.async.local.FileLocalAsyncExecutor;
import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;

@Configuration(order=Integer.MIN_VALUE, value=AsyncService.class)
public class DefaultAsyncService extends AbstractAsyncService {
	private final FileLocalAsyncExecutor fileLocalAsyncExecutor;

	public DefaultAsyncService(BeanFactory beanFactory) {
		super(beanFactory);
		this.fileLocalAsyncExecutor = new FileLocalAsyncExecutor("async", 1,
				TimeUnit.MINUTES);
		fileLocalAsyncExecutor.setBeanFactory(beanFactory);
	}

	@Override
	protected AsyncExecutor getAsyncExecutor() {
		return fileLocalAsyncExecutor;
	}
}
