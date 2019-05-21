package scw.db.cache;

import scw.db.DataManager;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public abstract class LazyDataManager implements DataManager {
	private static final String DEFAULT_KEY_PREFIX = "lazy:";
	private static final String DEFAULT_CONNECTOR = "|";

	private final int exp;

	public LazyDataManager(int exp) {
		this.exp = exp;
	}

	public final int getExp() {
		return exp;
	}

	public String getObjectKey(TableInfo tableInfo, Object bean) {
		ColumnInfo[] cs = tableInfo.getPrimaryKeyColumns();
		StringBuilder sb = new StringBuilder(128);
		sb.append(DEFAULT_KEY_PREFIX);
		sb.append(tableInfo.getSource().getName());
		sb.append(DEFAULT_CONNECTOR).append(cs.length);
		Object v;
		String value;
		try {
			for (int i = 0; i < cs.length; i++) {
				sb.append(DEFAULT_CONNECTOR);
				v = cs[i].getField().get(bean);
				value = v == null ? null : v.toString();
				sb.append(value == null ? 0 : value.length());
				sb.append(DEFAULT_CONNECTOR);
				sb.append(value);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public String getObjectKeyById(Class<?> clazz, Object... params) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(DEFAULT_KEY_PREFIX).append(clazz.getName());
		sb.append(DEFAULT_CONNECTOR).append(params.length);
		for (int i = 0; i < params.length; i++) {
			sb.append(DEFAULT_CONNECTOR);
			Object v = params[i];
			String value = v == null ? null : v.toString();
			sb.append(value == null ? 0 : value.length());
			sb.append(DEFAULT_CONNECTOR);
			sb.append(value);
		}
		return sb.toString();
	}

	public String appendObjectKey(String key, Object value) {
		String v = value == null ? null : value.toString();
		int len = v == null ? 0 : v.length();
		StringBuilder sb = new StringBuilder(key.length() + len + 10);
		sb.append(key);
		sb.append(DEFAULT_CONNECTOR);
		sb.append(len);
		sb.append(DEFAULT_CONNECTOR);
		sb.append(v);
		return sb.toString();
	}
}
