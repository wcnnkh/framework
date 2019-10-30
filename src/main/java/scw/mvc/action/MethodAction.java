package scw.mvc.action;

import java.lang.reflect.Method;

import scw.core.parameter.ParameterConfig;
import scw.core.reflect.AnnotationFactory;
import scw.mvc.Channel;

public interface MethodAction extends Action<Channel>, AnnotationFactory{
	Class<?> getTargetClass();
	
	Method getMethod();
	
	ParameterConfig[] getParameterConfigs();
}
