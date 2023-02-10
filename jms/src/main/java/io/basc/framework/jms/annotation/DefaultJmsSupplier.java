package io.basc.framework.jms.annotation;

import java.lang.reflect.Method;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;

import io.basc.framework.jms.JmsSupplier;

public class DefaultJmsSupplier implements JmsSupplier {
	private ConnectionFactory connectionFactory;

	public DefaultJmsSupplier(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public <T> MessageConsumer getMessageConsumer(Class<? extends T> clazz) {
		io.basc.framework.jms.annotation.MessageConsumer consumer = clazz
				.getAnnotation(io.basc.framework.jms.annotation.MessageConsumer.class);
		if(consumer == null) {
			return null;
		}
		
		connectionFactory.createConnection().createConnectionConsumer(null, null, null, 0)

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageConsumer getMessageConsumer(Class<?> clazz, Method method) {
		// TODO Auto-generated method stub
		return null;
	}

}
