package scw.mvc.action;

import java.lang.reflect.Method;

public interface MethodAction extends Action{
	Class<?> getTargetClass();
	
	Method getMethod();
}
