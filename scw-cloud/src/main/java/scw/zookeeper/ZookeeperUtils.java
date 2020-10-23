package scw.zookeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;

public final class ZookeeperUtils {
	private static final String PATH_PREFIX = "/";

	public static String toPath(String path) {
		return path == null ? PATH_PREFIX : ((path.startsWith(PATH_PREFIX) ? "" : PATH_PREFIX) + path);
	}

	public static String concatPath(String left, String right) {
		String leftToUse = toPath(left);
		if (StringUtils.isEmpty(right)) {
			return leftToUse;
		}

		if (!leftToUse.endsWith(PATH_PREFIX)) {
			leftToUse = leftToUse + PATH_PREFIX;
		}

		return leftToUse + (right.startsWith(PATH_PREFIX) ? right.substring(PATH_PREFIX.length()) : right);
	}

	public static boolean isExist(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
		return zooKeeper.exists(toPath(path), false) != null;
	}

	public static List<String> getChildren(ZooKeeper zooKeeper, String path)
			throws KeeperException, InterruptedException {
		String pathToUse = toPath(path);
		Stat stat = zooKeeper.exists(pathToUse, false);
		if (stat == null) {
			return Collections.emptyList();
		}

		return zooKeeper.getChildren(pathToUse, false);
	}

	public static List<ZookeeperTreePath> getChildrenTreePaths(ZooKeeper zooKeeper, String parent)
			throws KeeperException, InterruptedException {
		String pathToUse = toPath(parent);
		List<String> childrens = getChildren(zooKeeper, pathToUse);
		if (CollectionUtils.isEmpty(childrens)) {
			return Collections.emptyList();
		}

		List<ZookeeperTreePath> paths = new ArrayList<ZookeeperTreePath>();
		for (String path : childrens) {
			pathToUse = concatPath(pathToUse, path);
			ZookeeperTreePath zookeeperTreePath = new ZookeeperTreePath(pathToUse);
			zookeeperTreePath.setChildrens(getChildrenTreePaths(zooKeeper, pathToUse));
			paths.add(zookeeperTreePath);
		}
		return paths;
	}
}
