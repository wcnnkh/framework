package io.basc.framework.lucene;

import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.mapper.Structure;
import io.basc.framework.mapper.StructureFactory;
import io.basc.framework.orm.ObjectMapper;
import io.basc.framework.orm.ObjectRelational;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.RepositoryColumn;
import io.basc.framework.orm.repository.RepositoryMapper;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

public interface LuceneMapper extends RepositoryMapper, LuceneResolver, ObjectMapper<Document, LuceneException>,
		StructureFactory<ObjectRelational<? extends Property>> {

	@Override
	public default ObjectRelational<? extends Property> getStructure(Class<?> entityClass) {
		return RepositoryMapper.super.getStructure(entityClass);
	}

	Query parseQuery(Conditions conditions);

	Query parseQuery(Document document);

	Sort parseSort(Structure<? extends Property> structure, List<? extends OrderColumn> orders);

	@Override
	default void reverseTransform(Object value, ParameterDescriptor descriptor, Document target,
			TypeDescriptor targetType) throws LuceneException {
		target.removeField(descriptor.getName());
		Value v;
		if (Value.isBaseType(descriptor.getType())) {
			v = new AnyValue(descriptor, Sys.env.getConversionService());
		} else {
			v = new StringValue(JSONUtils.getJsonSupport().toJSONString(value));
		}
		resolve(descriptor, v).forEach((f) -> target.add(f));
	}

	@Override
	default Processor<Property, Object, LuceneException> getValueProcessor(Document source, TypeDescriptor sourceType)
			throws LuceneException {
		return (e) -> e.getValueByNames((name) -> source.get(name));
	}

	default void reverseTransform(RepositoryColumn source, Document target) throws LuceneException {
		reverseTransform(source.getValue(), source, target);
	}

	default void reverseTransform(Collection<? extends RepositoryColumn> source, Document target)
			throws LuceneException {
		for (RepositoryColumn column : source) {
			reverseTransform(column, target);
		}
	}

	default Document createDocument(Object instance) {
		return invert(instance, Document.class);
	}
}
