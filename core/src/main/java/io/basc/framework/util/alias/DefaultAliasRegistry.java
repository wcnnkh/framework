package io.basc.framework.util.alias;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.util.Assert;

public class DefaultAliasRegistry implements AliasRegistry, Cloneable {
	private final Map<String, Set<String>> aliasMap;

	public DefaultAliasRegistry() {
		this(new ConcurrentHashMap<>());
	}

	public DefaultAliasRegistry(Map<String, Set<String>> aliasMap) {
		Assert.requiredArgument(aliasMap != null, "aliasMap");
		this.aliasMap = aliasMap;
	}

	public void registerAlias(String name, String alias) {
		register(name, alias);
		register(alias, name);
	}

	private void register(String name, String alias) {
		Assert.hasText(name, "'name' must not be empty");
		Assert.hasText(alias, "'alias' must not be empty");
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

	public void removeAlias(String alias) {
		Set<String> names = aliasMap.remove(alias);
		if (names == null) {
			return;
		}

		for (String name : names) {
			Set<String> aliasNames = aliasMap.get(name);
			if (aliasNames != null) {
				aliasNames.remove(alias);
			}
		}
	}

	public boolean isAlias(String name) {
		return aliasMap.containsKey(name);
	}

	public boolean hasAlias(String name, String alias) {
		Set<String> aliases = aliasMap.get(name);
		if (aliases == null) {
			return false;
		}

		return aliases.contains(alias);
	}

	public String[] getAliases(String name) {
		Set<String> names = aliasMap.get(name);
		if (names == null) {
			return new String[0];
		}

		return names.toArray(new String[0]);
	}
}
