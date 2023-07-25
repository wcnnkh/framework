package io.basc.framework.oracle;

import io.basc.framework.db.Database;
import io.basc.framework.db.DataBaseResolver;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.StringUtils;

public class OracleDataBaseResolver implements DataBaseResolver {

	@Override
	public Database resolve(String driverClassName, String url,
			String username, String password) {
		if (StringUtils.isEmpty(driverClassName) && StringUtils.isEmpty(url)) {
			throw new UnsupportedException("driverClassName和url至少要存在一个有效的参数");
		}

		if (StringUtils.isEmpty(driverClassName)) {// 没有驱动名，只能根据URL来判断
			if (url.startsWith("jdbc:oracle:thin:")) {
				return new OracleDataBase(driverClassName, url, username,
						password);
			}

		} else {// 根据驱动名称来判断
			if (driverClassName.equals("oracle.jdbc.driver.OracleDriver")) {
				return new OracleDataBase(driverClassName, url, username,
						password);
			}
		}
		throw new UnsupportedException("不支持的数据库类型,driver=" + driverClassName
				+ ",url=" + url);
	}

}
