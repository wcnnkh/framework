package scw.mvc.logger;

import java.util.HashMap;
import java.util.Map;

import scw.core.Callable;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.Configuration;
import scw.core.parameter.annotation.DefaultValue;
import scw.core.parameter.annotation.ParameterName;
import scw.core.utils.CollectionUtils;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.logger.annotation.ActionLogAttributeConfig;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultActionLogFactory extends AbstractActionLogFactory {
	private static final Callable<HashMap<String, String>> ATTRIBUTE_MAP_CALLABLE = CollectionUtils.hashMapCallable(8);
	private String identificationKey;
	private boolean ipEnable;

	public DefaultActionLogFactory(
			@ParameterName("mvc.action.log.identification") @DefaultValue("uid") String identificationKey,
			@ParameterName("mvc.action.log.ip") @DefaultValue("false") boolean ipEnable) {
		this.identificationKey = identificationKey;
		this.ipEnable = ipEnable;
	}

	public String getIdentification(Action action, HttpChannel httpChannel) {
		if (identificationKey == null) {
			return null;
		}
		return httpChannel.getValue(identificationKey).getAsString();
	}

	public Map<String, String> getAttributeMap(Action action, HttpChannel httpChannel) {
		Map<String, String> map = null;
		appendAnnotationAttributeMap(map, action, httpChannel);
		return map;
	}

	protected final void appendAnnotationAttributeMap(Map<String, String> map, Action action, HttpChannel httpChannel) {
		ActionLogAttributeConfig logConfig = AnnotationUtils.getAnnotation(ActionLogAttributeConfig.class,
				action.getSourceClass(), action.getAnnotatedElement());
		if (ipEnable) {
			if (logConfig == null || logConfig.ip()) {
				CollectionUtils.put(map, "ip", httpChannel.getRequest().getIp(), ATTRIBUTE_MAP_CALLABLE);
			}
		} else if (logConfig != null && logConfig.ip()) {
			CollectionUtils.put(map, "ip", httpChannel.getRequest().getIp(), ATTRIBUTE_MAP_CALLABLE);
		}

		if (logConfig != null) {
			for (String name : logConfig.value()) {
				String value = getAttirubteValue(httpChannel, name);
				if (value == null) {
					continue;
				}

				CollectionUtils.put(map, name, value, ATTRIBUTE_MAP_CALLABLE);
			}
		}
	}

	protected String getAttirubteValue(HttpChannel httpChannel, String name) {
		return httpChannel.getValue(name).getAsString();
	}
}
