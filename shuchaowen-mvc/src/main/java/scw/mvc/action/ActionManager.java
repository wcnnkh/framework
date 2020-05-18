package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Collection;

public interface ActionManager {
	Action getAction(Class<?> clazz, Method method);

	Collection<Action> getActions();
}
