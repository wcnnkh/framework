package scw.complete.method.async;

import java.util.concurrent.Executor;

import scw.complete.CompleteService;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultAsyncMethodService extends AbstractAsyncMethodService {
	private CompleteService completeService;

	public DefaultAsyncMethodService(CompleteService completeService, Executor executor) throws Exception {
		super(executor);
		this.completeService = completeService;
	}

	@Override
	public CompleteService getCompleteService() {
		return completeService;
	}
}
