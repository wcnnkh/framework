package scw.security.authority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scw.core.utils.CollectionUtils;
import scw.json.JSONUtils;
import scw.lang.AlreadyExistsException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class DefaultAuthorityManager<T extends Authority> implements
		AuthorityManager<T> {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	public Map<String, T> authorityMap = new HashMap<String, T>();
	public Map<String, Set<String>> subListMap = new HashMap<String, Set<String>>();
	public List<String> roots = new ArrayList<String>();

	public T getAuthority(String id) {
		if (id == null) {
			return null;
		}

		return authorityMap.get(id);
	}

	public Collection<T> getAuthoritys() {
		return Collections.unmodifiableCollection(authorityMap.values());
	}

	public List<T> getAuthoritySubList(String id) {
		if (id == null) {
			// root
			List<T> list = new ArrayList<T>(roots.size());
			for (String subId : roots) {
				T v = getAuthority(subId);
				if (v == null) {
					continue;
				}

				list.add(v);
			}
			return list;
		}

		Set<String> set = subListMap.get(id);
		if (CollectionUtils.isEmpty(set)) {
			return Collections.emptyList();
		}

		List<T> values = new ArrayList<T>(set.size());
		for (String subId : set) {
			T v = getAuthority(subId);
			if (v == null) {
				continue;
			}

			values.add(v);
		}

		return values;
	}

	public List<AuthorityTree<T>> getAuthorityTreeList(String id) {
		List<T> list = getAuthoritySubList(id);
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}

		List<AuthorityTree<T>> treeList = new ArrayList<AuthorityTree<T>>(
				list.size());
		for (T authority : list) {
			treeList.add(new AuthorityTree<T>(authority,
					getAuthorityTreeList(authority.getId())));
		}
		return treeList;
	}

	public synchronized void register(T authority) {
		if (authority == null) {
			return;
		}

		if (authorityMap.containsKey(authority.getId())) {
			throw new AlreadyExistsException(JSONUtils.toJSONString(authority));
		}

		if (logger.isTraceEnabled()) {
			logger.trace("register authority:{}",
					JSONUtils.toJSONString(authority));
		}

		authorityMap.put(authority.getId(), authority);
		if (authority.getParentId() == null) {// root
			roots.add(authority.getId());
		} else {
			Set<String> set = subListMap.get(authority.getParentId());
			if (set == null) {
				set = new LinkedHashSet<String>();
			}

			if (set.contains(authority.getId())) {
				throw new AlreadyExistsException(
						JSONUtils.toJSONString(authority));
			}

			set.add(authority.getId());
			subListMap.put(authority.getParentId(), set);
		}
	}
}
