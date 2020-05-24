package scw.rabbitmq.method;

import java.io.IOException;
import java.lang.reflect.Method;

import scw.beans.AbstractBeanFactoryLifeCycle;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;
import scw.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
public final class RabbitmqMethodConsumerScan extends
		AbstractBeanFactoryLifeCycle {

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		for (Class<?> clazz : ResourceUtils.getPackageScan().getClasses(
				getScanAnnotationPackageName())) {
			scanningAMQPConsumer(beanFactory, clazz);
		}
	}

	public String getScanAnnotationPackageName() {
		return BeanUtils.getScanAnnotationPackageName();
	}

	private void scanningAMQPConsumer(BeanFactory beanFactory, Class<?> clz)
			throws IOException {
		for (Method method : AnnotationUtils.getAnnoationMethods(clz, true,
				true, RabbitmqConsumerBind.class)) {
			RabbitmqConsumerBind consumerBind = method
					.getAnnotation(RabbitmqConsumerBind.class);
			RabbitmqMethodExchange exchange = (RabbitmqMethodExchange) (StringUtils
					.isEmpty(consumerBind.exchangeService()) ? beanFactory
					.getInstance(RabbitmqMethodExchange.class) : beanFactory
					.getInstance(consumerBind.exchangeService()));
			exchange.bindConsumer(
					consumerBind.routingKey(),
					consumerBind.queueName(),
					consumerBind.durable(),
					consumerBind.exclusive(),
					consumerBind.autoDelete(),
					beanFactory.getAop().getProxyMethod(beanFactory, clz,
							method, null));
		}
	}

	public void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory) {
	}
}
