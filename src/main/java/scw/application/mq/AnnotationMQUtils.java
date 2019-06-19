package scw.application.mq;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.beans.MethodProxyInvoker;
import scw.core.Consumer;
import scw.core.Parameters;
import scw.core.aop.Invoker;
import scw.core.logger.LoggerUtils;
import scw.core.utils.AnnotationUtils;
import scw.mq.amqp.Exchange;

public final class AnnotationMQUtils {
	private AnnotationMQUtils() {
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void scanningAMQPParamsConsumer(BeanFactory beanFactory, Collection<Class<?>> classes,
			String[] rootFilters) {
		for (Class<?> clz : classes) {
			for (Method method : AnnotationUtils.getAnnoationMethods(clz, true, true, AmqpConsumer.class)) {
				AmqpConsumer c = method.getAnnotation(AmqpConsumer.class);
				Exchange mq = beanFactory.get(c.exchangeService());
				LoggerUtils.info(AnnotationMQUtils.class,
						"添加消费者：{}, amqp routingKey={}, queueName={}, durable={}, exclusive={}, autoDelete={}, clz={}, method={}",
						c.routingKey(), c.queueName(), c.durable(), c.exclusive(), c.autoDelete(), clz.getName(),
						method);
				mq.bindConsumer(c.routingKey(), c.queueName(), c.durable(), c.exclusive(), c.autoDelete(),
						new MqMethodConsumer(new MethodProxyInvoker(beanFactory, clz, method, rootFilters)));
			}
		}
	}
}

@SuppressWarnings("rawtypes")
class MqMethodConsumer implements Consumer {
	private Invoker invoker;

	public MqMethodConsumer(Invoker invoker) {
		this.invoker = invoker;
	}

	public void consume(Object message) {
		if (message == null) {
			return;
		}

		try {
			if (message instanceof Parameters) {
				invoker.invoke(((Parameters) message).getParameters());
			} else {
				if (message.getClass().isArray()) {
					invoker.invoke((Object[]) message);
				} else {
					invoker.invoke(message);
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
