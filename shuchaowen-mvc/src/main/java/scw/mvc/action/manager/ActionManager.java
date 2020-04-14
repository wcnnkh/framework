package scw.mvc.action.manager;

import java.util.Collection;

import scw.mvc.action.Action;

public interface ActionManager extends ActionLookup {
	Collection<Action> getActions();
}
