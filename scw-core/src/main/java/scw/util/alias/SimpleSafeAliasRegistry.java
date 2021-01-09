package scw.util.alias;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import scw.core.Assert;

public class SimpleSafeAliasRegistry implements AliasRegistry, Cloneable {
	private Map<String, Set<String>> aliasMap = Collections
			.synchronizedMap(new HashMap<String, Set<String>>());

	public void registerAlias(String name, String alias) {
		register(name, alias);
		register(alias, name);
	}
	
	private void register(String name, String alias){
		Assert.hasText(name, "'name' must not be empty");
		Assert.hasText(alias, "'alias' must not be empty");
		Set<String> names = aliasMap.get(name);
		if (names == null) {
			names = new HashSet<String>();
			names.add(alias);
			aliasMap.put(name, Collections.synchronizedSet(names));
		} else {
			if (names.contains(alias)) {
					throw new IllegalStateException("Cannot register alias '"
							+ alias + "' for name '" + name + "'");
			}
			names.add(alias);
		}
	}

	public void removeAlias(String alias) {
		Set<String> names = aliasMap.remove(alias);
		if(names == null){
			return ;
		}
		
		for(String name : names){
			Set<String> aliasNames = aliasMap.get(name);
			if(aliasNames != null){
				aliasNames.remove(alias);
			}
		}
	}

	public boolean isAlias(String name) {
		return aliasMap.containsKey(name);
	}

	public String[] getAliases(String name) {
		Set<String> names = aliasMap.get(name);
		if(names == null){
			return new String[0];
		}
		
		return names.toArray(new String[0]);
	}

	@Override
	public SimpleSafeAliasRegistry clone() {
		SimpleSafeAliasRegistry registry = new SimpleSafeAliasRegistry();
		for(Entry<String, Set<String>> entry : aliasMap.entrySet()){
			Set<String> names = new HashSet<String>();
			names.addAll(entry.getValue());
			registry.aliasMap.put(entry.getKey(), Collections.synchronizedSet(names));
		}
		return registry;
	}
}
