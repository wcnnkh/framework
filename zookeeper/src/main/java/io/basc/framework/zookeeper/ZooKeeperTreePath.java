package io.basc.framework.zookeeper;

import io.basc.framework.mapper.MapperUtils;

import java.io.Serializable;
import java.util.List;

public class ZooKeeperTreePath implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String path;
	private List<ZooKeeperTreePath> childrens;

	public ZooKeeperTreePath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public List<ZooKeeperTreePath> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<ZooKeeperTreePath> childrens) {
		this.childrens = childrens;
	}

	@Override
	public String toString() {
		return MapperUtils.toString(this);
	}
}
