package scw.mvc;

import scw.core.annotation.AnnotationFactory;
import scw.core.parameter.ParameterConfig;

public interface Action extends AnnotationFactory{
	Object doAction(Channel channel) throws Throwable;
	
	ParameterConfig[] getParameterConfigs();
}
