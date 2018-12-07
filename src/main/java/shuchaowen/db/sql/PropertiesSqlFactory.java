package shuchaowen.db.sql;

import java.util.Properties;

import shuchaowen.common.Logger;
import shuchaowen.common.utils.ConfigUtils;

public class PropertiesSqlFactory implements SqlFactory{
	private final Properties properties;
	
	public PropertiesSqlFactory(String propertieFile, String charsetName){
		this(ConfigUtils.getProperties(propertieFile, charsetName));
	}
	
	public PropertiesSqlFactory(Properties properties){
		this.properties = properties;
	}

	public Properties getProperties() {
		return properties;
	}

	public SQL getSql(String name, Object... params) { 
		String sql = properties.getProperty(name);
		if(sql == null){
			Logger.warn(this.getClass().getName(), name);
			return new SimpleSQL(name, params);
		}
		return new SimpleSQL(sql, params);
	}
}
