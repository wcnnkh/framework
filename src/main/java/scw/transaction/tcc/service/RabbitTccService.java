package scw.transaction.tcc.service;

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
import scw.beans.annotaion.Destroy;
import scw.common.utils.IOUtils;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;
import scw.transaction.tcc.InvokeInfo;
import scw.transaction.tcc.StageType;
import scw.transaction.tcc.TCCService;
import scw.utils.mq.rabbit.RabbitUtils;

public class RabbitTccService implements TCCService {
	private Connection connection;
	private ExecutorService executorService = new ThreadPoolExecutor(1, 20, 0, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());
	private Channel channel;
	private final String routingKey;
	private final BeanFactory beanFactory;

	public RabbitTccService(BeanFactory beanFactory, ConnectionFactory connectionFactory, String routingKey)
			throws IOException, TimeoutException {
		this(beanFactory, connectionFactory, "rabbit_tcc_service", routingKey, "queue." + routingKey);
	}

	public RabbitTccService(BeanFactory beanFactory, ConnectionFactory connectionFactory, String exchangeName,
			String routingKey, String queueName) throws IOException, TimeoutException {
		this.routingKey = routingKey;
		this.beanFactory = beanFactory;

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

	public void service(Object obj, final InvokeInfo invokeInfo, final String name) {
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void afterProcess() {
				TransactionInfo info = new TransactionInfo();
				info.setInvokeInfo(invokeInfo);
				info.setName(name);
				info.setStageType(StageType.Confirm);
				RabbitUtils.basicPublish(channel, this.getClass().getName(), routingKey,
						IOUtils.javaObjectToByte(info));
			}

			@Override
			public void afterRollback() {
				TransactionInfo info = new TransactionInfo();
				info.setInvokeInfo(invokeInfo);
				info.setName(name);
				info.setStageType(StageType.Cancel);
				RabbitUtils.basicPublish(channel, this.getClass().getName(), routingKey,
						IOUtils.javaObjectToByte(info));
			}

			@Override
			public void complete() {
				TransactionInfo info = new TransactionInfo();
				info.setInvokeInfo(invokeInfo);
				info.setName(name);
				info.setStageType(StageType.Complate);
				RabbitUtils.basicPublish(channel, this.getClass().getName(), routingKey,
						IOUtils.javaObjectToByte(info));
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
			final TransactionInfo info = IOUtils.byteToJavaObject(body);
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
