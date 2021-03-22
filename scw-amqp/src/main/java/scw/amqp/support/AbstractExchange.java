package scw.amqp.support;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import scw.amqp.Exchange;
import scw.amqp.ExchangeDeclare;
import scw.amqp.ExchangeException;
import scw.amqp.Message;
import scw.amqp.MessageListener;
import scw.amqp.MessageProperties;
import scw.amqp.MethodMessage;
import scw.amqp.QueueDeclare;
import scw.core.Assert;
import scw.core.Ordered;
import scw.core.reflect.MethodInvoker;
import scw.core.utils.StringUtils;
import scw.io.NoTypeSpecifiedSerializer;
import scw.json.JSONUtils;
import scw.lang.NestedExceptionUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.retry.RetryCallback;
import scw.retry.RetryContext;
import scw.retry.RetryOperations;
import scw.retry.support.RetryTemplate;
import scw.transaction.DefaultTransactionLifecycle;
import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionManager;
import scw.transaction.TransactionUtils;

/**
 * 此实现通过重试来保证消息的可靠消费
 * 
 * @author shuchaowen
 *
 */
public abstract class AbstractExchange implements Exchange {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	private final NoTypeSpecifiedSerializer serializer;
	private final ExchangeDeclare exchangeDeclare;
	private RetryOperations retryOperations = new RetryTemplate();

	/**
	 * @param serializer
	 * @param exchangeDeclare
	 */
	public AbstractExchange(NoTypeSpecifiedSerializer serializer,
			ExchangeDeclare exchangeDeclare) {
		this.serializer = serializer;
		this.exchangeDeclare = exchangeDeclare;
	}

	public RetryOperations getRetryOperations() {
		return retryOperations;
	}

	public void setRetryOperations(RetryOperations retryOperations) {
		Assert.requiredArgument(retryOperations != null, "retryOperations");
		this.retryOperations = retryOperations;
	}

	public final NoTypeSpecifiedSerializer getSerializer() {
		return serializer;
	}

	public final ExchangeDeclare getExchangeDeclare() {
		return exchangeDeclare;
	}

	public final void bind(String routingKey, QueueDeclare queueDeclare,
			MethodInvoker invoker) {
		bind(routingKey, queueDeclare, new MethodMessageListener(invoker));
	}

	public final void bind(String routingKey, QueueDeclare queueDeclare,
			MessageListener messageListener) {
		logger.info("add message listener：{}, routingKey={}, queueDeclare={}",
				messageListener, routingKey, queueDeclare);
		try {
			bindInternal(routingKey, queueDeclare, new MessageListenerInternal(
					messageListener));
		} catch (IOException e) {
			logger.error(e, "bind error, Try again in 10 seconds");
			try {
				Thread.sleep(10000);
				bind(routingKey, queueDeclare, messageListener);
			} catch (InterruptedException e1) {
			}
		}
	}

	protected abstract void bindInternal(String routingKey,
			QueueDeclare queueDeclare, MessageListener messageListener)
			throws IOException;

	public final void push(String routingKey, Message message) {
		push(routingKey, message, message.getBody());
	}

	public final void push(String routingKey, MethodMessage methodMessage) {
		byte[] body = serializer.serialize(methodMessage.getArgs());
		push(routingKey, methodMessage, body);
	}

	public final void push(String routingKey,
			MessageProperties messageProperties, byte[] body)
			throws ExchangeException {
		Transaction transaction = TransactionUtils.getManager()
				.getTransaction();
		final PushRetryCallback retryCallback = new PushRetryCallback(
				routingKey, messageProperties, body);
		long transactionMessageConfirmDelay = messageProperties.getTransactionMessageConfirmDelay();
		if(transaction != null && transactionMessageConfirmDelay > 0){
			long delay = Math.abs(messageProperties.getDelay()) + transactionMessageConfirmDelay;
			MessageProperties confirmMessage = messageProperties.clone();
			confirmMessage.setDelay(delay, TimeUnit.MILLISECONDS);
			//发送延迟的确认消息
			retryOperations.execute(new PushRetryCallback(
				routingKey, confirmMessage, body));
			//在事务提交后发送消息
			transaction.addLifecycle(new DefaultTransactionLifecycle(){
				@Override
				public int getOrder() {
					return Ordered.LOWEST_PRECEDENCE;
				}
				
				public void afterCommit() {
					retryOperations.execute(retryCallback);
				};
			});
		}else{
			retryOperations.execute(retryCallback);
		}
	}

