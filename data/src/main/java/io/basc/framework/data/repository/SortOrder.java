package io.basc.framework.data.repository;

import io.basc.framework.util.IgnoreCaseSymbol;

public class SortOrder extends IgnoreCaseSymbol {
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_NAME = "DEFAULT";

	public static final SortOrder DEFAULT = new SortOrder(DEFAULT_NAME);

	public static final String ASC_NAME = "ASC";

	/**
	 * 正序, 一般默认都是此值
	 */
	public static final SortOrder ASC = new SortOrder(ASC_NAME);

	public static final String DESC_NAME = "DESC";

	/**
	 * 倒序
	 */
	public static final SortOrder DESC = new SortOrder(DESC_NAME);

	public SortOrder(String name) {
		super(name);
	}

	public static SortOrder forName(String name) {
		return IgnoreCaseSymbol.forName(name, SortOrder.class, () -> new SortOrder(name));
	}
}
