package scw.mq;

import java.lang.reflect.Method;

import scw.application.ApplicationConfigUtils;
import scw.beans.AbstractBeanConfiguration;
import scw.beans.AutoProxyMethodInvoker;
import scw.beans.BeanFactory;
import scw.beans.SimpleBeanConfiguration;
import scw.beans.annotation.Configuration;
import scw.beans.property.ValueWiredManager;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.annotation.AnnotationUtils;
import scw.core.utils.ClassUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mq.amqp.Exchange;
import scw.mq.annotation.AmqpConsumer;
import scw.mq.annotation.Consumer;
import scw.mq.support.MqMethodConsumer;

@Configuration
public class MQBeanConfigFactory extends AbstractBeanConfiguration implements SimpleBeanConfiguration {
	private static Logger logger = LoggerUtils.getLogger(MQBeanConfigFactory.class);

	public void init(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory) {
		addInit(new ScanConsumer(beanFactory, propertyFactory));
	}

	private static class ScanConsumer implements Init {
		private BeanFactory beanFactory;
		private PropertyFactory propertyFactory;

		public ScanConsumer(BeanFactory beanFactory, PropertyFactory propertyFactory) {
			this.beanFactory = beanFactory;
			this.propertyFactory = propertyFactory;
		}

		public void init() {
			for (Class<?> clazz : ClassUtils
					.getClassSet(ApplicationConfigUtils.getMQAnnotationPackage(propertyFactory))) {
				scanningConsumer(beanFactory, clazz);
				scanningAMQPConsumer(beanFactory, clazz);
			}
		}
	}

	private static void scanningConsumer(BeanFactory beanFactory, Class<?> clz) {
		for (Method method : AnnotationUtils.getAnnoationMethods(clz, true, true, Consumer.class)) {
			Consumer c = method.getAnnotation(Consumer.class);
			ConsumerFactory consumerFactory = beanFactory.getInstance(c.factory());
			logger.info("添加消费者：{}, name={}, factory={}", method, c.name(), c.factory());
			consumerFactory.bindConsumer(c.name(),
					new MqMethodConsumer(new AutoProxyMethodInvoker(beanFactory, clz, method)));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void scanningAMQPConsumer(BeanFactory beanFactory, Class<?> clz) {
		for (Method method : AnnotationUtils.getAnnoationMethods(clz, true, true, AmqpConsumer.class)) {
			AmqpConsumer c = method.getAnnotation(AmqpConsumer.class);
			Exchange mq = beanFactory.getInstance(c.exchangeService());
			logger.info("添加消费者：{}, amqp routingKey={}, queueName={}, durable={}, exclusive={}, autoDelete={}", method,
					c.routingKey(), c.queueName(), c.durable(), c.exclusive(), c.autoDelete());
			mq.bindConsumer(c.routingKey(), c.queueName(), c.durable(), c.exclusive(), c.autoDelete(),
					new MqMethodConsumer(new AutoProxyMethodInvoker(beanFactory, clz, method)));
		}
	}
}
