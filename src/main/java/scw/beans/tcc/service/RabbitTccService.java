package scw.beans.tcc.service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.Connection;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowrite;
import scw.beans.tcc.InvokeInfo;
import scw.beans.tcc.StageType;
import scw.beans.tcc.TCCService;
import scw.core.Consumer;
import scw.mq.amqp.Exchange;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public final class RabbitTccService implements TCCService, scw.core.Destroy {
	private Connection connection;
	private ExecutorService executorService = new ThreadPoolExecutor(1, 20, 0, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());
	private Exchange<TransactionInfo> exchange;
	@Autowrite
	private BeanFactory beanFactory;
	private String routingKey;

	public RabbitTccService(Exchange<TransactionInfo> exchange, String routingKey) {
		this(exchange, routingKey, "queue." + routingKey);
	}

	public RabbitTccService(Exchange<TransactionInfo> exchange, String routingKey, String queueName) {
		this.routingKey = routingKey;
		exchange.bindConsumer(routingKey, queueName, new TccConsumter());
	}

	public void destroy() {
		executorService.shutdownNow();
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void invoke(InvokeInfo invokeInfo, StageType stageType) {
		if (!invokeInfo.hasCanInvoke(stageType)) {
			return;
		}

		TransactionInfo info = new TransactionInfo(invokeInfo, stageType);
		exchange.push(routingKey, info);
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
		});
	}

	final class TccConsumter implements Consumer<TransactionInfo> {

		public void consume(TransactionInfo info) throws Exception {
			info.invoke(beanFactory);
		}
	}
}
