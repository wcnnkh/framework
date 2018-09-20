package shuchaowen.core.db;

/**
 * 不调用get和set方法
 * @author shuchaowen
 *
 */
public class DefaultColumnFormat implements ColumnFormat {

	public Object get(Object obj, ColumnInfo columnInfo) throws Throwable {
		Object v = columnInfo.getFieldInfo().forceGet(obj);
		if (boolean.class == columnInfo.getFieldInfo().getField().getType()) {
			boolean b = v == null ? false : (Boolean) v;
			return b ? 1 : 0;
		}

		if (Boolean.class == columnInfo.getFieldInfo().getField().getType()) {
			if (v == null) {
				return null;
			}
			return (Boolean) v ? 1 : 0;
		}
		return v;

	}

	public void set(Object obj, ColumnInfo columnInfo, Object value) throws Throwable {
		columnInfo.getFieldInfo().forceSet(obj, value);
	}
}
