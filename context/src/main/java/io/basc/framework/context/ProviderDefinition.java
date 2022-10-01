package io.basc.framework.context;

import java.util.Collection;
import java.util.Collections;

public class ProviderDefinition {
	private Collection<Class<?>> names;
	private int order;
	private boolean assignable;
	private Collection<Class<?>> excludes;

	public Collection<Class<?>> getNames() {
		return names;
	}

	public void setNames(Collection<Class<?>> names) {
		this.names = names;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isAssignable() {
		return assignable;
	}

	public void setAssignable(boolean assignable) {
		this.assignable = assignable;
	}

	public Collection<Class<?>> getExcludes() {
		return excludes == null ? Collections.emptyList() : excludes;
	}

	public void setExcludes(Collection<Class<?>> excludes) {
		this.excludes = excludes;
	}
}
