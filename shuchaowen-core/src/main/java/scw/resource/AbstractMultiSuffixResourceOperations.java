package scw.resource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import scw.core.utils.ArrayUtils;

public abstract class AbstractMultiSuffixResourceOperations extends AbstractMultiResourceOperations {
	private ResourceLookup resourceLookup;

	public AbstractMultiSuffixResourceOperations(ResourceLookup resourceLookup) {
		this.resourceLookup = resourceLookup;
	}

	public abstract String[] getSuffixs();

	@Override
	public final List<String> getResourceNameList(String resource) {
		String[] suffixs = getSuffixs();
		if (ArrayUtils.isEmpty(suffixs)) {
			return Arrays.asList(resource);
		}

		List<String> list = new LinkedList<String>();
		for (String name : suffixs) {
			list.add(getTestFileName(resource, name));
		}
		list.add(resource);
		return list;
	}

	@Override
	public final ResourceLookup getTargetResourceLookup() {
		return resourceLookup;
	}

	private static String getTestFileName(String fileName, String str) {
		int index = fileName.lastIndexOf(".");
		if (index == -1) {// 不存在
			return fileName + str;
		} else {
			return fileName.substring(0, index) + str + fileName.substring(index);
		}
	};
}
