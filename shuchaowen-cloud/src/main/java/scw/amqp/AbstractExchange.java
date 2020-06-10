package scw.amqp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import scw.aop.MethodInvoker;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.json.JSONUtils;
import scw.lang.NestedExceptionUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public abstract class AbstractExchange implements Exchange {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	private final NoTypeSpecifiedSerializer serializer;

	public AbstractExchange(NoTypeSpecifiedSerializer serializer) {
		this.serializer = serializer;
	}

	public final NoTypeSpecifiedSerializer getSerializer() {
		return serializer;
	}

	@Override
	public void bind(String routingKey, QueueDeclare queueDeclare, MethodInvoker invoker) {
		bind(routingKey, queueDeclare, new MethodMessageListener(invoker));
	}

	@Override
	public final void bind(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener) {
		logger.info("add message listener：{}, routingKey={}, queueDeclare={}", messageListener, routingKey,
				queueDeclare);
		try {
			bindInternal(routingKey, queueDeclare, new MessageListenerInternal(messageListener));
		} catch (IOException e) {
			logger.error(e, "bind error, Try again in 10 seconds");
			try {
				Thread.sleep(10000);
				bind(routingKey, queueDeclare, messageListener);
			} catch (InterruptedException e1) {
			}
		}
	}

	protected abstract void bindInternal(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener) throws IOException;

	@Override
	public final void push(String routingKey, Message message) {
		push(routingKey, message, message.getBody());
	}

	@Override
	public final void push(String routingKey, MethodMessage methodMessage) {
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
		public void onMessage(String exchange, String routingKey, Message message) throws IOException {
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
	protected void forwardPush(String routingKey, MessageProperties messageProperties, byte[] body) throws IOException {
		push(routingKey, messageProperties, body, false);
	}

	/**
	 * 失败重试时会调用此方法
	 * 
	 * @param routingKey
	 * @param messageProperties
	 * @param body
	 */
	protected void retryPush(String routingKey, MessageProperties messageProperties, byte[] body) throws IOException {
		push(routingKey, messageProperties, body);
	}

	private class MessageListenerInternal implements MessageListener {
		private final MessageListener messageListener;

		MessageListenerInternal(MessageListener messageListener) {
			this.messageListener = messageListener;
		}

		@Override
		public void onMessage(String exchange, String routingKey, Message message) throws IOException {
			if (logger.isTraceEnabled()) {
				logger.trace("handleDelivery exchange:{}, routingKey:{}, message:{}", exchange, routingKey, JSONUtils.toJSONString(message));
			}

			if (message.getDelay() > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("delay message forward exchange:{}, routingKey:{}, message:{}", exchange, routingKey, JSONUtils.toJSONString(message));
				}

				message.setDelay(0, TimeUnit.SECONDS);
				forwardPush(routingKey, message, message.getBody());
				return;
			}

			Transaction transaction = TransactionManager.getTransaction(new DefaultTransactionDefinition());
			try {
				messageListener.onMessage(exchange, routingKey, message);
				TransactionManager.commit(transaction);
			} catch (Throwable e) {
				TransactionManager.rollback(transaction);
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
					logger.error(NestedExceptionUtils.getRootCause(e), "Don't try again: exchange={}, properties={}", exchange,
							JSONUtils.toJSONString(message));
				} else {
					logger.error(NestedExceptionUtils.getRootCause(e), "retry delay: {}, exchange={}, properties={}", retryDelay, exchange,
							JSONUtils.toJSONString(message));
					message.setDelay(retryDelay, TimeUnit.MILLISECONDS);
					retryPush(routingKey, message, message.getBody());
				}
			}
		}
	}

	@Override
	public void push(String routingKey, MessageProperties messageProperties, byte[] body, boolean transaction) {
		if (transaction) {
			throw new UnsupportedOperationException("transactoin push");
		}
		push(routingKey, messageProperties, body);
	}

	@Override
	public final void push(String routingKey, Message message, boolean transaction) {
		push(routingKey, message, message.getBody(), transaction);
	}

	@Override
	public final void push(String routingKey, MethodMessage message, boolean transaction) {
		try {
			push(routingKey, message, serializer.serialize(message.getArgs()), transaction);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
