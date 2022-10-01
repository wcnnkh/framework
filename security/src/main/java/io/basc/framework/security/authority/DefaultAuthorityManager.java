package io.basc.framework.security.authority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import io.basc.framework.json.JSONUtils;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

public class DefaultAuthorityManager<T extends Authority> implements AuthorityManager<T> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	public Map<String, T> authorityMap = new HashMap<String, T>();

	public T getAuthority(String id) {
		if (id == null) {
			return null;
		}

		return authorityMap.get(id);
	}

	public List<T> getAuthorityList(Predicate<T> authorityFilter) {
		List<T> list = new ArrayList<T>(authorityMap.size());
		for (Entry<String, T> entry : authorityMap.entrySet()) {
			if (acceptInternal(entry.getValue(), authorityFilter)) {
				list.add(entry.getValue());
			}
		}
		return list;
	}

	public List<T> getRootList(Predicate<T> authorityFilter) {
		List<T> list = new ArrayList<T>();
		for (Entry<String, T> entry : authorityMap.entrySet()) {
			String parentId = entry.getValue().getParentId();
			if (parentId == null || !authorityMap.containsKey(parentId)) {
				if (acceptInternal(entry.getValue(), authorityFilter)) {
					list.add(entry.getValue());
				}
			}
		}
		return list;
	}

	public List<AuthorityTree<T>> getAuthorityTreeList(Predicate<T> authorityFilter) {
		List<T> list = getRootList(authorityFilter);
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}

		List<AuthorityTree<T>> treeList = new ArrayList<AuthorityTree<T>>(list.size());
		for (T authority : list) {
			treeList.add(new AuthorityTree<T>(authority, getAuthoritySubTreeList(authority.getId(), authorityFilter)));
		}
		return treeList;
	}

	public List<T> getAuthoritySubList(String id, Predicate<T> authorityFilter) {
		if (id == null) {
			return getRootList(authorityFilter);
		}

		List<T> values = new ArrayList<T>();
		for (Entry<String, T> entry : authorityMap.entrySet()) {
			if (StringUtils.equals(id, entry.getValue().getParentId())) {
				if (acceptInternal(entry.getValue(), authorityFilter)) {
					values.add(entry.getValue());
				}
			}
		}
		return values;
	}

	protected boolean acceptInternal(T authority, Predicate<T> authorityFilter) {
		return authorityFilter == null || authorityFilter.test(authority);
	}

	public List<AuthorityTree<T>> getAuthoritySubTreeList(String id, Predicate<T> authorityFilter) {
		List<T> list = getAuthoritySubList(id, authorityFilter);
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}

		List<AuthorityTree<T>> treeList = new ArrayList<AuthorityTree<T>>(list.size());
		for (T authority : list) {
			treeList.add(new AuthorityTree<T>(authority, getAuthoritySubTreeList(authority.getId(), authorityFilter)));
		}
		return treeList;
	}

	public synchronized void register(T authority) {
		if (authority == null) {
			return;
		}

		if (authorityMap.containsKey(authority.getId())) {
			throw new AlreadyExistsException(authority.toString());
		}

		if (authority.getId().equals(authority.getParentId())) {
			throw new RuntimeException(
					"ID and parentid cannot be the same：" + JSONUtils.getJsonSupport().toJSONString(authority));
		}

		if (logger.isTraceEnabled()) {
			logger.trace("register authority:{}", JSONUtils.getJsonSupport().toJSONString(authority));
		}

		authorityMap.put(authority.getId(), authority);
	}

	public List<T> getParentList(String id, Predicate<T> authorityFilter) {
		T t = getAuthority(id);
		if (t == null) {
			return Collections.emptyList();
		}

		List<T> list = new ArrayList<T>();
		T p = getAuthority(t.getParentId());
		while (p != null) {
			if (acceptInternal(p, authorityFilter)) {
				list.add(p);
			}
			p = getAuthority(p.getParentId());
		}
		return list;
	}

	public List<T> getRelationAuthorityList(Collection<String> ids, Predicate<T> authorityFilter) {
		Set<String> useIds = getRelationIds(ids, authorityFilter);
		if (CollectionUtils.isEmpty(useIds)) {
			return Collections.emptyList();
		}

		List<T> list = new ArrayList<T>();
		for (String id : useIds) {
			T t = getAuthority(id);
			if (t != null && acceptInternal(t, authorityFilter)) {
				list.add(t);
			}
		}
		return list;
	}

	protected List<T> getRootList(Set<String> ids) {
		List<T> list = new ArrayList<T>();
		for (String id : ids) {
			T t = getAuthority(id);
			if (t == null) {
				continue;
			}

			if (t.getParentId() == null || !ids.contains(t.getParentId())) {
				list.add(t);
			}
		}
		return list;
	}

	private Set<String> getRelationIds(Collection<String> ids, Predicate<T> authorityFilter) {
		LinkedHashSet<String> useIds = new LinkedHashSet<String>(ids);
		for (String id : ids) {
			List<T> list = getParentList(id, authorityFilter);
			if (!CollectionUtils.isEmpty(list)) {
				for (T t : list) {
					useIds.add(t.getId());
				}
			}
		}
		return useIds;
	}

	public List<AuthorityTree<T>> getRelationAuthorityTreeList(Collection<String> ids, Predicate<T> authorityFilter) {
		Set<String> useIds = getRelationIds(ids, authorityFilter);
		if (CollectionUtils.isEmpty(useIds)) {
			return Collections.emptyList();
		}

		return getAuthorityTreeList(getRootList(useIds), useIds, authorityFilter);
	}

	protected List<AuthorityTree<T>> getAuthorityTreeList(Collection<T> rootList, final Set<String> ids,
			final Predicate<T> authorityFilter) {
		List<AuthorityTree<T>> list = new ArrayList<AuthorityTree<T>>();
		for (T t : rootList) {
			List<T> subList = getAuthoritySubList(t.getId(), new Predicate<T>() {

				public boolean test(T authority) {
					if (!ids.contains(authority.getId())) {
						return false;
					}
					return acceptInternal(authority, authorityFilter);
				}
			});

			list.add(new AuthorityTree<T>(t, getAuthorityTreeList(subList, ids, authorityFilter)));
		}
		return list;
	}

	@Override
	public void remove(T authority) {
		authorityMap.remove(authority.getId());
	}
}
