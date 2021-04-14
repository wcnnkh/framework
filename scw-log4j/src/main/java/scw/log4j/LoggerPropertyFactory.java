package scw.log4j;

import scw.env.SystemEnvironment;
import scw.logger.LoggerLevelManager;
import scw.value.StringValue;
import scw.value.Value;
import scw.value.factory.ValueFactory;

public class LoggerPropertyFactory implements ValueFactory<String> {
	private static LoggerPropertyFactory instance = new LoggerPropertyFactory();

	public static LoggerPropertyFactory getInstance() {
		return instance;
	}
	
	public Value getValue(String key) {
		Value value = SystemEnvironment.getInstance().getValue(key);
		if(value != null){
			return value;
		}
		
		String v = null;
		if (key.equalsIgnoreCase("default.logger.level")) {
			v = LoggerLevelManager.getInstance().getDefaultLevel().getName();
		} else if (key.equalsIgnoreCase("logger.rootPath")) {
			v = SystemEnvironment.getInstance().getWorkPath();
		}
		
		return v == null? null:new StringValue(v);
	}
}
