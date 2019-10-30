package scw.mq;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.beans.MethodProxyInvoker;
import scw.core.reflect.AnnotationUtils;
import scw.logger.LoggerUtils;
import scw.mq.amqp.Exchange;
import scw.mq.annotation.AmqpConsumer;
import scw.mq.annotation.Consumer;
import scw.mq.support.MqMethodConsumer;

public final class MQUtils {
	private MQUtils() {
	}

	public static void scanningAnnotation(BeanFactory beanFactory, Collection<Class<?>> classes) {
		for (Class<?> clz : classes) {
			scanningConsumer(beanFactory, clz);
			scanningAMQPConsumer(beanFactory, clz);
		}
	}

	private static void scanningConsumer(BeanFactory beanFactory, Class<?> clz) {
		for (Method method : AnnotationUtils.getAnnoationMethods(clz, true, true, Consumer.class)) {
			Consumer c = method.getAnnotation(Consumer.class);
			ConsumerFactory consumerFactory = beanFactory.getInstance(c.factory());
			LoggerUtils.getLogger(MQUtils.class).info("添加消费者：{}, name={}, factory={}", method, c.name(), c.factory());
			consumerFactory.bindConsumer(c.name(),
					new MqMethodConsumer(new MethodProxyInvoker(beanFactory, clz, method)));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void scanningAMQPConsumer(BeanFactory beanFactory, Class<?> clz) {
		for (Method method : AnnotationUtils.getAnnoationMethods(clz, true, true, AmqpConsumer.class)) {
			AmqpConsumer c = method.getAnnotation(AmqpConsumer.class);
			Exchange mq = beanFactory.getInstance(c.exchangeService());
			LoggerUtils.getLogger(MQUtils.class).info(
					"添加消费者：{}, amqp routingKey={}, queueName={}, durable={}, exclusive={}, autoDelete={}", method,
					c.routingKey(), c.queueName(), c.durable(), c.exclusive(), c.autoDelete());
			mq.bindConsumer(c.routingKey(), c.queueName(), c.durable(), c.exclusive(), c.autoDelete(),
					new MqMethodConsumer(new MethodProxyInvoker(beanFactory, clz, method)));
		}
	}
}
