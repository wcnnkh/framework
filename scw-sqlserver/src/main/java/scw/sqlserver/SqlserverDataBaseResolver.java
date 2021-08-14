package scw.sqlserver;

import scw.core.utils.StringUtils;
import scw.db.DataBase;
import scw.db.DataBaseResolver;
import scw.lang.NotSupportedException;

public class SqlserverDataBaseResolver implements DataBaseResolver {

	@Override
	public DataBase resolve(String driverClassName, String url,
			String username, String password) {
		if (StringUtils.isEmpty(driverClassName) && StringUtils.isEmpty(url)) {
			throw new NotSupportedException("driverClassName和url至少要存在一个有效的参数");
		}

		if (StringUtils.isEmpty(driverClassName)) {// 没有驱动名，只能根据URL来判断
			if (url.startsWith("jdbc:microsoft:sqlserver:")) {
				return new SqlServerDataBase(driverClassName, url, username,
						password);
			}

		} else {// 根据驱动名称来判断
			if (driverClassName
					.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver")) {
				return new SqlServerDataBase(driverClassName, url, username,
						password);
			}
		}
		throw new NotSupportedException("不支持的数据库类型,driver=" + driverClassName
				+ ",url=" + url);
	}

}
