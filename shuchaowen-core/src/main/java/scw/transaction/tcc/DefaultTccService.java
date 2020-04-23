package scw.transaction.tcc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import scw.async.AsyncExecutor;
import scw.async.local.FileLocalAsyncExecutor;
import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE, value = TccService.class)
public class DefaultTccService extends AbstractTccService {
	private AsyncExecutor asyncExecutor;

	public DefaultTccService(BeanFactory beanFactory) throws IOException {
		super(beanFactory);
		this.asyncExecutor = new FileLocalAsyncExecutor(beanFactory, "tcc", 1, TimeUnit.MINUTES);
	}

	public void execute(Stage stage) {
		asyncExecutor.execute(stage);
	}

}