	public abstract void basicPublish(String routingKey,
			MessageProperties messageProperties, byte[] body)
			throws ExchangeException;

	private final class MethodMessageListener implements MessageListener {
		private MethodInvoker invoker;

		public MethodMessageListener(MethodInvoker invoker) {
			this.invoker = invoker;
		}

		public void onMessage(String exchange, String routingKey,
				Message message) throws IOException {
			try {
				Object[] args = serializer.deserialize(message.getBody());
				invoker.invoke(args);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String toString() {
			return invoker.toString();
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

	/**
	 * 转发消息时会调用此方法
	 * 
	 * @param routingKey
	 * @param messageProperties
	 * @param body
	 */
	protected void forwardPush(String routingKey,
			MessageProperties messageProperties, byte[] body)
			throws IOException {
		basicPublish(routingKey, messageProperties, body);
	}

	/**
	 * 失败重试时会调用此方法
	 * 
	 * @param routingKey
	 * @param messageProperties
	 * @param body
	 */
	protected void retryPush(String routingKey,
			MessageProperties messageProperties, byte[] body)
			throws IOException {
		basicPublish(routingKey, messageProperties, body);
	}

	protected class MessageListenerInternal implements MessageListener {
		private final MessageListener messageListener;

		public MessageListenerInternal(MessageListener messageListener) {
			this.messageListener = messageListener;
		}

		public void onMessage(String exchange, String routingKey,
				Message message) throws IOException {
			String routingKeyToUse = message.getPublishRoutingKey();
			if (StringUtils.isEmpty(routingKeyToUse)) {
				routingKeyToUse = routingKey;
			}

			if (message.getDelay() > 0) {
				//这是一个延迟消息
				if (logger.isDebugEnabled()) {
					logger.debug(
							"delay message forward exchange:{}, routingKey:{}, message:{}",
							exchange, routingKey,
							JSONUtils.toJSONString(message));
				}

				message.setDelay(0, TimeUnit.SECONDS);
				forwardPush(routingKeyToUse, message, message.getBody());
				return;
			}

			if (messageListener == null) {
				// 不应该到这里, 转为延迟消息重试
				int delay = 1;
				TimeUnit delayTimeUnit = TimeUnit.SECONDS;
				logger.error(
						"retry delay: {}, Unable to consume exchange:{}, routingKey:{}, message:{}",
						delayTimeUnit.toMillis(delay), exchange,
						routingKeyToUse, JSONUtils.toJSONString(message));
				message.setDelay(delay, delayTimeUnit);
				retryPush(routingKeyToUse, message, message.getBody());
				return;
			}

			if (logger.isDebugEnabled()) {
				logger.debug(
						"handleDelivery exchange:{}, routingKey:{}, message:{}",
						exchange, routingKeyToUse,
						JSONUtils.toJSONString(message));
			}

			//开始消费消息
			TransactionManager transactionManager = TransactionUtils
					.getManager();
			Transaction transaction = transactionManager
					.getTransaction(TransactionDefinition.DEFAULT);
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
						retryDelay = (long) (retryDelay * retryDelayMultiple * message
								.getRetryCount());
					}
				}

				int maxRetryCount = message.getMaxRetryCount();
				if (maxRetryCount == 0) {
					maxRetryCount = getMaxRetryCount();
				}

				if (retryDelay < 0
						|| maxRetryCount < 0
						|| (maxRetryCount > 0 && message.getRetryCount() > maxRetryCount)) {// 不重试
					logger.error(
							NestedExceptionUtils.getRootCause(e),
							"Don't try again: exchange={}, routingKey={}, message={}",
							exchange, routingKeyToUse,
							JSONUtils.toJSONString(message));
				} else {
					logger.error(
							NestedExceptionUtils.getRootCause(e),
							"retry delay: {}, exchange={}, routingKey={}, message={}",
							retryDelay, exchange, routingKeyToUse,
							JSONUtils.toJSONString(message));
					message.setDelay(retryDelay, TimeUnit.MILLISECONDS);
					retryPush(routingKeyToUse, message, message.getBody());
				}
			}
		}
	}

	private class PushRetryCallback implements
			RetryCallback<Void, ExchangeException> {
		private final String routingKey;
		private final MessageProperties messageProperties;
		private final byte[] body;

		public PushRetryCallback(String routingKey,
				MessageProperties messageProperties, byte[] body) {
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
