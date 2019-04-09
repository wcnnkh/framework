package scw.database;

import scw.sql.orm.ORMUtils;

/**
 * 此类已弃用，请不要使用
 * @author shuchaowen
 *
 */
@Deprecated
public final class DataBaseUtils {
	private DataBaseUtils() {
	};

	/**
	 * ORMUtils.registerCglibProxyTableBean
	 * @param pageName
	 */
	@Deprecated
	public static void registerCglibProxyTableBean(String pageName) {
		ORMUtils.registerCglibProxyTableBean(pageName);
	}

	public static String getLikeValue(String likeValue) {
		if (likeValue == null || likeValue.length() == 0) {
			return "%";// 注意：这会忽略空
		}

		return "%" + likeValue + "%";
	}
}
