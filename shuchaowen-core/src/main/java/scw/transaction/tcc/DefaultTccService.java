package scw.transaction.tcc;

import java.util.concurrent.TimeUnit;

import scw.beans.BeanFactory;
import scw.complete.Complete;
import scw.complete.LocalCompleteService;
import scw.core.Destroy;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE, value = TccService.class)
public class DefaultTccService extends AbstractTccService implements Destroy {
	private LocalCompleteService completeService;

	public DefaultTccService(BeanFactory beanFactory) throws Exception {
		super(beanFactory);
		this.completeService = new LocalCompleteService(beanFactory, "tcc", 1, TimeUnit.MINUTES);
		this.completeService.init();
	}

	public Complete registerComplete(Stage stage) throws Exception {
		return completeService.register(stage);
	}

	public void destroy() throws Exception {
		completeService.destroy();
	}
}
