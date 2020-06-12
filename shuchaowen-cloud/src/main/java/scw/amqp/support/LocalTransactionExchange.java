package scw.amqp.support;

import scw.amqp.AbstractExchange;
import scw.amqp.Exchange;
import scw.amqp.MessageProperties;
import scw.beans.BeanFactoryAccessor;
import scw.complete.Complete;
import scw.complete.CompleteService;
import scw.complete.CompleteTask;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public abstract class LocalTransactionExchange extends AbstractExchange {
	private CompleteService completeService;
	private String beanId;

	public LocalTransactionExchange(NoTypeSpecifiedSerializer serializer, CompleteService completeService, String beanId) {
		super(serializer);
		this.completeService = completeService;
		this.beanId = beanId;
	}

	public void push(String routingKey, MessageProperties messageProperties, byte[] body, boolean transaction) {
		if (transaction && TransactionManager.hasTransaction()) {
			try {
				final Complete complete = completeService
						.register(new PushCompleteTask(beanId, routingKey, messageProperties, body));
				TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
					@Override
					public void afterCommit() {
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

	@Override
	public void push(String routingKey, MessageProperties messageProperties, byte[] body) {
		push(routingKey, messageProperties, body, true);
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
			Exchange exchange = getBeanFactory().getInstance(rabbitExchangeBeanId);
			exchange.push(routingKey, messageProperties, body, false);
			return null;
		}
	}
}
