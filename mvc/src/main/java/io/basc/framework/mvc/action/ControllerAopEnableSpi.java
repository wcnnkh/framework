package io.basc.framework.mvc.action;

import io.basc.framework.beans.AopEnableSpi;
import io.basc.framework.mvc.annotation.Controller;

import java.lang.reflect.AnnotatedElement;

public class ControllerAopEnableSpi implements AopEnableSpi {

	public boolean isAopEnable(Class<?> clazz, AnnotatedElement annotatedElement) {
		return clazz.getAnnotation(Controller.class) != null;
	}
}
