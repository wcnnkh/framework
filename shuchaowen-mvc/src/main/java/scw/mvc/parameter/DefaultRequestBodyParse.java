package scw.mvc.parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import scw.core.parameter.ParameterDescriptor;
import scw.core.utils.CollectionUtils;
import scw.json.JSONSupport;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.HttpChannel;
import scw.net.InetUtils;
import scw.util.MultiValueMap;
import scw.value.StringValue;
import scw.xml.XMLUtils;

public class DefaultRequestBodyParse implements RequestBodyParse {
	private static Logger logger = LoggerFactory.getLogger(DefaultRequestBodyParse.class);

	public Object requestBodyParse(HttpChannel httpChannel, JSONSupport jsonParseSupport,
			ParameterDescriptor parameterDescriptor) throws Exception {
		String body = null;
		if (InetUtils.isJsonMessage(httpChannel.getRequest())) {
			body = httpChannel.getBean(Body.class).getBody();
		} else if (InetUtils.isXmlMessage(httpChannel.getRequest())) {
			Document document = XMLUtils.parse(new InputSource(httpChannel.getRequest().getReader()));
			Element element = document.getDocumentElement();
			body = jsonParseSupport.toJSONString(XMLUtils.toRecursionMap(element));
		} else {
			MultiValueMap<String, String> parameterMap = httpChannel.getRequest().getParameterMap();
			if (parameterMap != null) {
				Map<String, Object> map = new HashMap<String, Object>(parameterMap.size());
				for (Entry<String, List<String>> entry : parameterMap.entrySet()) {
					List<String> values = entry.getValue();
					if (CollectionUtils.isEmpty(values)) {
						continue;
					}

					map.put(entry.getKey(), values.size() == 1 ? values.get(0) : values);
				}
				body = jsonParseSupport.toJSONString(map);
			}
		}

		if (body == null) {
			logger.warn("无法解析：{}", httpChannel);
			return null;
		}

		StringValue value = new StringValue(body);
		value.setJsonSupport(jsonParseSupport);
		return value.getAsObject(parameterDescriptor.getGenericType());
	}
}
