package io.basc.framework.beans;

import io.basc.framework.factory.InstanceException;

public class BeansException extends InstanceException{
	private static final long serialVersionUID = 1L;
	
	public BeansException(String message){
		super(message);
	}
	
	public BeansException(Throwable e){
		super(e);
	}
	
	public BeansException(String message, Throwable e){
		super(message, e);
	}
}
