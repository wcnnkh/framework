package scw.beans.event;

import scw.beans.BeanFactory;
import scw.event.EventObject;

public abstract class BeanEvent extends EventObject{
	private static final long serialVersionUID = 1L;
	private transient final BeanFactory beanFactory;
	
	public BeanEvent(Object source, BeanFactory beanFactory) {
		super(source);
		this.beanFactory = beanFactory;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
