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
import scw.mq.amqp.ParametersExchange;

public final class AnnotationMQUtils {
	private AnnotationMQUtils() {
	};

	public static void scanningAMQPParamsConsumer(BeanFactory beanFactory, Collection<Class<?>> classes,
			String[] rootFilters) {
		for (Class<?> clz : classes) {
			for (Method method : AnnotationUtils.getAnnoationMethods(clz, true, true, AmqpParametersConsumer.class)) {
				AmqpParametersConsumer c = method.getAnnotation(AmqpParametersConsumer.class);
				ParametersExchange mq = beanFactory.get(c.paramsExchange());
				LoggerUtils.info(AnnotationMQUtils.class,
						"添加消费者：{}, amqp routingKey={}, queueName={}, durable={}, exclusive={}, autoDelete={}, clz={}, method={}",
						c.routingKey(), c.queueName(), c.durable(), c.exclusive(), c.autoDelete(), clz.getName(),
						method);
				mq.addConsumer(c.routingKey(), c.queueName(), c.durable(), c.exclusive(), c.autoDelete(),
						new MqMethodConsumer(new MethodProxyInvoker(beanFactory, clz, method, rootFilters)));
			}
		}
	}
}

class MqMethodConsumer implements Consumer<Parameters> {
	private Invoker invoker;

	public MqMethodConsumer(Invoker invoker) {
		this.invoker = invoker;
	}

	public void consume(Parameters message) {
		if (message == null) {
			return;
		}

		try {
			invoker.invoke(message.getParameters());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
