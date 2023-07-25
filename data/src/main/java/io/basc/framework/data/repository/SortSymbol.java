package io.basc.framework.data.repository;

import io.basc.framework.util.Symbol;
import io.basc.framework.util.element.Elements;

public class SortSymbol extends Symbol {
	private static final long serialVersionUID = 1L;

	/**
	 * 正序, 一般默认都是此值
	 */
	public static final SortSymbol ASC = new SortSymbol("ASC");

	/**
	 * 倒序
	 */
	public static final SortSymbol DESC = new SortSymbol("DESC");

	public SortSymbol(String name) {
		super(name);
	}

	public static Elements<SortSymbol> getSortSymbols() {
		return getSymbols(SortSymbol.class);
	}

	public static Elements<SortSymbol> getSortSymbols(String name) {
		return getSymbols(SortSymbol.class, name);
	}
}
