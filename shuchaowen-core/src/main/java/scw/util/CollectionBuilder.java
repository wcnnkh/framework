package scw.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import scw.core.Constants;
import scw.core.utils.CollectionUtils;
import scw.io.ResourceUtils;
import scw.value.StringValue;
import scw.value.Value;

public class CollectionBuilder implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Collection<Value> values = createValues();

	protected Collection<Value> createValues() {
		return new LinkedList<Value>();
	}

	public Collection<Value> getValues() {
		return Collections.unmodifiableCollection(values);
	}

	public void loading(String resource) {
		loading(resource, true);
	}

	public void loading(String resource, boolean trim) {
		loading(resource, Constants.DEFAULT_CHARSET_NAME, trim);
	}

	public void loading(String resource, String charsetName, boolean trim) {
		List<String> list = ResourceUtils.getResourceOperations().getLines(resource, charsetName);
		if (CollectionUtils.isEmpty(list)) {
			return;
		}

		for (String value : list) {
			values.add(new StringValue(trim ? value.trim() : value));
		}
	}
}
