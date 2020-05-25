package scw.amqp.support;

import java.io.IOException;
import java.lang.reflect.Method;

import scw.amqp.Exchange;
import scw.amqp.QueueDeclare;
import scw.beans.AbstractBeanFactoryLifeCycle;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.Configuration;
import scw.io.ResourceUtils;
import scw.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
public final class MethodMessageListenerScan extends AbstractBeanFactoryLifeCycle {

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		for (Class<?> clazz : ResourceUtils.getPackageScan().getClasses(getScanAnnotationPackageName())) {
			scanningAMQPConsumer(beanFactory, clazz);
		}
	}

	public String getScanAnnotationPackageName() {
		return BeanUtils.getScanAnnotationPackageName();
	}

	private void scanningAMQPConsumer(BeanFactory beanFactory, Class<?> clz) throws IOException {
		for (Method method : AnnotationUtils.getAnnoationMethods(clz, true, true, MessageListener.class)) {
			MessageListener messageListener = method.getAnnotation(MessageListener.class);
			Exchange exchange = beanFactory.getInstance(messageListener.exchange());
			QueueDeclare queueDeclare = new QueueDeclare(messageListener.queueName());
			queueDeclare.setDurable(messageListener.durable());
			queueDeclare.setExclusive(messageListener.exclusive());
			queueDeclare.setAutoDelete(messageListener.autoDelete());
			exchange.bind(messageListener.routingKey(), queueDeclare,
					beanFactory.getAop().getProxyMethod(beanFactory, clz, method, null));
		}
	}

	public void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory) {
	}
}
