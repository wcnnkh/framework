package scw.amqp.support;

import java.lang.reflect.Method;

import scw.amqp.Exchange;
import scw.amqp.QueueDeclare;
import scw.beans.AbstractBeanFactoryLifeCycle;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.Configuration;
import scw.util.PackageScan;
import scw.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
public final class MethodMessageListenerScan extends AbstractBeanFactoryLifeCycle {

	@SuppressWarnings("unchecked")
	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		for (Class<?> clazz : PackageScan.getInstance().getClasses(getScanAnnotationPackageName())) {
			if (scw.amqp.MessageListener.class.isAssignableFrom(clazz)) {
				MessageListener messageListener = clazz.getAnnotation(MessageListener.class);
				if (messageListener != null) {
					Exchange exchange = beanFactory.getInstance(messageListener.exchange());
					scw.amqp.MessageListener listener = beanFactory
							.getInstance((Class<scw.amqp.MessageListener>) clazz);
					exchange.bind(messageListener.routingKey(), createQueueDeclare(messageListener), listener);
				}
			}

			for (Method method : AnnotationUtils.getAnnoationMethods(clazz, true, true, MessageListener.class)) {
				MessageListener messageListener = method.getAnnotation(MessageListener.class);
				Exchange exchange = beanFactory.getInstance(messageListener.exchange());
				exchange.bind(messageListener.routingKey(), createQueueDeclare(messageListener),
						beanFactory.getAop().getProxyMethod(beanFactory, clazz, method));
			}
		}
	}

	private QueueDeclare createQueueDeclare(MessageListener messageListener) {
		QueueDeclare queueDeclare = new QueueDeclare(messageListener.queueName());
		queueDeclare.setDurable(messageListener.durable());
		queueDeclare.setExclusive(messageListener.exclusive());
		queueDeclare.setAutoDelete(messageListener.autoDelete());
		return queueDeclare;
	}

	public String getScanAnnotationPackageName() {
		return BeanUtils.getScanAnnotationPackageName();
	}

	public void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory) {
	}
}
