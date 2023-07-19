package io.basc.framework.mapper.support;

import io.basc.framework.mapper.Element;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Setter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;
import lombok.ToString;

@ToString
public class DefaultField implements Element {
	private final String name;
	private volatile Elements<String> aliasNames;
	private Elements<? extends Getter> getters;
	private Elements<? extends Setter> setters;

	public DefaultField(String name) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		this.name = name;
	}

	public DefaultField(Element field) {
		Assert.requiredArgument(field != null, "field");
		this.name = field.getName();
		this.aliasNames = field.getAliasNames();
		this.getters = field.getGetters();
		this.setters = field.getSetters();
	}

	@Override
	public Elements<String> getAliasNames() {
		if (aliasNames == null) {
			synchronized (this) {
				if (aliasNames == null) {
					this.aliasNames = Element.super.getAliasNames();
				}
			}
		}
		return aliasNames;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Elements<? extends Getter> getGetters() {
		return getters == null ? Elements.empty() : getters;
	}

	@Override
	public Elements<? extends Setter> getSetters() {
		return setters == null ? Elements.empty() : setters;
	}

	public void setGetters(Elements<? extends Getter> getters) {
		this.getters = getters;
	}

	public void setSetters(Elements<? extends Setter> setters) {
		this.setters = setters;
	}

	public void setAliasNames(Elements<String> aliasNames) {
		this.aliasNames = aliasNames;
	}
}
