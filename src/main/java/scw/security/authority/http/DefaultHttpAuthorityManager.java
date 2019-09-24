package scw.security.authority.http;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.annotation.Bean;
import scw.beans.auto.annotation.ResourceParameter;
import scw.core.utils.CollectionUtils;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.core.utils.XUtils;
import scw.json.JSONUtils;
import scw.mvc.http.HttpRequest;

@Bean(proxy = false)
public class DefaultHttpAuthorityManager extends SimplHttpAuthorityManager implements MvcHttpAuthorityManager {
	public DefaultHttpAuthorityManager() {
		scanAnnotation("");
	}

	public DefaultHttpAuthorityManager(@ResourceParameter("classpath:/http-authority.xml") String xml) {
		addHttpAuthority(null, 1000, xml, null);
	}

	private void scanAnnotation(String packageName) {
		Collection<Class<?>> classList = ResourceUtils.getClassList(packageName);
		if (!CollectionUtils.isEmpty(classList)) {
			for (Class<?> clazz : classList) {
				scanAnnotation(clazz);
			}
		}
	}

	private void scanAnnotation(Class<?> clazz) {
		// ignore
	}

	private HttpAuthority merge(String parentRequestPath, int baseId, HttpAuthority parent,
			HttpAuthority httpAuthority) {
		if (parent == null) {
			return httpAuthority;
		}

		SimpleHttpAuthority authority = new SimpleHttpAuthority();
		long id = parent.getId() * baseId + httpAuthority.getId();
		authority.setId(id);

		long parentId = httpAuthority.getParentId() == 0 ? parent.getId()
				: (parent.getId() * baseId + httpAuthority.getParentId());
		authority.setParentId(parentId);
		authority.setName(httpAuthority.getName());
		authority.setMethod(httpAuthority.getMethod());
		authority.setRequestPath(XUtils.mergePath(parentRequestPath, httpAuthority.getRequestPath()));

		Map<String, String> map = new LinkedHashMap<String, String>();
		Map<String, String> parentMap = parent.getAttributeMap();
		if (parentMap != null) {
			map.putAll(parentMap);
		}

		Map<String, String> currMap = httpAuthority.getAttributeMap();
		if (currMap != null) {
			map.putAll(currMap);
		}
		authority.setAttributeMap(map);
		return authority;
	}

	private void addHttpAuthority(String parentRequestPath, int baseId, String xml, HttpAuthority parent) {
		if (!ResourceUtils.isExist(xml)) {
			return;
		}

		Element root = XMLUtils.getRootElement(xml);
		String annotation = root.getAttribute("annotation");
		if (annotation != null) {
			scanAnnotation(annotation);
		}

		String parentPath = XMLUtils.getNodeAttributeValue(root, "path");
		if (StringUtils.isEmpty(parentPath)) {
			parentPath = parentRequestPath;
		}

		int base = StringUtils.parseInt(XMLUtils.getNodeAttributeValue(root, "baseId"), baseId);
		NodeList nhosts = root.getChildNodes();
		for (int i = 0, size = nhosts.getLength(); i < size; i++) {
			addHttpAuthority(parentPath, base, nhosts.item(i), parent);
		}
	}

	private void addHttpAuthority(String parentRequestPath, int baseId, Node node, HttpAuthority parent) {
		SimpleHttpAuthority httpAuthority = convertHttpAuthority(node);
		Map<String, String> attributeMap = httpAuthority.getAttributeMap();
		if (attributeMap == null) {
			addHttpAuthority(merge(parentRequestPath, baseId, parent, httpAuthority));
			return;
		}

		String subList = attributeMap.remove("include");
		httpAuthority.setAttributeMap(attributeMap);
		if (StringUtils.isEmpty(subList)) {
			addHttpAuthority(merge(parentRequestPath, baseId, parent, httpAuthority));
			return;
		}

		addHttpAuthority(parentRequestPath, baseId, subList, parent);
	}

	private SimpleHttpAuthority convertHttpAuthority(Node rootNode) {
		SimpleHttpAuthority simpleHttpAuthority = new SimpleHttpAuthority();
		LinkedHashMap<String, String> attributeMap = XMLUtils.nodeList2Map(rootNode.getChildNodes());
		String id = attributeMap.remove("id");
		if (id == null) {
			throw new NullPointerException(JSONUtils.toJSONString(attributeMap));
		}

		simpleHttpAuthority.setId(StringUtils.parseLong(id));
		simpleHttpAuthority.setParentId(StringUtils.parseLong(attributeMap.remove("parentId")));
		simpleHttpAuthority.setMethod(attributeMap.remove("method"));
		simpleHttpAuthority.setRequestPath(attributeMap.remove("requestPath"));
		simpleHttpAuthority.setAttributeMap(attributeMap);
		return simpleHttpAuthority;
	}

	public HttpAuthority getHttpAuthority(HttpRequest httpRequest) {
		return getHttpAuthority(httpRequest.getRequestPath(), httpRequest.getMethod());
	}

}
