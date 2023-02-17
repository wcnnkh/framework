package io.basc.framework.security.authority.http;

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.xml.XmlUtils;

public class XmlHttpAuthorityManager extends DefaultHttpAuthorityManager<HttpAuthority> {
	private static Logger logger = LoggerFactory.getLogger(XmlHttpAuthorityManager.class);
	private final Environment environment;

	public XmlHttpAuthorityManager(Environment environment, Resource resource) {
		this(environment, resource, null);
	}

	public XmlHttpAuthorityManager(Environment environment, Resource resource, String parentId) {
		this.environment = environment;
		addByXml(resource, StringUtils.isEmpty(parentId) ? "" : parentId);
	}

	@SuppressWarnings("unchecked")
	private void addByXml(Resource resource, String defParentId) {
		if (!resource.exists()) {
			logger.warn("not found:{}", resource);
			return;
		}

		DomUtils.getTemplate().read(resource, (document) -> {
			Element element = document.getDocumentElement();
			String prefix = DomUtils.getNodeAttributeValue(element, "prefix").getAsString();
			NodeList nodeList = XmlUtils.getTemplate().getChildNodes(element, environment.getResourceLoader());
			if (nodeList == null) {
				return;
			}

			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node node = nodeList.item(i);
				if (node == null) {
					continue;
				}

				Map<String, String> map = (Map<String, String>) environment.getConversionService().convert(node,
						TypeDescriptor.forObject(node), TypeDescriptor.map(Map.class, String.class, String.class));
				if (map.isEmpty()) {
					continue;
				}

				addAuthority(map, defParentId, prefix);
			}
		});
	}

	private void addAuthority(Map<String, String> map, String defParentId, String prefix) {
		String id = map.remove("id");
		if (id == null) {
			throw new NullPointerException("id不能为空：" + JsonUtils.toJsonString(map));
		}

		if (StringUtils.isNotEmpty(prefix)) {
			id = prefix + id;
		}

		String name = map.remove("name");
		if (StringUtils.isEmpty(name)) {
			throw new NullPointerException("name不能为空或空字符串：" + JsonUtils.toJsonString(map));
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
			addByXml(environment.getResourceLoader().getResource(include), parentId);
		}

		String path = map.remove("path");
		String method = map.remove("method");
		boolean isMenu = !StringUtils.isAnyEmpty(path, method);
		register(new DefaultHttpAuthority(id, parentId, name, map, isMenu, path, method));
	}
}
