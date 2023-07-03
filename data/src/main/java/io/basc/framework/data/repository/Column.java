package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 列
 * 
 * @author wcnnkh
 *
 */
@AllArgsConstructor
@Data
public class Column implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 这个字段所属的repository
	 */
	@Nullable
	private final Repository repository;
	/**
	 * 字段参数
	 */
	private final Parameter parameter;

	@Nullable
	private final String aliasName;

	public Column(Repository repository, String name) {
		this(repository, new Parameter(name));
	}

	public Column(Repository repository, Parameter parameter) {
		this(repository, parameter, null);
	}
}
