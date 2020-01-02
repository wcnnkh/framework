package scw.mvc;

import scw.core.parameter.ParameterConfig;
import scw.core.reflect.AnnotationFactory;

public interface Action extends AnnotationFactory{
	Object doAction(Channel channel) throws Throwable;
	
	ParameterConfig[] getParameterConfigs();
}
