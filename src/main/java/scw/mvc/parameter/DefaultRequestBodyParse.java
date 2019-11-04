package scw.mvc.parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import scw.beans.annotation.Bean;
import scw.core.multivalue.MultiValueParameterFactory;
import scw.core.parameter.ParameterConfig;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.json.JSONParseSupport;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;

@Bean(proxy = false)
public class DefaultRequestBodyParse implements RequestBodyParse {
	private static Logger logger = LoggerFactory.getLogger(DefaultRequestBodyParse.class);

	public Object requestBodyParse(Channel channel, JSONParseSupport jsonParseSupport, ParameterConfig parameterConfig)
			throws Exception {
		String body = null;
		if (MVCUtils.isJsonRequest(channel.getRequest())) {
			body = channel.getBean(Body.class).getBody();
		} else if (MVCUtils.isXmlRequeset(channel.getRequest())) {
			Document document = XMLUtils.parse(new InputSource(channel.getRequest().getReader()));
			Element element = document.getDocumentElement();
			body = jsonParseSupport.toJSONString(XMLUtils.toRecursionMap(element));
		} else if (channel.getRequest() instanceof MultiValueParameterFactory) {
			MultiValueParameterFactory multiValueParameterFactory = channel.getRequest();
			Map<String, String[]> parameterMap = multiValueParameterFactory.getParameterMap();
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
			logger.warn("无法解析：{}", channel);
			return null;
		}
		return StringUtils.defaultAutoParse(body, parameterConfig.getGenericType(), jsonParseSupport);
	}
}
