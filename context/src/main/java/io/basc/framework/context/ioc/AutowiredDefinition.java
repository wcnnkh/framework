package io.basc.framework.context.ioc;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

public class AutowiredDefinition extends IocDefinition {
	private Collection<String> names;
	private boolean required;

	public Collection<String> getNames() {
		return names == null ? Collections.emptyList() : names;
	}

	public void setNames(Collection<String> names) {
		this.names = CollectionUtils.isEmpty(names) ? names
				: names.stream().filter((e) -> StringUtils.isNotEmpty(e)).distinct().collect(Collectors.toList());
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
