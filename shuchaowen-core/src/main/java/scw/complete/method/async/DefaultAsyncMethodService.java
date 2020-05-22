package scw.complete.method.async;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import scw.beans.BeanFactory;
import scw.complete.CompleteService;
import scw.complete.LocalCompleteService;
import scw.core.Destroy;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultAsyncMethodService extends AbstractAsyncMethodService implements Destroy {
	private final LocalCompleteService localCompleteService;

	public DefaultAsyncMethodService(BeanFactory beanFactory) throws Exception {
		this.localCompleteService = new LocalCompleteService(beanFactory, "async", 1, TimeUnit.MINUTES);
		localCompleteService.init();
	}

	@Override
	public Executor getExecutor() {
		return localCompleteService.getExecutorService();
	}

	@Override
	public CompleteService getCompleteService() {
		return localCompleteService;
	}

	public void destroy() throws Exception {
		localCompleteService.destroy();
	}

}
