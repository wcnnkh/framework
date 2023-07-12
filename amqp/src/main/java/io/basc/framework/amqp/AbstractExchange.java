package io.basc.framework.amqp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.retry.RetryCallback;
import io.basc.framework.retry.RetryContext;
import io.basc.framework.retry.RetryOperations;
import io.basc.framework.retry.support.RetryTemplate;
import io.basc.framework.transaction.Status;
import io.basc.framework.transaction.Synchronization;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionManager;
import io.basc.framework.transaction.TransactionUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;
import io.basc.framework.util.StringUtils;

/**
 * 此实现通过重试来保证消息的可靠消费
 * 
 * @author wcnnkh
 *
 */
public abstract class AbstractExchange<T> implements Exchange<T> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final ExchangeDeclare exchangeDeclare;
	private RetryOperations retryOperations = new RetryTemplate();

	public AbstractExchange(ExchangeDeclare exchangeDeclare) {
		this.exchangeDeclare = exchangeDeclare;
	}

	public RetryOperations getRetryOperations() {
		return retryOperations;
	}

	public void setRetryOperations(RetryOperations retryOperations) {
		Assert.requiredArgument(retryOperations != null, "retryOperations");
		this.retryOperations = retryOperations;
	}

	public final ExchangeDeclare getExchangeDeclare() {
		return exchangeDeclare;
	}

	public final Registration bind(String routingKey, QueueDeclare queueDeclare, MessageListener<T> messageListener) {
		logger.info("add message listener：{}, routingKey={}, queueDeclare={}", messageListener, routingKey,
				queueDeclare);
		try {
			return retryOperations.execute((context) -> {
				return bindInternal(routingKey, queueDeclare, new MessageListenerInternal(messageListener));
			});
		} catch (IOException e) {
			throw new ExchangeException("bind error routingKey=" + routingKey, e);
		}
	}

	protected abstract Registration bindInternal(String routingKey, QueueDeclare queueDeclare,
			MessageListener<T> messageListener) throws IOException;

	public final void push(String routingKey, Message<T> message) throws ExchangeException {
		Assert.requiredArgument(routingKey != null, "routingKey");
		Assert.requiredArgument(message != null, "message");
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		final PushRetryCallback retryCallback = new PushRetryCallback(routingKey, message);
		long transactionMessageConfirmDelay = message.getTransactionMessageConfirmDelay();
		if (transaction != null && transactionMessageConfirmDelay > 0) {
			long delay = Math.max(0, message.getDelay()) + transactionMessageConfirmDelay;
			Message<T> confirmMessage = message.clone();
			confirmMessage.setDelay(delay, TimeUnit.MILLISECONDS);
			// 发送延迟的确认消息
			retryOperations.execute(new PushRetryCallback(routingKey, confirmMessage));
			// 在事务提交后发送消息
			transaction.registerSynchronization(new Synchronization() {

				@Override
				public void beforeCompletion() throws Throwable {
				}

				@Override
				public void afterCompletion(Status status) {
					if (status.equals(Status.COMMITTED)) {
						retryOperations.execute(retryCallback);
					}
				}
			});
		} else {
			retryOperations.execute(retryCallback);
		}
	}

	protected abstract void basicPublish(String routingKey, Message<T> message) throws ExchangeException;

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

	protected void forwardPush(String routingKey, Message<T> message) throws IOException {
		basicPublish(routingKey, message);
	}

	protected void retryPush(String routingKey, Message<T> message) throws IOException {
		basicPublish(routingKey, message);
	}

	protected class MessageListenerInternal implements MessageListener<T> {
		private final MessageListener<T> messageListener;

		public MessageListenerInternal(MessageListener<T> messageListener) {
			this.messageListener = messageListener;
		}

		public void onMessage(String exchange, String routingKey, Message<T> message) throws IOException {
			String routingKeyToUse = message.getPublishRoutingKey();
			if (StringUtils.isEmpty(routingKeyToUse)) {
				routingKeyToUse = routingKey;
			}

			if (message.getDelay() > 0) {
				// 这是一个延迟消息
				if (logger.isDebugEnabled()) {
					logger.debug("delay message forward exchange:{}, routingKey:{}, message:{}", exchange, routingKey,
							message);
				}

				message.setDelay(0, TimeUnit.SECONDS);
				forwardPush(routingKeyToUse, message);
				return;
			}

			if (messageListener == null) {
				// 不应该到这里, 转为延迟消息重试
				int delay = 1;
				TimeUnit delayTimeUnit = TimeUnit.SECONDS;
				logger.error("retry delay: {}, Unable to consume exchange:{}, routingKey:{}, message:{}",
						delayTimeUnit.toMillis(delay), exchange, routingKeyToUse, message);
				message.setDelay(delay, delayTimeUnit);
				retryPush(routingKeyToUse, message);
				return;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("handleDelivery exchange:{}, routingKey:{}, message:{}", exchange, routingKeyToUse,
						message);
			}

			// 开始消费消息
			TransactionManager transactionManager = TransactionUtils.getManager();
			Transaction transaction = transactionManager.getTransaction(TransactionDefinition.DEFAULT);
			try {
				messageListener.onMessage(exchange, routingKeyToUse, message);
				transactionManager.commit(transaction);
			} catch (Throwable e) {
				transactionManager.rollback(transaction);
				message.incrRetryCount();
				long retryDelay = message.getRetryDelay();
				if (retryDelay == 0) {
					retryDelay = getDefaultRetryDelay();
				}

				if (retryDelay != 0) {
					double retryDelayMultiple = message.getRetryDelayMultiple();
					if (retryDelayMultiple > 0) {
						retryDelay = (long) (retryDelay * retryDelayMultiple * message.getRetryCount());
					}
				}

				int maxRetryCount = message.getMaxRetryCount();
				if (maxRetryCount == 0) {
					maxRetryCount = getMaxRetryCount();
				}

				if (retryDelay < 0 || maxRetryCount < 0
						|| (maxRetryCount > 0 && message.getRetryCount() > maxRetryCount)) {// 不重试
					logger.error(NestedExceptionUtils.getRootCause(e),
							"Don't try again: exchange={}, routingKey={}, message={}", exchange, routingKeyToUse,
							message);
				} else {
					logger.error(NestedExceptionUtils.getRootCause(e),
							"retry delay: {}, exchange={}, routingKey={}, message={}", retryDelay, exchange, message);
					message.setDelay(retryDelay, TimeUnit.MILLISECONDS);
					retryPush(routingKeyToUse, message);
				}
			}
		}
	}

	private class PushRetryCallback implements RetryCallback<Void, ExchangeException> {
		private final String routingKey;
		private final Message<T> message;

		public PushRetryCallback(String routingKey, Message<T> message) {
			this.routingKey = routingKey;
			this.message = message;
		}

		public Void doWithRetry(RetryContext context) throws ExchangeException {
			basicPublish(routingKey, message);
			return null;
		}
	}
}
