package scw.mvc.parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import scw.beans.annotation.Bean;
import scw.core.parameter.ParameterDescriptor;
import scw.json.JSONSupport;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.HttpChannel;
import scw.net.NetworkUtils;
import scw.value.StringValue;
import scw.xml.XMLUtils;

@Bean(proxy = false)
public class DefaultRequestBodyParse implements RequestBodyParse {
	private static Logger logger = LoggerFactory.getLogger(DefaultRequestBodyParse.class);

	public Object requestBodyParse(HttpChannel httpChannel, JSONSupport jsonParseSupport,
			ParameterDescriptor parameterDescriptor) throws Exception {
		String body = null;
		if (NetworkUtils.isJsonMessage(httpChannel.getRequest())) {
			body = httpChannel.getHttpChannelBeanManager().getBean(Body.class).getBody();
		} else if (NetworkUtils.isXmlMessage(httpChannel.getRequest())) {
			Document document = XMLUtils.parse(new InputSource(httpChannel.getRequest().getReader()));
			Element element = document.getDocumentElement();
			body = jsonParseSupport.toJSONString(XMLUtils.toRecursionMap(element));
		} else {
			Map<String, String[]> parameterMap = httpChannel.getRequest().getParameterMap();
			if (parameterMap != null) {
				Map<String, Object> map = new HashMap<String, Object>(parameterMap.size());
				for (Entry<String, String[]> entry : parameterMap.entrySet()) {
					String[] values = entry.getValue();
					if (values == null || values.length == 0) {
						continue;
					}

					map.put(entry.getKey(), entry.getValue().length == 1 ? entry.getValue()[0] : entry.getValue());
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
