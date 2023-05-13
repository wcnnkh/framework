package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Named;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Repository implements Serializable, Named {
	private static final long serialVersionUID = 1L;
	/**
	 * 和上一个存储库之前的join关系
	 */
	@Nullable
	private final JoinSymbol joinSymbol;
	/**
	 * 名称
	 */
	@NonNull
	private final String name;
	/**
	 * 别名
	 */
	@Nullable
	private final String aliasName;
	/**
	 * join的条件
	 */
	@Nullable
	private final Elements<? extends Condition> joinConditions;

	public Repository(String name) {
		this(null, name, null, null);
	}
}
