package scw.mvc;

import scw.core.annotation.AnnotationFactory;

public interface Action extends AnnotationFactory{
	String getController();
	
	Object doAction(Channel channel) throws Throwable;
}
