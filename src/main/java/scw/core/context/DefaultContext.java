package scw.core.context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.exception.AlreadyExistsException;

public class DefaultContext implements Context, ContextResource {
	private final DefaultContext parent;
	private boolean active;
	private Map<Object, Object> resourceMap;
	private LinkedList<ContextLifeCycle> contextLifeCycles;
	private boolean release;
	private boolean isNew;

	public DefaultContext(DefaultContext parent, boolean isNew, boolean active) {
		this.parent = parent;
		this.isNew = isNew;
		this.active = active;
	}

	public final void addContextLifeCycle(ContextLifeCycle contextLifeCycle) {
		if (contextLifeCycles == null) {
			contextLifeCycles = new LinkedList<ContextLifeCycle>();
		}

		contextLifeCycles.add(contextLifeCycle);
	}

	public final DefaultContext getParent() {
		return parent;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	protected Map<Object, Object> createResourceMap() {
		return new HashMap<Object, Object>();
	}

	public Object bindResource(Object name, Object value) {
		if (parent != null) {
			return parent.bindResource(name, value);
		}

		if (resourceMap == null) {
			createResourceMap();
		} else if (resourceMap.containsKey(name)) {
			throw new AlreadyExistsException("已经存在此事务资源了，不可以重复绑定：" + name);
		}

		resourceMap.put(name, value);
		return value;
	}

	public Object getResource(Object name) {
		if (parent != null) {
			return parent.getResource(name);
		}
		return resourceMap == null ? null : resourceMap.get(name);
	}

	public void after() {
		if (resourceMap != null) {
			for (Entry<Object, Object> entry : resourceMap.entrySet()) {
				Object resource = entry.getValue();
				if (resource == null) {
					continue;
				}

				if (resource instanceof ContextResource) {
					((ContextResource) resource).after();
				}
			}
		}

		if (contextLifeCycles != null) {
			for (ContextLifeCycle contextLifeCycle : contextLifeCycles) {
				contextLifeCycle.after(this);
			}
		}
	}

	public void error(Throwable e) {
		if (resourceMap != null) {
			for (Entry<Object, Object> entry : resourceMap.entrySet()) {
				Object resource = entry.getValue();
				if (resource == null) {
					continue;
				}

				if (resource instanceof ContextResource) {
					((ContextResource) resource).error(e);
				}
			}
		}

		if (contextLifeCycles != null) {
			for (ContextLifeCycle contextLifeCycle : contextLifeCycles) {
				contextLifeCycle.error(this, e);
			}
		}
	}

	public void release() {
		if (release) {
			throw new ContextException("Context destroyed(上下文已被销毁)");
		}

		if (resourceMap != null) {
			for (Entry<Object, Object> entry : resourceMap.entrySet()) {
				Object resource = entry.getValue();
				if (resource == null) {
					continue;
				}

				if (resource instanceof ContextResource) {
					((ContextResource) resource).release();
				}
			}
		}

		if (contextLifeCycles != null) {
			for (ContextLifeCycle contextLifeCycle : contextLifeCycles) {
				contextLifeCycle.release(this);
			}
		}
	}

	public final void setRelease(boolean release) {
		this.release = release;
	}

	public final boolean isRelease() {
		return release;
	}
}
