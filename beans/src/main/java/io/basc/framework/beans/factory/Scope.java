package io.basc.framework.beans.factory;

import io.basc.framework.util.Symbol;
import io.basc.framework.util.collection.Elements;

/**
 * bean的作用域
 * 
 * @author P004149
 *
 */
public class Scope extends Symbol {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认作用域名
	 */
	public static final String DEFAULT_SCOPE_NAME = "default";

	/**
	 * 默认的作用域
	 */
	public static final Scope DEFAULT = new Scope(DEFAULT_SCOPE_NAME);

	public Scope(String name) {
		super(name);
	}

	public static Elements<Scope> getScopes() {
		return getSymbols(Scope.class);
	}

	public static Scope getUniqueScope(String name) {
		return getFirstOrCreate(name, Scope.class, () -> new Scope(name));
	}
}
