package scw.async.filter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import scw.async.AsyncExecutor;
import scw.async.local.FileLocalAsyncExecutor;
import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE, value = AsyncService.class)
public class DefaultAsyncService extends AbstractAsyncService {
	private final FileLocalAsyncExecutor fileLocalAsyncExecutor;

	public DefaultAsyncService(BeanFactory beanFactory) throws IOException {
		super(beanFactory);
		this.fileLocalAsyncExecutor = new FileLocalAsyncExecutor(beanFactory, "async", 1, TimeUnit.MINUTES);
	}

	@Override
	public AsyncExecutor getAsyncExecutor() {
		return fileLocalAsyncExecutor;
	}
}
