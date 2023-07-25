package io.basc.framework.jms;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import io.basc.framework.util.function.Source;
import io.basc.framework.util.function.StandardStreamOperations;
import io.basc.framework.util.registry.Registration;

public abstract class AbstractJmsOperations<T, C extends AbstractJmsOperations<T, C>>
		extends StandardStreamOperations<T, JMSException, C> implements JmsOperations {

	private String defaultMessageSelector;

	private Boolean noLocal;

	public AbstractJmsOperations(Source<? extends T, ? extends JMSException> source) {
		super(source);
	}

	public void copyConfig(AbstractJmsOperations<?, ?> source) {
		this.defaultMessageSelector = source.defaultMessageSelector;
		this.noLocal = source.noLocal;
	}

	public String getDefaultMessageSelector() {
		return defaultMessageSelector;
	}

	public void setDefaultMessageSelector(String defaultMessageSelector) {
		this.defaultMessageSelector = defaultMessageSelector;
	}

	public Boolean getNoLocal() {
		return noLocal;
	}

	/**
	 * 只有topic才支持此参数
	 * 
	 * @param noLocal if true, and the destination is a topic, then the
	 *                {@code MessageConsumer} will not receive messages published to
	 *                the topic by its own connection.
	 */
	public void setNoLocal(Boolean noLocal) {
		this.noLocal = noLocal;
	}

	@Override
	public Registration bind(MessageListener messageListener) throws JMSException {
		return bind(getDefaultMessageSelector(), messageListener);
	}
}
