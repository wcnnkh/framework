package io.basc.framework.orm;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.core.Members;
import io.basc.framework.mapper.Structure;
import io.basc.framework.mapper.StructureDecorator;
import io.basc.framework.util.StringUtils;

public class ObjectRelational<T extends Property> extends StructureDecorator<T, ObjectRelational<T>> {
	protected ObjectRelationalResolver objectRelationalResolver;
	protected String comment;
	protected String charsetName;

	public ObjectRelational(Class<?> sourceClass, ObjectRelationalResolver objectRelationalResolver,
			Function<Class<?>, ? extends Stream<T>> processor) {
		super(sourceClass, processor.andThen((e) -> e.filter((o) -> o.isSupportGetter() || o.isSupportSetter())));
		this.objectRelationalResolver = objectRelationalResolver;
	}

	public ObjectRelational(ObjectRelational<T> members) {
		super(members);
		this.objectRelationalResolver = members.objectRelationalResolver;
	}

	public ObjectRelational(Structure<T> members) {
		super(members);
	}

	public ObjectRelationalResolver getObjectRelationalResolver() {
		return objectRelationalResolver;
	}

	@Override
	public <R extends T> ObjectRelational<R> mapStructure(Function<? super T, R> map) {
		Structure<R> relational = super.mapStructure(map);
		return new ObjectRelational<R>(relational);
	}

	@Override
	protected ObjectRelational<T> decorate(Structure<T> structure) {
		ObjectRelational<T> objectRelational = new ObjectRelational<T>(structure);
		objectRelational.objectRelationalResolver = this.objectRelationalResolver;
		objectRelational.comment = this.comment;
		return objectRelational;
	}

	public ObjectRelational<T> setObjectRelationalResolver(ObjectRelationalResolver objectRelationalResolver) {
		if (objectRelationalResolver == this.objectRelationalResolver) {
			return this;
		}

		Members<T> members = map((e) -> {
			T t = clone(e);
			t.setObjectRelationalResolver(objectRelationalResolver);
			return t;
		});
		Structure<T> structure = decorate(members);
		return decorate(structure);
	}

	@Override
	public String getName() {
		if (StringUtils.isEmpty(this.name) && this.objectRelationalResolver != null) {
			return objectRelationalResolver.getName(getSourceClass());
		}
		return super.getName();
	}

	@Override
	public Collection<String> getAliasNames() {
		if (this.aliasNames == null && this.objectRelationalResolver != null) {
			return this.objectRelationalResolver.getAliasNames(getSourceClass());
		}
		return super.getAliasNames();
	}

	public Stream<T> columns() {
		return stream().filter((e) -> !e.isEntity());
	}

	public final List<T> getPrimaryKeys() {
		return columns().filter((e) -> e.isPrimaryKey()).collect(Collectors.toList());
	}

	public final List<T> getNotPrimaryKeys() {
		return columns().filter((e) -> !e.isPrimaryKey()).collect(Collectors.toList());
	}

	@Override
	public ObjectRelational<T> setParent(T parent) {
		if (parent != null && parent.objectRelationalResolver == null) {
			parent.objectRelationalResolver = this.objectRelationalResolver;
		}
		return super.setParent(parent);
	}

	public ObjectRelational<T> setParentProperty(Property parent) {
		if (parent == null) {
			return decorate(this);
		}

		Members<T> members = map((e) -> {
			T t = clone(e);
			t.setParent(parent);
			return t;
		});
		return decorate(decorate(members));
	}

	public String getComment() {
		if (StringUtils.isEmpty(comment) && objectRelationalResolver != null) {
			return this.objectRelationalResolver.getComment(getSourceClass());
		}
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCharsetName() {
		if (StringUtils.isEmpty(charsetName) && objectRelationalResolver != null) {
			return this.objectRelationalResolver.getCharsetName(getSourceClass());
		}
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}
}
