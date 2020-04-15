package scw.mvc.action.manager;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.mvc.action.Action;

public interface ActionManager {
	Action getAction(Class<?> clazz, Method method);

	Collection<Action> getActions();
}
