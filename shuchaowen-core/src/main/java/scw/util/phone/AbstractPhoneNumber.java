package scw.util.phone;

import java.io.Serializable;

import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;

public abstract class AbstractPhoneNumber implements PhoneNumber, Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 默认的区号 86代表中国
	 */
	static final String DEFAULT_AREA_CODE = StringUtils
			.toString(SystemPropertyUtils.getProperty("default.phone.number.area.code"), "86");
	/**
	 * 默认的区号和号码的连接符
	 */
	static final String DEFAULT_CONNECTOR = StringUtils
			.toString(SystemPropertyUtils.getProperty("default.phone.number.connector"), "-");;

	/**
	 * 获取区号和号码的连接符
	 * 
	 * @return
	 */
	public abstract String getConnector();

	@Override
	public String toString() {
		if (StringUtils.isEmpty(getAreaCode())) {
			return getNumber();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(getAreaCode());
		if (getConnector() != null) {
			sb.append(getConnector());
		}
		sb.append(getNumber());
		return sb.toString();
	}
}
