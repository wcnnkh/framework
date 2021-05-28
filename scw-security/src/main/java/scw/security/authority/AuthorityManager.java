package scw.security.authority;

import java.util.Collection;
import java.util.List;

import scw.util.Accept;

public interface AuthorityManager<T extends Authority> {
	void register(T authority);
	
	void remove(T authority);
	
	T getAuthority(String id);

	/**
	 * 从全部中筛选结果
	 * @param authorityFilter
	 * @return
	 */
	List<T> getAuthorityList(Accept<T> authorityFilter);
	
	/**
	 * 从根信息中筛选结果
	 * @param authorityFilter
	 * @return
	 */
	List<T> getRootList(Accept<T> authorityFilter);
	
	/**
	 * 从全部中筛选结果，并返回树结构
	 * @param authorityFilter
	 * @return
	 */
	List<AuthorityTree<T>> getAuthorityTreeList(Accept<T> authorityFilter);

	/**
	 * 从子集中筛选结果
	 * @param id
	 * @param authorityFilter
	 * @return
	 */
	List<T> getAuthoritySubList(String id, Accept<T> authorityFilter);
	
	/**
	 * 从父级列表筛选结果
	 * @param id
	 * @param authorityFilter
	 * @return
	 */
	List<T> getParentList(String id, Accept<T> authorityFilter);

	/**
	 * 从子集中筛选结果，并返回树结构
	 * @param id
	 * @param authorityFilter
	 * @return
	 */
	List<AuthorityTree<T>> getAuthoritySubTreeList(String id, Accept<T> authorityFilter);

	/**
	 * 获取关联的结果，并组成树结构
	 * @param ids
	 * @param authorityFilter
	 * @return
	 */
	List<AuthorityTree<T>> getRelationAuthorityTreeList(Collection<String> ids, Accept<T> authorityFilter);
	
	/**
	 * 获取关联的结果
	 * @param ids
	 * @param authorityFilter
	 * @return
	 */
	List<T> getRelationAuthorityList(Collection<String> ids, Accept<T> authorityFilter);
}