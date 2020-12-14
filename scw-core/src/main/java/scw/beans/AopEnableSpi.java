package scw.beans;

import java.lang.reflect.AnnotatedElement;

public interface AopEnableSpi {
	boolean isAopEnable(Class<?> clazz, AnnotatedElement annotatedElement);
}
