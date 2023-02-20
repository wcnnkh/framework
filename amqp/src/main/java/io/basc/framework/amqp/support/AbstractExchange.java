package io.basc.framework.amqp.support;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.basc.framework.amqp.ArgsMessageCodec;
import io.basc.framework.amqp.Exchange;
import io.basc.framework.amqp.ExchangeDeclare;
import io.basc.framework.amqp.ExchangeException;
import io.basc.framework.amqp.Message;
import io.basc.framework.amqp.MessageListener;
import io.basc.framework.amqp.MessageProperties;
import io.basc.framework.amqp.QueueDeclare;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.retry.RetryCallback;
import io.basc.framework.retry.RetryContext;
import io.basc.framework.retry.RetryOperations;
import io.basc.framework.retry.support.RetryTemplate;
import io.basc.framework.transaction.Synchronization;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionManager;
import io.basc.framework.transaction.Status;
import io.basc.framework.transaction.TransactionUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

/**
 * 此实现通过重试来保证消息的可靠消费
 * 
 * @author shuchaowen
 *
 */
public abstract class AbstractExchange implements Exchange {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final ExchangeDeclare exchangeDeclare;
	private RetryOperations retryOperations = new RetryTemplate();
	private ArgsMessageCodec messageCodec = new SerializerArgsMessageCodec();

	/**
	 * @param serializer
	 * @param exchangeDeclare
	 */
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

	public ArgsMessageCodec getMessageCodec() {
		return messageCodec;
	}

	public void setMessageCodec(ArgsMessageCodec messageCodec) {
		Assert.requiredArgument(messageCodec != null, "messageCodec");
		this.messageCodec = messageCodec;
	}

	public final void bind(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener) {
		logger.info("add message listener：{}, routingKey={}, queueDeclare={}", messageListener, routingKey,
				queueDeclare);
		try {
			retryOperations.execute((context) -> {
				bindInternal(routingKey, queueDeclare, new MessageListenerInternal(messageListener));
				return null;
			});
		} catch (IOException e) {
			throw new ExchangeException("bind error routingKey=" + routingKey, e);
		}
	}

	protected abstract void bindInternal(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener)
			throws IOException;

	public final void push(String routingKey, MessageProperties messageProperties, byte[] body)
			throws ExchangeException {
		Assert.requiredArgument(routingKey != null, "routingKey");
		Assert.requiredArgument(messageProperties != null, "messageProperties");
		Assert.requiredArgument(body != null, "body");
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		final PushRetryCallback retryCallback = new PushRetryCallback(routingKey, messageProperties, body);
		long transactionMessageConfirmDelay = messageProperties.getTransactionMessageConfirmDelay();
		if (transaction != null && transactionMessageConfirmDelay > 0) {
			long delay = Math.max(0, messageProperties.getDelay()) + transactionMessageConfirmDelay;
			MessageProperties confirmMessage = messageProperties.clone();
			confirmMessage.setDelay(delay, TimeUnit.MILLISECONDS);
			// 发送延迟的确认消息
			retryOperations.execute(new PushRetryCallback(routingKey, confirmMessage, body));
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

	protected abstract void basicPublish(String routingKey, MessageProperties messageProperties, byte[] body)
			throws ExchangeException;

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

	/**
	 * 转发消息时会调用此方法
	 * 
	 * @param routingKey
	 * @param messageProperties
	 * @param body
	 */
	protected void forwardPush(String routingKey, MessageProperties messageProperties, byte[] body) throws IOException {
		basicPublish(routingKey, messageProperties, body);
	}

	/**
	 * 失败重试时会调用此方法
	 * 
	 * @param routingKey
	 * @param messageProperties
	 * @param body
	 */
	protected void retryPush(String routingKey, MessageProperties messageProperties, byte[] body) throws IOException {
		basicPublish(routingKey, messageProperties, body);
	}

	protected class MessageListenerInternal implements MessageListener {
		private final MessageListener messageListener;

		public MessageListenerInternal(MessageListener messageListener) {
			this.messageListener = messageListener;
		}

		public void onMessage(String exchange, String routingKey, Message message) throws IOException {
			String routingKeyToUse = message.getPublishRoutingKey();
			if (StringUtils.isEmpty(routingKeyToUse)) {
				routingKeyToUse = routingKey;
			}

			if (message.getDelay() > 0) {
				// 这是一个延迟消息
				if (logger.isDebugEnabled()) {
					logger.debug("delay message forward exchange:{}, routingKey:{}, message:{}", exchange, routingKey,
							JsonUtils.getJsonSupport().toJsonString(message));
				}

				message.setDelay(0, TimeUnit.SECONDS);
				forwardPush(routingKeyToUse, message, message.getBody());
				return;
			}

			if (messageListener == null) {
				// 不应该到这里, 转为延迟消息重试
				int delay = 1;
				TimeUnit delayTimeUnit = TimeUnit.SECONDS;
				logger.error("retry delay: {}, Unable to consume exchange:{}, routingKey:{}, message:{}",
						delayTimeUnit.toMillis(delay), exchange, routingKeyToUse,
						JsonUtils.getJsonSupport().toJsonString(message));
				message.setDelay(delay, delayTimeUnit);
				retryPush(routingKeyToUse, message, message.getBody());
				return;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("handleDelivery exchange:{}, routingKey:{}, message:{}", exchange, routingKeyToUse,
						JsonUtils.getJsonSupport().toJsonString(message));
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
							JsonUtils.getJsonSupport().toJsonString(message));
				} else {
					logger.error(NestedExceptionUtils.getRootCause(e),
							"retry delay: {}, exchange={}, routingKey={}, message={}", retryDelay, exchange,
							routingKeyToUse, JsonUtils.getJsonSupport().toJsonString(message));
					message.setDelay(retryDelay, TimeUnit.MILLISECONDS);
					retryPush(routingKeyToUse, message, message.getBody());
				}
			}
		}
	}

	private class PushRetryCallback implements RetryCallback<Void, ExchangeException> {
		private final String routingKey;
		private final MessageProperties messageProperties;
		private final byte[] body;

		public PushRetryCallback(String routingKey, MessageProperties messageProperties, byte[] body) {
			this.routingKey = routingKey;
			this.messageProperties = messageProperties;
			this.body = body;
		}

		public Void doWithRetry(RetryContext context) throws ExchangeException {
			basicPublish(routingKey, messageProperties, body);
			return null;
		}
	}
}
