package scw.security.authority.http;

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.annotation.Bean;
import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.http.Method;
import scw.resource.ResourceUtils;

@Bean(proxy = false)
public class XmlHttpAuthorityManager extends SimpleHttpAuthorityManager {
	private static Logger logger = LoggerFactory.getLogger(XmlHttpAuthorityManager.class);

	public XmlHttpAuthorityManager(
			@ParameterName("xml.http.authority") @ResourceParameter @DefaultValue("classpath:/http-authority.xml") String xml) {
		this(xml, null);
	}

	public XmlHttpAuthorityManager(String xml, String parentId) {
		addByXml(xml, StringUtils.isEmpty(parentId) ? "" : parentId);
	}

	private void addByXml(String xml, String defParentId) {
		if (!ResourceUtils.getResourceOperations().isExist(xml)) {
			logger.warn("not found:{}", xml);
			return;
		}

		Element element = XMLUtils.getRootElement(xml);
		String prefix = XMLUtils.getNodeAttributeValue(element, "prefix");
		NodeList nodeList = XMLUtils.getChildNodes(element, true);
		if (nodeList == null) {
			return;
		}

		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}

			Map<String, String> map = XMLUtils.xmlToMap(node);
			if (map.isEmpty()) {
				continue;
			}

			addAuthority(map, defParentId, prefix);
		}
	}

	private void addAuthority(Map<String, String> map, String defParentId, String prefix) {
		String id = map.remove("id");
		if (id == null) {
			throw new NullPointerException("id不能为空：" + JSONUtils.toJSONString(map));
		}

		if (StringUtils.isNotEmpty(prefix)) {
			id = prefix + id;
		}

		String name = map.remove("name");
		if (StringUtils.isEmpty(name)) {
			throw new NullPointerException("name不能为空或空字符串：" + JSONUtils.toJSONString(map));
		}

		String parentId = map.remove("parentId");
		if (StringUtils.isEmpty(parentId)) {
			parentId = defParentId;
		} else {
			if (StringUtils.isNotEmpty(prefix)) {
				parentId = prefix + parentId;
			}
		}

		String include = map.remove("include");
		if (!StringUtils.isEmpty(include)) {
			addByXml(include, parentId);
		}

		String path = map.remove("path");
		String method = map.remove("method");

		SimpleHttpAuthority simpleAuthority = new SimpleHttpAuthority();
		simpleAuthority.setId(id);
		simpleAuthority.setParentId(parentId);
		simpleAuthority.setName(name);
		simpleAuthority.setPath(path);
		simpleAuthority.setHttpMethod(Method.resolve(method));
		simpleAuthority.setAttributeMap(map);
		addAuthroity(simpleAuthority);
	}
}
