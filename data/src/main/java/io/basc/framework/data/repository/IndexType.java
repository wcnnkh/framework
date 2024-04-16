package io.basc.framework.data.repository;

import io.basc.framework.util.IgnoreCaseSymbol;

public class IndexType extends IgnoreCaseSymbol {
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_NAME = "DEFAULT";

	/**
	 * 默认索引
	 */
	public static final IndexType DEFAULT = new IndexType(DEFAULT_NAME);

	public static final String UNIQUE_NAME = "UNIQUE";
	/**
	 * 唯一索引
	 */
	public static final IndexType UNIQUE = new IndexType(UNIQUE_NAME);

	public static final String FULLTEXT_NAME = "FULLTEXT";
	/**
	 * 全文索引
	 */
	public static final IndexType FULLTEXT = new IndexType(FULLTEXT_NAME);

	public static final String SPATIAL_NAME = "SPATIAL";
	/**
	 * 空间索引
	 */
	public static final IndexType SPATIAL = new IndexType(SPATIAL_NAME);

	public IndexType(String name) {
		super(name);
	}

	public static IndexType forName(String name) {
		return forName(name, IndexType.class, () -> new IndexType(name));
	}
}
