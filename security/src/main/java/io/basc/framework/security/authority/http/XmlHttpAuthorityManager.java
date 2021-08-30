package io.basc.framework.security.authority.http;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.env.Environment;
import io.basc.framework.env.Sys;
import io.basc.framework.instance.annotation.PropertyName;
import io.basc.framework.instance.annotation.ResourceParameter;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.lang.DefaultValue;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHttpAuthorityManager extends
		DefaultHttpAuthorityManager<HttpAuthority> {
	private static Logger logger = LoggerFactory
			.getLogger(XmlHttpAuthorityManager.class);
	private final Environment environment;

	public XmlHttpAuthorityManager(
			Environment environment,
			@PropertyName("xml.http.authority") @ResourceParameter @DefaultValue("classpath:/http-authority.xml") String xml) {
		this(environment, xml, null);
	}

	public XmlHttpAuthorityManager(Environment environment, String xml,
			String parentId) {
		this.environment = environment;
		addByXml(xml, StringUtils.isEmpty(parentId) ? "" : parentId);
	}

	@SuppressWarnings("unchecked")
	private void addByXml(String xml, String defParentId) {
		if (!environment.exists(xml)) {
			logger.warn("not found:{}", xml);
			return;
		}

		Element element = DomUtils.getRootElement(environment, xml);
		String prefix = DomUtils.getNodeAttributeValue(element, "prefix");
		NodeList nodeList = DomUtils.getChildNodes(element, environment);
		if (nodeList == null) {
			return;
		}

		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}

			Map<String, String> map = (Map<String, String>) Sys.env
					.getConversionService().convert(
							node,
							TypeDescriptor.forObject(node),
							TypeDescriptor.map(Map.class, String.class,
									String.class));
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
					+ JSONUtils.getJsonSupport().toJSONString(map));
		}

		if (StringUtils.isNotEmpty(prefix)) {
			id = prefix + id;
		}

		String name = map.remove("name");
		if (StringUtils.isEmpty(name)) {
			throw new NullPointerException("name不能为空或空字符串："
					+ JSONUtils.getJsonSupport().toJSONString(map));
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
		register(new DefaultHttpAuthority(id, parentId, name, map, isMenu,
				path, method));
	}
}
