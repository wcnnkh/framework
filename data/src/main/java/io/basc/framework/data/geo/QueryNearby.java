package io.basc.framework.data.geo;

import io.basc.framework.util.comparator.Sort;

/**
 * 查询附近的点所需的参数
 * 
 * @author shuchaowen
 *
 */
public class QueryNearby extends Point {
	private static final long serialVersionUID = 1L;
	private Distance distance;
	/**
	 * 数量
	 */
	private int count;
	private Sort orderBy;

	public QueryNearby(double x, double y) {
		super(x, y);
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Sort getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Sort orderBy) {
		this.orderBy = orderBy;
	}
}
