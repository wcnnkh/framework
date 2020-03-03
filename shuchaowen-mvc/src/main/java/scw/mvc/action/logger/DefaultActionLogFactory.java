package scw.mvc.action.logger;

import java.util.HashMap;
import java.util.Map;

import scw.core.Callable;
import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.SimpleParameterConfig;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.mvc.Channel;
import scw.mvc.Request;
import scw.mvc.action.Action;
import scw.mvc.http.HttpRequest;

public class DefaultActionLogFactory implements ActionLogFactory{
	private static final Callable<HashMap<String, String>> ATTRIBUTE_MAP_CALLABLE = CollectionUtils.hashMapCallable(8);
	private ParameterConfig identificationParameterConfig;
	private boolean ipEnable;

	public DefaultActionLogFactory(
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
		appendAnnotationAttributeMap(map, action, channel);
		return map;
	}
	
	protected final void appendAnnotationAttributeMap(Map<String, String> map, Action action, Channel channel){
		ActionLogAttributeConfig logConfig = action.getAnnotation(ActionLogAttributeConfig.class);
		if(ipEnable){
			if(logConfig == null || logConfig.ip()){
				CollectionUtils.put(map, "ip", getIp(action, channel), ATTRIBUTE_MAP_CALLABLE);
			}
		}else if(logConfig != null && logConfig.ip()){
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
