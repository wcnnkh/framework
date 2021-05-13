package scw.search.location;

import scw.util.comparator.OrderBy;

/**
 * 查询附近的点所需的参数
 * @author shuchaowen
 *
 */
public class QueryNearby extends Location {
	private static final long serialVersionUID = 1L;
	/**
	 * 范围(米)
	 */
	private int range;
	/**
	 * 范围单位
	 */
	private DistanceUnit rangeUnit;
	/**
	 * 数量
	 */
	private int count;
	private OrderBy orderBy;

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public DistanceUnit getRangeUnit() {
		return rangeUnit;
	}

	public void setRangeUnit(DistanceUnit rangeUnit) {
		this.rangeUnit = rangeUnit;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public OrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}
}
