package shuchaowen.core.db.storage.async;

import java.io.IOException;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.AbstractAsyncStorage;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.IOUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 基于rabbitMQ实现的异步存盘
 * @author shuchaowen
 *
 */
public class RubbitMQAsyncStorage extends AbstractAsyncStorage {
	private Channel channel;
	private String queueName;
	
	public RubbitMQAsyncStorage(AbstractDB db, Channel channel, String queueName)
			throws IOException {
		super(db);
		this.channel = channel;
		this.queueName = queueName;
		/**
		 * 声明（创建）队列 参数1：队列名称 参数2：为true时server重启队列不会消失 是否持久化
		 * 参数3：队列是否是独占的，如果为true只能被一个connection使用，其他连接建立时会抛出异常
		 * 参数4：队列不再使用时是否自动删除（没有连接，并且没有未处理的消息) 参数5：建立队列时的其他参数
		 */
		channel.queueDeclare(queueName, true, false, false, null);// 创建一个持久化队列
		channel.basicConsume(queueName, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				try {
					ExecuteInfo executeInfo = IOUtils.byteToJavaObject(body);
					if(executeInfo != null){
						getDb().execute(getSqlList(executeInfo));
					}
					getChannel().basicAck(envelope.getDeliveryTag(), false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void execute(ExecuteInfo executeInfo) {
		byte[] data = null;
		try {
			data = IOUtils.javaObjectToByte(executeInfo);
			/**
			 * 向server发布一条消息 
			 * 参数1：exchange名字，若为空则使用默认的exchange 
			 * 参数2：routing key
			 * 参数3：其他的属性 参数4：消息体 RabbitMQ默认有一个exchange，叫default
			 * exchange，它用一个空字符串表示，它是direct exchange类型， *
			 * 任何发往这个exchange的消息都会被路由到routing key的名字对应的队列上，如果没有对应的队列，则消息会被丢弃
			 **/
			channel.basicPublish("", queueName, null, data);
		} catch (IOException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
}
