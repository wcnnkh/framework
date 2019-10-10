package scw.core.context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DefaultContext implements Context, ContextLifeCycle {
	private Map<Object, Object> resourceMap;
	private LinkedList<ContextLifeCycle> contextLifeCycles;

	public Object getResource(Object name) {
		return resourceMap == null ? null : resourceMap.get(name);
	}

	public Object bindResource(Object name, Object value) {
		if (resourceMap == null) {
			resourceMap = new HashMap<Object, Object>(8);
		}

		return resourceMap.put(name, value);
	}

	public void lifeCycle(ContextLifeCycle lifeCycle) {
		if (contextLifeCycles == null) {
			contextLifeCycles = new LinkedList<ContextLifeCycle>();
		}

		contextLifeCycles.add(lifeCycle);
	}

	public void release() {
		if (contextLifeCycles == null) {
			return;
		}

		for (ContextLifeCycle contextLifeCycle : contextLifeCycles) {
			contextLifeCycle.release();
		}
	}
}
