package scw.mvc.action;

import scw.core.parameter.ParameterConfig;
import scw.core.reflect.AnnotationFactory;
import scw.mvc.Channel;

public interface Action extends AnnotationFactory{
	Object doAction(Channel channel) throws Throwable;
	
	ParameterConfig[] getParameterConfigs();
}
