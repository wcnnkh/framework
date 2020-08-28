package scw.context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

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
		if(!isNew){
			parent.addContextLifeCycle(contextLifeCycle);
			return ;
		}
		
		if (contextLifeCycles == null) {
			contextLifeCycles = new LinkedList<ContextLifeCycle>();
		}

		contextLifeCycles.add(contextLifeCycle);
	}

	public final DefaultContext getParent() {
		return parent;
	}

	public final boolean isNew() {
		return isNew;
	}

	public final void setActive(boolean active) {
		this.active = active;
	}

	public final boolean isActive() {
		return active;
	}

	protected Map<Object, Object> createResourceMap() {
		return new HashMap<Object, Object>();
	}

	public final Object bindResource(Object name, Object value) {
		if (!isNew) {
			return parent.bindResource(name, value);
		}
		
		if(value == this){
			throw new ContextException("不能将当前Context绑定到resource中:" + name);
		}

		if (resourceMap == null) {
			resourceMap = createResourceMap();
		} else if (resourceMap.containsKey(name)) {
			throw new ContextException("上下文中已经存在此资源了，不可以重复绑定：" + name);
		}

		resourceMap.put(name, value);
		return value;
	}

	public final Object getResource(Object name) {
		if (!isNew) {
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
		
		//release = true;
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
