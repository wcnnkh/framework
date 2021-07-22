package scw.ibatis.beans;

import scw.beans.annotation.ConfigurationProperties;

@ConfigurationProperties(prefix = IbatisProperties.MYBATIS_PREFIX)
public class IbatisProperties {
	public static final String MYBATIS_PREFIX = "mybatis";
	public static final String DEFAULT_CONFIG_LOCATION = "mybatis-config.xml";
	
	private String configLocation;
	
	public String getConfigLocation() {
		return configLocation;
	}
	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}
}
