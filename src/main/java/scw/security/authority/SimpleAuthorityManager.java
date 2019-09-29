package scw.security.authority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scw.core.exception.AlreadyExistsException;
import scw.core.utils.CollectionUtils;
import scw.json.JSONUtils;

public class SimpleAuthorityManager<T extends Authority> implements AuthorityManager<T> {
	public Map<String, T> authorityMap = new HashMap<String, T>();
	public Map<String, Set<String>> subListMap = new HashMap<String, Set<String>>();

	public T getAuthority(String id) {
		return authorityMap.get(id);
	}

	public List<T> getAuthorityList() {
		return new ArrayList<T>(authorityMap.values());
	}

	public List<T> getAuthoritySubList(String id) {
		Set<String> set = subListMap.get(id);
		if (CollectionUtils.isEmpty(set)) {
			return new ArrayList<T>(4);
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

	public synchronized void addAuthroity(T authority) {
		if (authority == null) {
			return;
		}

		if (authorityMap.containsKey(authority.getId())) {
			throw new AlreadyExistsException(JSONUtils.toJSONString(authority));
		}

		Set<String> set = subListMap.get(authority.getParentId());
		if (set == null) {
			set = new LinkedHashSet<String>();
		}

		if (set.contains(authority.getId())) {
			throw new AlreadyExistsException(JSONUtils.toJSONString(authority));
		}

		set.add(authority.getId());
		subListMap.put(authority.getParentId(), set);
		authorityMap.put(authority.getId(), authority);
	}

	public AuthorityTree<T> getAuthorityTree(String id) {
		T authroity = getAuthority(id);
		if (authroity == null) {
			return null;
		}

		SimpleAuthorityTree<T> authorityTree = new SimpleAuthorityTree<T>();
		authorityTree.setAuthority(authroity);
		Set<String> subList = subListMap.get(authroity.getId());
		if (subList != null) {
			List<AuthorityTree<T>> list = new ArrayList<AuthorityTree<T>>(subList.size());
			for (String subId : subList) {
				AuthorityTree<T> tree = getAuthorityTree(subId);
				if (tree == null) {
					continue;
				}

				list.add(tree);
			}
			authorityTree.setSubList(list);
		}
		return authorityTree;
	}
}
