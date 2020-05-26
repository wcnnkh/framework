package scw.amqp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import scw.aop.MethodInvoker;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public abstract class AbstractExchange implements Exchange {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	private NoTypeSpecifiedSerializer serializer;

	public AbstractExchange(NoTypeSpecifiedSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public void bind(String routingKey, QueueDeclare queueDeclare, MethodInvoker methodInvoker) {
		logger.info("add message listener：{}, routingKey={}, queueDeclare={}", methodInvoker.getMethod(), routingKey,
				queueDeclare);
		bind(routingKey, queueDeclare, new MethodMessageListener(methodInvoker));
	}

	@Override
	public void push(String routingKey, Message message) {
		push(routingKey, message, message.getBody());
	}

	protected abstract void push(String routingKey, MessageProperties messageProperties, byte[] body);

	@Override
	public void push(String routingKey, MethodMessage methodMessage) {
		try {
			push(routingKey, methodMessage, serializer.serialize(methodMessage.getArgs()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final class MethodMessageListener implements MessageListener {
		private MethodInvoker invoker;

		public MethodMessageListener(MethodInvoker invoker) {
			this.invoker = invoker;
		}

		@Override
		public void onMessage(String exchange, String routingKey, Message message) throws Throwable {
			Object[] args = serializer.deserialize(message.getBody());
			invoker.invoke(args);
		}
	}

	protected long getDefaultRetryDelay() {
		return 10000;
	}

	/**
	 * 0表示一直重试
	 * 
	 * @return
	 */
	protected int getMaxRetryCount() {
		return 0;
	}

	protected void onMessageInternal(String exchange, String routingKey, Message message,
			MessageListener messageListener) {
		if (logger.isTraceEnabled()) {
			logger.trace("handleDelivery: {}", JSONUtils.toJSONString(message));
		}

		if (message.getDelay() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("delay message forward properties: {}", JSONUtils.toJSONString(message));
			}

			message.setDelay(0, TimeUnit.SECONDS);
			push(routingKey, message, message.getBody());
			return;
		}

		Transaction transaction = TransactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			messageListener.onMessage(exchange, routingKey, message);
			TransactionManager.commit(transaction);
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			long retryDelay = message.getRetryDelay();
			if (retryDelay == 0) {
				retryDelay = getDefaultRetryDelay();
			}

			int maxRetryCount = message.getMaxRetryCount();
			if (maxRetryCount == 0) {
				maxRetryCount = getMaxRetryCount();
			}

			if (retryDelay < 0 || maxRetryCount < 0 || (maxRetryCount > 0 && message.getRetryCount() > maxRetryCount)) {// 不重试
				logger.error(e, "Don't try again: exchange={}, properties={}", exchange,
						JSONUtils.toJSONString(message));
			} else {
				logger.error(e, "retry delay: {}, exchange={}, properties={}", retryDelay, exchange,
						JSONUtils.toJSONString(message));
				message.setDelay(retryDelay, TimeUnit.MILLISECONDS);
				message.incrRetryCount();
				push(routingKey, message);
			}
		}
	}
}
