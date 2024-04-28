package io.basc.framework.mapper.support;

import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.mapper.Named;
import io.basc.framework.util.DisposableRegistration;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(of = "name")
@ToString
public class StandardNamed implements Named {
	private String name;
	private Set<String> aliasNames;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Elements<String> getAliasNames() {
		return aliasNames == null ? Elements.empty() : Elements.of(aliasNames);
	}

	public Registration unregisterAliasName(String aliasName) {
		if (aliasNames == null) {
			return Registration.EMPTY;
		}

		if (aliasNames.remove(aliasName)) {
			return DisposableRegistration.of(() -> registerAliasName(aliasName));
		}
		return Registration.EMPTY;
	}

	public Registration registerAliasName(String aliasName) {
		if (aliasNames == null) {
			aliasNames = new LinkedHashSet<>();
		}

		if (aliasNames.add(aliasName)) {
			return DisposableRegistration.of(() -> unregisterAliasName(aliasName));
		}
		return Registration.EMPTY;
	}

	public void setAliasNames(Elements<String> aliasNames) {
		this.aliasNames = aliasNames == null ? null : aliasNames.toSet();
	}
}
