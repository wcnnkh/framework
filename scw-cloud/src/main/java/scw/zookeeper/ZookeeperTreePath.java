package scw.zookeeper;

import java.io.Serializable;
import java.util.List;

import scw.mapper.MapperUtils;

public class ZookeeperTreePath implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String path;
	private List<ZookeeperTreePath> childrens;

	public ZookeeperTreePath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public List<ZookeeperTreePath> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<ZookeeperTreePath> childrens) {
		this.childrens = childrens;
	}

	@Override
	public String toString() {
		return MapperUtils.getMapper().toString(this);
	}
}
