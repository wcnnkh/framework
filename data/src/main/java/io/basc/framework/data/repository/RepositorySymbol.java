package io.basc.framework.data.repository;

import io.basc.framework.util.Symbol;
import io.basc.framework.util.element.Elements;

/**
 * 存储相关的符号
 * 
 * @author wcnnkh
 *
 */
public class RepositorySymbol extends Symbol {
	private static final long serialVersionUID = 1L;

	public RepositorySymbol(String name) {
		super(name);
	}

	public static Elements<RepositorySymbol> getRepositorySymbols() {
		return getSymbols(RepositorySymbol.class);
	}
}
