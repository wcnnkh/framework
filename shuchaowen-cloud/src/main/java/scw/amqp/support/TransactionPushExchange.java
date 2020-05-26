package scw.amqp.support;

import java.util.concurrent.TimeUnit;

import scw.amqp.AbstractExchange;
import scw.amqp.MessageProperties;
import scw.beans.BeanFactoryAccessor;
import scw.complete.Complete;
import scw.complete.CompleteService;
import scw.complete.CompleteTask;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public abstract class TransactionPushExchange extends AbstractExchange {
	private CompleteService completeService;
	private String beanId;

	public TransactionPushExchange(NoTypeSpecifiedSerializer serializer, CompleteService completeService,
			String beanId) {
		super(serializer);
		this.completeService = completeService;
		this.beanId = beanId;
	}

	@Override
	protected void push(String routingKey, MessageProperties messageProperties, byte[] body) {
		if (TransactionManager.hasTransaction()) {
			try {
				final Complete complete = completeService
						.register(new PushCompleteTask(beanId, routingKey, messageProperties, body));
				TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
					@Override
					public void complete() {
						complete.run();
					}

					@Override
					public void afterRollback() {
						super.afterRollback();
						complete.cancel();
					}
				});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			basePush(routingKey, messageProperties, body);
		}
	}

	protected abstract void basePush(String routingKey, MessageProperties messageProperties, byte[] body);

	private static class PushCompleteTask extends BeanFactoryAccessor implements CompleteTask {
		private static final long serialVersionUID = 1L;
		private String rabbitExchangeBeanId;
		private String routingKey;
		private MessageProperties messageProperties;
		private byte[] body;

		public PushCompleteTask(String rabbitExchangeBeanId, String routingKey, MessageProperties messageProperties,
				byte[] body) {
			this.rabbitExchangeBeanId = rabbitExchangeBeanId;
			this.routingKey = routingKey;
			this.messageProperties = messageProperties;
			this.body = body;
		}

		public Object process() throws Throwable {
			TransactionPushExchange exchange = getBeanFactory().getInstance(rabbitExchangeBeanId);
			exchange.basePush(routingKey, messageProperties, body);
			return null;
		}
	}

	protected long getRetryDelay() {
		return TimeUnit.SECONDS.toMillis(10);
	}
}
