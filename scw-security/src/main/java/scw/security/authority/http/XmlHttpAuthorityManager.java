package scw.security.authority.http;

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.configure.support.ConfigureUtils;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.annotation.DefaultValue;
import scw.core.parameter.annotation.ParameterName;
import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.io.ResourceUtils;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.xml.XMLUtils;

public class XmlHttpAuthorityManager extends
		DefaultHttpAuthorityManager<HttpAuthority> {
	private static Logger logger = LoggerFactory
			.getLogger(XmlHttpAuthorityManager.class);

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
			
			Map<String, String> map = ConfigureUtils.getConversionServiceFactory().convertToMap(node, String.class, String.class);
			if (map.isEmpty()) {
				continue;
			}

			addAuthority(map, defParentId, prefix);
		}
	}

	private void addAuthority(Map<String, String> map, String defParentId,
			String prefix) {
		String id = map.remove("id");
		if (id == null) {
			throw new NullPointerException("id不能为空："
					+ JSONUtils.toJSONString(map));
		}

		if (StringUtils.isNotEmpty(prefix)) {
			id = prefix + id;
		}

		String name = map.remove("name");
		if (StringUtils.isEmpty(name)) {
			throw new NullPointerException("name不能为空或空字符串："
					+ JSONUtils.toJSONString(map));
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
		boolean isMenu = !StringUtils.isEmpty(path, method);
		register(new DefaultHttpAuthority(id, parentId, name, map, isMenu, path,
				HttpMethod.resolve(method)));
	}
}
