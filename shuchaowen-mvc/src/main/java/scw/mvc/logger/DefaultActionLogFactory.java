package scw.mvc.logger;

import java.util.HashMap;
import java.util.Map;

import scw.core.Callable;
import scw.core.instance.annotation.Configuration;
import scw.core.parameter.DefaultParameterDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.annotation.DefaultValue;
import scw.core.parameter.annotation.ParameterName;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.mvc.logger.annotation.ActionLogAttributeConfig;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.action.Action;

@Configuration(order=Integer.MIN_VALUE)
public class DefaultActionLogFactory extends AbstractActionLogFactory{
	private static final Callable<HashMap<String, String>> ATTRIBUTE_MAP_CALLABLE = CollectionUtils.hashMapCallable(8);
	private ParameterDescriptor identificationParameterConfig;
	private boolean ipEnable;

	public DefaultActionLogFactory(
			@ParameterName("mvc.action.log.identification") @DefaultValue("uid") String identificationKey, @ParameterName("mvc.action.log.ip")@DefaultValue("false") boolean ipEnable) {
		if (StringUtils.isNotEmpty(identificationKey)) {
			this.identificationParameterConfig = new DefaultParameterDescriptor(
					identificationKey, String.class, String.class);
		}
		this.ipEnable = ipEnable;
	}

	public String getIdentification(Action action, HttpChannel httpChannel) {
		if(identificationParameterConfig == null){
			return null;
		}
		return (String) httpChannel.getParameter(identificationParameterConfig);
	}

	public Map<String, String> getAttributeMap(Action action, HttpChannel httpChannel) {
		Map<String, String> map = null;
		appendAnnotationAttributeMap(map, action, httpChannel);
		return map;
	}
	
	protected final void appendAnnotationAttributeMap(Map<String, String> map, Action action, HttpChannel httpChannel){
		ActionLogAttributeConfig logConfig = action.getAnnotatedElement().getAnnotation(ActionLogAttributeConfig.class);
		if(ipEnable){
			if(logConfig == null || logConfig.ip()){
				CollectionUtils.put(map, "ip", httpChannel.getRequest().getIp(), ATTRIBUTE_MAP_CALLABLE);
			}
		}else if(logConfig != null && logConfig.ip()){
			CollectionUtils.put(map, "ip", httpChannel.getRequest().getIp(), ATTRIBUTE_MAP_CALLABLE);
		}
		
		if(logConfig != null){
			for(String name : logConfig.value()){
				String value = getAttirubteValue(httpChannel, name);
				if(value == null){
					continue;
				}
				
				CollectionUtils.put(map, name, value, ATTRIBUTE_MAP_CALLABLE);
			}
		}
	}
	
	protected String getAttirubteValue(HttpChannel httpChannel, String name){
		return (String) httpChannel.getParameter( new DefaultParameterDescriptor(
				name, String.class, String.class));
	}
}
