package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface ActionManager {
	Action getAction(Class<?> clazz, Method method);

	Collection<Action> getActions();
}
