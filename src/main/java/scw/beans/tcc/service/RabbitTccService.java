package scw.beans.tcc.service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowrite;
import scw.beans.annotation.Destroy;
import scw.beans.tcc.InvokeInfo;
import scw.beans.tcc.StageType;
import scw.beans.tcc.TCCService;
import scw.core.Constants;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;
import scw.utils.mq.rabbit.RabbitUtils;

public final class RabbitTccService implements TCCService {
	private Connection connection;
	private ExecutorService executorService = new ThreadPoolExecutor(1, 20, 0, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());
	private Channel channel;
	private final String routingKey;
	@Autowrite
	private BeanFactory beanFactory;
	private final String exchangeName;

	public RabbitTccService(ConnectionFactory connectionFactory, String routingKey)
			throws IOException, TimeoutException {
		this(connectionFactory, "rabbit_tcc_service", routingKey, "queue." + routingKey);
	}

	public RabbitTccService(ConnectionFactory connectionFactory, String exchangeName, String routingKey,
			String queueName) throws IOException, TimeoutException {
		this.routingKey = routingKey;
		this.exchangeName = exchangeName;
		this.connection = connectionFactory.newConnection();
		channel = connection.createChannel();
		channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
		channel.queueDeclare(queueName, true, true, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		channel.basicConsume(queueName, false, new TccConsumter(channel));
	}

	@Destroy
	public void destory() throws IOException {
		executorService.shutdownNow();
		connection.close();
	}

	private void invoke(InvokeInfo invokeInfo, StageType stageType) {
		if (!invokeInfo.hasCanInvoke(stageType)) {
			return;
		}

		TransactionInfo info = new TransactionInfo(invokeInfo, stageType);
		RabbitUtils.basicPublish(channel, exchangeName, routingKey, Constants.DEFAULT_SERIALIZER.serialize(info));
	}

	public void service(final InvokeInfo invokeInfo) {
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void beforeProcess() {
				invoke(invokeInfo, StageType.Confirm);
			}

			@Override
			public void beforeRollback() {
				invoke(invokeInfo, StageType.Cancel);
			}

			@Override
			public void complete() {
				invoke(invokeInfo, StageType.Complete);
			}
		});
	}

	final class TccConsumter extends DefaultConsumer {

		public TccConsumter(Channel channel) {
			super(channel);
		}

		@Override
		public void handleDelivery(String consumerTag, final Envelope envelope, BasicProperties properties, byte[] body)
				throws IOException {
			final TransactionInfo info = Constants.DEFAULT_SERIALIZER.deserialize(body);
			executorService.execute(new Runnable() {

				public void run() {
					try {
						info.invoke(beanFactory);
						getChannel().basicAck(envelope.getDeliveryTag(), false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
