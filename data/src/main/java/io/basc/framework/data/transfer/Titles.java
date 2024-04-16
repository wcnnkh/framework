package io.basc.framework.data.transfer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.util.element.Elements;

public class Titles {
	private Map<Integer, List<String>> titleMap = new HashMap<>();

	public Elements<String> names() {
		
		
		return Elements.of(titleMap.keySet());
	}

	public boolean isEmpty() {
		return titleMap.isEmpty();
	}

	public int size() {
		return titleMap.size();
	}
}
