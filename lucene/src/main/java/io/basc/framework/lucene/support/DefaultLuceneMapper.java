package io.basc.framework.lucene.support;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.lucene.LuceneException;
import io.basc.framework.lucene.LuceneMapper;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.SimpleObjectMapper;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.StructureRegistry;
import io.basc.framework.orm.repository.DefaultRepositoryMapper;
import io.basc.framework.orm.support.SimpleStructureRegistry;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.Value;

import java.util.Collection;

import org.apache.lucene.document.Document;

public class DefaultLuceneMapper extends DefaultRepositoryMapper implements
		LuceneMapper {
	private final ObjectMapper<Document, LuceneException> objectMapper = new SimpleObjectMapper<Document, LuceneException>();
	private final StructureRegistry<EntityStructure<? extends Property>> structureRegistry = new SimpleStructureRegistry<EntityStructure<? extends Property>>();
	private final ConfigurableServices<LuceneResolverExtend> luceneResolverExtends = new ConfigurableServices<LuceneResolverExtend>(
			LuceneResolverExtend.class);

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		luceneResolverExtends.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	public ConfigurableServices<LuceneResolverExtend> getLuceneResolverExtends() {
		return luceneResolverExtends;
	}

	@Override
	public boolean isStructureRegistred(Class<?> entityClass) {
		return structureRegistry.isStructureRegistred(entityClass);
	}

	@Override
	public EntityStructure<? extends Property> getStructure(Class<?> entityClass) {
		EntityStructure<? extends Property> structure = structureRegistry
				.getStructure(entityClass);
		if (structure == null) {
			return super.getStructure(entityClass);
		}
		return structure;
	}

	@Override
	public void registerStructure(Class<?> entityClass,
			EntityStructure<? extends Property> structure) {
		structureRegistry.registerStructure(entityClass, structure);
	}

	@Override
	public boolean isMapperRegistred(Class<?> type) {
		return objectMapper.isMapperRegistred(type);
	}

	@Override
	public <T> Processor<Document, T, LuceneException> getMappingProcessor(
			Class<? extends T> type) {
		return objectMapper.getMappingProcessor(type);
	}

	@Override
	public <T> void registerMapper(
			Class<T> type,
			Processor<Document, ? extends T, ? extends LuceneException> processor) {
		objectMapper.registerMapper(type, processor);
	}

	@Override
	public Collection<org.apache.lucene.document.Field> resolve(
			ParameterDescriptor descriptor, Value value) {
		return LuceneResolverExtendChain
				.build(luceneResolverExtends.iterator()).resolve(descriptor,
						value);
	}

}
