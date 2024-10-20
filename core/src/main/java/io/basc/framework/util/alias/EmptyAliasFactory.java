package io.basc.framework.util.alias;

import io.basc.framework.util.StringUtils;

public class EmptyAliasFactory implements AliasFactory {

	public static final EmptyAliasFactory INSTANCE = new EmptyAliasFactory();

	@Override
	public boolean isAlias(String name) {
		return false;
	}

	@Override
	public boolean hasAlias(String name, String alias) {
		return false;
	}

	@Override
	public String[] getAliases(String name) {
		return StringUtils.EMPTY_ARRAY;
	}

}
