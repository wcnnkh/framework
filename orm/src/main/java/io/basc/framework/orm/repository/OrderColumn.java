package io.basc.framework.orm.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.basc.framework.util.comparator.Sort;
import lombok.Data;

/**
 * 排序
 * 
 * @author wcnnkh
 *
 */
@Data
public class OrderColumn implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final Sort sort;
	private final List<OrderColumn> withOrders;

	public OrderColumn(String name, Sort sort, List<OrderColumn> withOrders) {
		this.name = name;
		this.sort = sort;
		this.withOrders = withOrders == null ? Collections.emptyList() : Collections.unmodifiableList(withOrders);
	}

	public String getName() {
		return name;
	}

	public Sort getSort() {
		return sort;
	}

	public List<OrderColumn> getWithOrders() {
		return withOrders;
	}
}
