package io.basc.framework.messageing.handler.boot;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.annotation.Component;
import io.basc.framework.beans.factory.config.ConfigurableBeanFactory;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.messageing.MessageHandler;
import io.basc.framework.messageing.SubscribableChannel;
import io.basc.framework.messageing.handler.ExecutorMessageHandler;
import io.basc.framework.messageing.handler.HandleMessageConverter;
import io.basc.framework.messageing.handler.HandleMessageConverters;
import io.basc.framework.messageing.handler.converter.DefaultHandleMessageConverters;
import io.basc.framework.util.StringUtils;

/**
 * 自动绑定
 * 
 * @author wcnnkh
 *
 */
@Component
class AutomaticSubscribeChannelPostProcessor implements ApplicationPostProcessor {

	private void subscribeBean(ConfigurableBeanFactory beanFactory,
			HandleMessageConverter defaultHandleMessageConverter, String beanName, Class<?> beanType) {
		MessageListener messageListener = beanType.getAnnotation(MessageListener.class);
		if (messageListener == null) {
			return;
		}

		MessageHandler messageHandler = beanFactory.getBean(beanName, MessageHandler.class);
		SubscribableChannel subscribableChannel = beanFactory.getBean(messageListener.channel(),
				SubscribableChannel.class);
		subscribableChannel.subscribe(messageHandler);
	}

	private void subscribeMethod(ConfigurableBeanFactory beanFactory,
			HandleMessageConverter defaultHandleMessageConverter, String beanName, Class<?> beanType, Method method) {
		MessageListener messageListener = method.getAnnotation(MessageListener.class);
		if (messageListener == null) {
			return;
		}

		SubscribableChannel subscribableChannel = beanFactory.getBean(messageListener.channel(),
				SubscribableChannel.class);
		HandleMessageConverter handleMessageConverter = StringUtils.isEmpty(messageListener.handleMessageConverter())
				? defaultHandleMessageConverter
				: beanFactory.getBean(messageListener.handleMessageConverter(), HandleMessageConverter.class);

		Object bean = beanFactory.getBean(beanName);
		ReflectionMethod methodExecutor = new ReflectionMethod(method,
				TypeDescriptor.valueOf(beanType));
		methodExecutor.setTarget(bean);
		ExecutorMessageHandler executorMessageHandler = new ExecutorMessageHandler(methodExecutor,
				handleMessageConverter);
		subscribableChannel.subscribe(executorMessageHandler);
	}

	@Override
	public void postProcessApplication(ConfigurableApplication beanFactory) throws Throwable {
		HandleMessageConverters handleMessageConverters = new DefaultHandleMessageConverters();
		handleMessageConverters.configure(beanFactory);

		for (String beanName : beanFactory.getBeanNames()) {
			Class<?> beanType = beanFactory.getType(beanName);
			if (MessageHandler.class.isAssignableFrom(beanType)) {
				subscribeBean(beanFactory, handleMessageConverters, beanName, beanType);
			}

			for (Method method : beanType.getDeclaredMethods()) {
				subscribeMethod(beanFactory, handleMessageConverters, beanName, beanType, method);
			}
		}
	}
}
