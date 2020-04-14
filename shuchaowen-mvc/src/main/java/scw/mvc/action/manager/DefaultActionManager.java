package scw.mvc.action.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.action.Action;

public class DefaultActionManager extends MultiActionLookup implements
		ActionManager {
	private static final long serialVersionUID = 1L;
	protected transient final Logger logger = LoggerUtils.getLogger(getClass());
	private LinkedList<Action> actions = new LinkedList<Action>();

	@Override
	public void register(Action action) {
		actions.add(action);
		super.register(action);
	}

	public Collection<Action> getActions() {
		return Collections.unmodifiableCollection(actions);
	}
}
