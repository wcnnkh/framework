package scw.security.authority.http;

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.annotation.Bean;
import scw.beans.auto.annotation.ResourceParameter;
import scw.core.annotation.ParameterName;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

@Bean(proxy = false)
public class XmlHttpAuthorityManager extends SimpleHttpAuthorityManager {
	private static Logger logger = LoggerFactory.getLogger(XmlHttpAuthorityManager.class);

	public XmlHttpAuthorityManager(
			@ParameterName("xml.http.authority") @ResourceParameter("classpath:/http-authority.xml") String xml) {
		this(xml, null);
	}

	public XmlHttpAuthorityManager(String xml, String parentId) {
		addByXml(xml, StringUtils.isEmpty(parentId) ? "" : parentId);
	}

	private void addByXml(String xml, String defParentId) {
		if (!ResourceUtils.isExist(xml)) {
			logger.warn("not found:{}", xml);
			return;
		}

		Element element = XMLUtils.getRootElement(xml);
		NodeList nodeList = XMLUtils.getChildNodes(element, true);
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}

			Map<String, String> map = XMLUtils.xmlToMap(node);
			if (map.isEmpty()) {
				continue;
			}

			addAuthority(map, defParentId);
		}
	}

	private void addAuthority(Map<String, String> map, String defParentId) {
		String id = map.remove("id");
		if (id == null) {
			throw new NullPointerException("id不能为空：" + JSONUtils.toJSONString(map));
		}

		String name = map.remove("name");
		if (StringUtils.isEmpty(name)) {
			throw new NullPointerException("name不能为空或空字符串：" + JSONUtils.toJSONString(map));
		}

		String parentId = map.remove("parentId");
		if (StringUtils.isEmpty(parentId)) {
			parentId = defParentId;
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
		simpleAuthority.setMethod(method);
		simpleAuthority.setAttributeMap(map);
		addAuthroity(simpleAuthority);
	}
}
