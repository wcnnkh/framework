package io.basc.framework.data.repository;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 排序
 * 
 * @author wcnnkh
 *
 */
@Data
@AllArgsConstructor
public class Sort implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Expression expression;
	private final SortOrder order;
}
