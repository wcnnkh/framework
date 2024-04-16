package io.basc.framework.data.repository;

import io.basc.framework.util.IgnoreCaseSymbol;

public class IndexMethod extends IgnoreCaseSymbol {
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_NAME = "DEFAULT";

	public static final IndexMethod DEFAULT = new IndexMethod("DEFAULT");

	public static final String BTREE_NAME = "BTREE";

	/**
	 * 正序, 一般默认都是此值
	 */
	public static final IndexMethod BTREE = new IndexMethod(BTREE_NAME);

	public static final String HASH_NAME = "HASH";

	/**
	 * 倒序
	 */
	public static final IndexMethod HASH = new IndexMethod("HASH");

	public IndexMethod(String name) {
		super(name);
	}

	public static IndexMethod forName(String name) {
		return forName(name, IndexMethod.class, () -> new IndexMethod(name));
	}
}
