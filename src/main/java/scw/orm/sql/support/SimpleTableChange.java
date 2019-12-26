package scw.orm.sql.support;

import java.util.Collection;
import java.util.Collections;

import scw.core.utils.CollectionUtils;
import scw.orm.MappingContext;
import scw.orm.sql.TableChange;

public final class SimpleTableChange implements TableChange {
	private Collection<String> deleteNames;
	private Collection<MappingContext> addMappingContexts;

	@SuppressWarnings("unchecked")
	public SimpleTableChange(Collection<String> deleteNames, Collection<MappingContext> addMappingContexts) {
		this.deleteNames = CollectionUtils.isEmpty(deleteNames) ? Collections.EMPTY_LIST
				: Collections.unmodifiableCollection(deleteNames);
		this.addMappingContexts = CollectionUtils.isEmpty(addMappingContexts) ? Collections.EMPTY_LIST
				: Collections.unmodifiableCollection(addMappingContexts);
	}

	public Collection<String> getDeleteNames() {
		return deleteNames;
	}

	public Collection<MappingContext> getAddMappingContexts() {
		return addMappingContexts;
	}
}