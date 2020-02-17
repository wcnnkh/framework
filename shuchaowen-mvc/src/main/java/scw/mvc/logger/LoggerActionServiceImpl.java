package scw.mvc.logger;

import java.util.HashMap;
import java.util.Map;

import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.SimpleParameterConfig;
import scw.core.utils.StringUtils;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.Request;
import scw.mvc.http.HttpRequest;

public class LoggerActionServiceImpl implements LoggerActionService{
	private ParameterConfig identificationParameterConfig;

	public LoggerActionServiceImpl(
			@ParameterName("mvc.logger.identification") @DefaultValue("uid") String identificationKey) {
		if (StringUtils.isNotEmpty(identificationKey)) {
			this.identificationParameterConfig = new SimpleParameterConfig(
					identificationKey, null, String.class, String.class);
		}
	}

	public String getIdentification(Action action, Channel channel) {
		return (String) channel.getParameter(identificationParameterConfig);
	}

	public Map<String, String> getAttributeMap(Action action, Channel channel) {
		LogAttributeConfig logConfig = action.getAnnotation(LogAttributeConfig.class);
		Map<String, String> map = new HashMap<String, String>(8);
		if(logConfig != null){
			if(logConfig.ip()){
				map.put("ip", getIp(action, channel));
			}
			
			for(String name : logConfig.value()){
				String value = getAttirubteValue(channel, name);
				if(value == null){
					continue;
				}
				
				map.put(name, value);
			}
		}
		
		//测试
		map.put("ip", getIp(action, channel));
		map.put("ip2", getIp(action, channel));
		return map;
	}
	
	protected String getAttirubteValue(Channel channel, String name){
		return (String) channel.getParameter( new SimpleParameterConfig(
				name, null, String.class, String.class));
	}
	
	protected String getIp(Action action, Channel channel){
		Request request = channel.getRequest();
		if(request instanceof HttpRequest){
			return ((HttpRequest) request).getIP();
		}
		
		return null;
	}
}
