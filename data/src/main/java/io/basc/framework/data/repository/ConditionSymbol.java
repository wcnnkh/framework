package io.basc.framework.data.repository;

import io.basc.framework.util.collection.Elements;

/**
 * 条件符号
 * 
 * @author wcnnkh
 *
 */
public class ConditionSymbol extends RepositorySymbol {
	private static final long serialVersionUID = 1L;

	/**
	 * 前缀匹配
	 */
	public static final ConditionSymbol STARTS_WITH = new ConditionSymbol("startsWith");
	/**
	 * 后缀匹配
	 */
	public static final ConditionSymbol ENDS_WITH = new ConditionSymbol("endsWith");
	/**
	 * 相等
	 */
	public static final ConditionSymbol EQU = new ConditionSymbol("Equ");
	/**
	 * 大于或等于
	 */
	public static final ConditionSymbol GEQ = new ConditionSymbol("Geq");
	/**
	 * 大于
	 */
	public static final ConditionSymbol GTR = new ConditionSymbol("Gtr");
	/**
	 * 在某某之内
	 */
	public static final ConditionSymbol IN = new ConditionSymbol("In");
	/**
	 * 小于或等于
	 */
	public static final ConditionSymbol LEQ = new ConditionSymbol("Leq");
	/**
	 * 包含
	 */
	public static final ConditionSymbol CONTAINS = new ConditionSymbol("contains");
	/**
	 * 小于
	 */
	public static final ConditionSymbol LSS = new ConditionSymbol("Lss");
	/**
	 * 不等于
	 */
	public static final ConditionSymbol NEQ = new ConditionSymbol("Neq");

	/**
	 * 搜索
	 */
	public static final ConditionSymbol SEARCH = new ConditionSymbol("Search");

	public static Elements<ConditionSymbol> getConditionSymbols() {
		return getSymbols(ConditionSymbol.class);
	}

	public ConditionSymbol(String name) {
		super(name);
	}

	public static Elements<ConditionSymbol> getConditionSymbols(String name) {
		return getSymbols(ConditionSymbol.class, name);
	}
}
