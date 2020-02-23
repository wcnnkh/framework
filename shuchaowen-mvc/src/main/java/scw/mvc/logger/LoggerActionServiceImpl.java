package scw.mvc.logger;

import java.util.HashMap;
import java.util.Map;

import scw.core.Callable;
import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.SimpleParameterConfig;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.Request;
import scw.mvc.http.HttpRequest;

public class LoggerActionServiceImpl implements LoggerActionService{
	private static final Callable<HashMap<String, String>> ATTRIBUTE_MAP_CALLABLE = CollectionUtils.hashMapCallable(8);
	private ParameterConfig identificationParameterConfig;
	private boolean ipEnable;

	public LoggerActionServiceImpl(
			@ParameterName("mvc.logger.identification") @DefaultValue("uid") String identificationKey, @ParameterName("mvc.logger.ip")@DefaultValue("false") boolean ipEnable) {
		if (StringUtils.isNotEmpty(identificationKey)) {
			this.identificationParameterConfig = new SimpleParameterConfig(
					identificationKey, null, String.class, String.class);
		}
		this.ipEnable = ipEnable;
	}

	public String getIdentification(Action action, Channel channel) {
		if(identificationParameterConfig == null){
			return null;
		}
		return (String) channel.getParameter(identificationParameterConfig);
	}

	public Map<String, String> getAttributeMap(Action action, Channel channel) {
		Map<String, String> map = null;
		LogAttributeConfig logConfig = action.getAnnotation(LogAttributeConfig.class);
		if(ipEnable || (logConfig != null && logConfig.ip())){
			CollectionUtils.put(map, "ip", getIp(action, channel), ATTRIBUTE_MAP_CALLABLE);
		}
		
		if(logConfig != null){
			for(String name : logConfig.value()){
				String value = getAttirubteValue(channel, name);
				if(value == null){
					continue;
				}
				
				CollectionUtils.put(map, name, value, ATTRIBUTE_MAP_CALLABLE);
			}
		}
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
