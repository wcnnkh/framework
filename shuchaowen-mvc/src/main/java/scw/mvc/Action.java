package scw.mvc;

import scw.core.annotation.AnnotationFactory;
import scw.core.parameter.ParameterConfig;

public interface Action extends AnnotationFactory{
	String getController();
	
	Object doAction(Channel channel) throws Throwable;
	
	ParameterConfig[] getParameterConfigs();
}
