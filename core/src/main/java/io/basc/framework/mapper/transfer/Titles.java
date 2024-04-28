package io.basc.framework.mapper.transfer;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Item;
import io.basc.framework.mapper.support.ItemRegistry;
import io.basc.framework.mapper.support.StandardItem;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;

public class Titles extends ItemRegistry<StandardItem> {

	public boolean isEmpty() {
		return getSize() == 0;
	}

	/**
	 * 设置标题
	 * 
	 * @param index
	 * @param name
	 * @param aliasNames
	 * @return
	 */
	public Titles setTitle(int index, String name, @Nullable Elements<String> aliasNames) {
		// 先删除所有相关的标题
		getRegistrations().filter((e) -> e.getElement().getPositionIndex() == index).forEach((e) -> e.unregister());
		StandardItem item = new StandardItem();
		item.setPositionIndex(index);
		item.setName(name);
		if (aliasNames != null) {
			item.setAliasNames(aliasNames);
		}
		register(item);
		return this;
	}

	/**
	 * 添加标题
	 * 
	 * @param name
	 * @param aliasNames
	 * @return
	 */
	public Titles addTitle(String name, @Nullable Elements<String> aliasNames) {
		StandardItem item = new StandardItem();
		item.setPositionIndex(getSize());
		item.setName(name);
		if (aliasNames != null) {
			item.setAliasNames(aliasNames);
		}
		register(item);
		return this;
	}

	/**
	 * 设置标题
	 * 
	 * @param titles
	 */
	public void setTitles(Elements<String> titles) {
		Elements<StandardItem> items = titles.map((e) -> {
			StandardItem item = new StandardItem();
			item.setName(e);
			return item;
		});

		clear();
		registers(items);
	}

	public boolean setItems(Elements<? extends Item> items) {
		Elements<StandardItem> elements = items.filter((e) -> StringUtils.isNotEmpty(e.getName())).map((e) -> {
			StandardItem item = new StandardItem();
			item.setName(e.getName());
			item.setPositionIndex(e.getPositionIndex());
			item.setAliasNames(e.getAliasNames());
			return item;
		});

		if (elements.isEmpty()) {
			return false;
		}

		clear();
		registers(elements);
		return true;
	}
}
