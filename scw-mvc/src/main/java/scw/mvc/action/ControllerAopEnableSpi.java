package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;

import scw.beans.AopEnableSpi;
import scw.mvc.annotation.Controller;

public class ControllerAopEnableSpi implements AopEnableSpi {

	public boolean isAopEnable(Class<?> clazz, AnnotatedElement annotatedElement) {
		return clazz.getAnnotation(Controller.class) != null;
	}
}
