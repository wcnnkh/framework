package io.basc.framework.util.alias;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public class DefaultAliasRegistry implements AliasRegistry, Cloneable {
	private final Map<String, Set<String>> aliasMap;
	private AliasFactory parentAliasFactory;

	public DefaultAliasRegistry() {
		this(new ConcurrentHashMap<>());
	}

	public DefaultAliasRegistry(Map<String, Set<String>> aliasMap) {
		Assert.requiredArgument(aliasMap != null, "aliasMap");
		this.aliasMap = aliasMap;
	}

	public AliasFactory getParentAliasFactory() {
		return parentAliasFactory;
	}

	public void setParentAliasFactory(AliasFactory parentAliasFactory) {
		this.parentAliasFactory = parentAliasFactory;
	}

	public void registerAlias(String name, String alias) {
		Assert.hasText(name, "'name' must not be empty");
		Assert.hasText(alias, "'alias' must not be empty");
		Assert.requiredArgument(!hasAlias(name, alias), "name[" + name + "] -> alias[" + alias + "]");
		register(name, alias);
		register(alias, name);
	}

	private void register(String name, String alias) {
		Set<String> names = aliasMap.get(name);
		if (names == null) {
			names = Collections.synchronizedSet(new HashSet<>());
			Set<String> sets = aliasMap.putIfAbsent(name, names);
			if (sets != null) {
				names = sets;
			}
		} else {
			if (names.contains(alias)) {
				throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" + name + "'");
			}
		}
		names.add(alias);
	}

	public final boolean isAlias(String name) {
		return isAlias(name, getParentAliasFactory());
	}

	public boolean isAlias(String name, AliasFactory parent) {
		return aliasMap.containsKey(name) || (parent != null && parent.isAlias(name));
	}

	public final boolean hasAlias(String name, String alias) {
		return hasAlias(name, alias, getParentAliasFactory());
	}

	public boolean hasAlias(String name, String alias, AliasFactory parent) {
		Set<String> aliases = aliasMap.get(name);
		if (aliases == null) {
			return parent == null ? false : parent.hasAlias(name, alias);
		}

		return aliases.contains(alias) || (parent != null && parent.hasAlias(name, alias));
	}

	public final String[] getAliases(String name) {
		return getAliases(name, getParentAliasFactory());
	}

	public String[] getAliases(String name, AliasFactory parent) {
		Set<String> names = aliasMap.get(name);
		if (names == null) {
			return parent == null ? StringUtils.EMPTY_ARRAY : parent.getAliases(name);
		}

		String[] array = names.toArray(new String[0]);
		return parent == null ? array : ArrayUtils.merge(array, parent.getAliases(name));
	}
}
