package scw.security.authority.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scw.core.exception.AlreadyExistsException;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.json.JSONUtils;
import scw.security.authority.Authority;

public class SimplHttpAuthorityManager implements HttpAuthorityManager {
	private Map<String, Map<String, Long>> httpAuthorityMap = new HashMap<String, Map<String, Long>>();
	private Map<Long, HttpAuthority> authorityMap = new HashMap<Long, HttpAuthority>();
	private Map<Long, List<Long>> parentMap = new HashMap<Long, List<Long>>();

	public synchronized void addHttpAuthority(HttpAuthority httpAuthority) {
		if (authorityMap.containsKey(httpAuthority.getId())) {
			throw new AlreadyExistsException(JSONUtils.toJSONString(httpAuthority));
		}

		authorityMap.put(httpAuthority.getId(), httpAuthority);

		if (StringUtils.isNotEmpty(httpAuthority.getRequestPath(), httpAuthority.getMethod())) {
			Map<String, Long> methodMap = httpAuthorityMap.get(httpAuthority.getRequestPath());
			if (methodMap == null) {
				methodMap = new HashMap<String, Long>();
			}

			if (methodMap.containsKey(httpAuthority.getMethod())) {
				throw new AlreadyExistsException(JSONUtils.toJSONString(httpAuthority));
			}

			methodMap.put(httpAuthority.getMethod(), httpAuthority.getId());
			httpAuthorityMap.put(httpAuthority.getRequestPath(), methodMap);
		}

		List<Long> subList = parentMap.get(httpAuthority.getParentId());
		if (subList == null) {
			subList = new LinkedList<Long>();
		}
		subList.add(httpAuthority.getId());
		parentMap.put(httpAuthority.getParentId(), subList);
	}

	public HttpAuthority getHttpAuthority(long id) {
		return authorityMap.get(id);
	}

	public HttpAuthority getHttpAuthority(String requestPath, String method) {
		Map<String, Long> map = httpAuthorityMap.get(requestPath);
		if (map == null) {
			return null;
		}

		Long id = map.get(method);
		if (id == null) {
			return null;
		}

		return getHttpAuthority(id);
	}

	public List<HttpAuthority> getList() {
		return new ArrayList<HttpAuthority>(authorityMap.values());
	}

	public List<HttpAuthority> getList(long parentId) {
		List<Long> subList = parentMap.get(parentId);
		if (subList == null) {
			return null;
		}

		List<HttpAuthority> httpAuthorities = new ArrayList<HttpAuthority>(subList.size());
		for (long id : subList) {
			HttpAuthority httpAuthority = getHttpAuthority(id);
			if (httpAuthority == null) {
				continue;
			}

			httpAuthorities.add(httpAuthority);
		}
		return httpAuthorities;
	}

	public Authority getAuthority(long id) {
		return getHttpAuthority(id);
	}

	public List<Long> getSubList(long id) {
		List<Long> subList = parentMap.get(id);
		if (subList == null) {
			return null;
		}

		return new ArrayList<Long>(subList);
	}

	public TreeHttpAuthority getTreeHttpAuthority() {
		return getTreeHttpAuthority(0);
	}

	public TreeHttpAuthority getTreeHttpAuthority(long id) {
		HttpAuthority httpAuthority = getHttpAuthority(id);
		if (httpAuthority == null) {
			return null;
		}

		SimpleTreeHttpAuthority treeHttpAuthority = new SimpleTreeHttpAuthority();
		treeHttpAuthority.setHttpAuthority(httpAuthority);
		List<Long> subList = parentMap.get(id);
		if (!CollectionUtils.isEmpty(subList)) {
			List<TreeHttpAuthority> treeHttpAuthorities = new ArrayList<TreeHttpAuthority>(subList.size());
			for (long subId : subList) {
				TreeHttpAuthority authority = getTreeHttpAuthority(subId);
				if (authority == null) {
					continue;
				}

				treeHttpAuthorities.add(authority);
			}
			treeHttpAuthority.setSubList(treeHttpAuthorities);
		}
		return treeHttpAuthority;
	}

}
