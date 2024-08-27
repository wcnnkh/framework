package io.basc.framework.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public interface Items<T extends Item> {
	/**
	 * 通过索引位置获取元素
	 * 
	 * @param index
	 * @return
	 */
	default T getElement(int index) {
		if (index < 0) {
			return null;
		}

		return getElements().filter((e) -> e.getPositionIndex() == index).findFirst()
				.orElseGet(() -> getElements().get(index));
	}

	/**
	 * 获取所有元素
	 * 
	 * @return
	 */
	Elements<T> getElements();

	/**
	 * 可能存在多个重名的element
	 * 
	 * @see Item#getName()
	 * @param name
	 * @return
	 */
	default Elements<T> getElements(String name) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		return getElements().filter((item) -> item.getName().equals(name) || item.getAliasNames().contains(name));
	}

	default Elements<T> getElements(Item item) {
		Elements<T> elements = getElements(item.getName());
		if (!elements.isEmpty()) {
			return elements;
		}

		elements = item.getAliasNames().map((name) -> getElements(name)).filter((e) -> !e.isEmpty()).first();
		if (elements != null) {
			return elements;
		}

		T element = getElement(item.getPositionIndex());
		return element == null ? Elements.empty() : Elements.singleton(element);
	}

	default int getNumberOfElements() {
		return (int) getElements().count();
	}

	/**
	 * 获取唯一的元素
	 * 
	 * @param name
	 * @see #isUnique()
	 * @return
	 * @throws NoSuchElementException   没有元素
	 * @throws NoUniqueElementException 存在多个元素
	 */
	default T getUniqueElement(String name) throws NoSuchElementException, NoUniqueElementException {
		return getElements(name).getUnique();
	}

	/**
	 * 是否是唯一元素
	 * 
	 * @param name
	 * @return
	 */
	default boolean isUniqueElement(String name) {
		return getElements(name).isUnique();
	}

	default Map<String, T> getMap(String prefix) {
		Assert.requiredArgument(prefix != null, "prefix");
		Map<String, T> map = new LinkedHashMap<>();
		for (T item : getElements()) {
			String name = item.getName();
			if (name.length() > prefix.length() && name.startsWith(prefix)) {
				name = name.substring(prefix.length());
				map.put(name, item);
			}
		}
		return map;
	}
}
