package scw.ibatis.beans;

import scw.beans.annotation.ConfigurationProperties;

@ConfigurationProperties(prefix = IbatisProperties.MYBATIS_PREFIX)
public class IbatisProperties {
	public static final String MYBATIS_PREFIX = "mybatis";
}
