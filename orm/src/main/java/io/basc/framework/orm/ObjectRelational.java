package io.basc.framework.orm;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import io.basc.framework.core.Structure;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.StructureDecorator;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public class ObjectRelational<T extends Property> extends StructureDecorator<T, ObjectRelational<T>> {
	private static Logger logger = LoggerFactory.getLogger(ObjectRelational.class);
	protected ObjectRelationalResolver objectRelationalResolver;
	protected String comment;
	protected String charsetName;

	public ObjectRelational(Class<?> sourceClass, ObjectRelationalResolver objectRelationalResolver, T parent,
			Function<Class<?>, ? extends Elements<T>> processor) {
		super(sourceClass, parent, (s) -> {
			Elements<T> stream = processor.apply(s);
			if (stream == null) {
				return Elements.empty();
			}
			return stream.filter((o) -> o.isSupportGetter() || o.isSupportSetter())
					.filter((o) -> !Modifier.isStatic(o.getModifiers()));
		});
		this.objectRelationalResolver = objectRelationalResolver;
	}

	public ObjectRelational(Structure<T> members) {
		super(members);
		if (members instanceof ObjectRelational) {
			this.comment = ((ObjectRelational<?>) members).comment;
			this.charsetName = ((ObjectRelational<?>) members).charsetName;
			this.objectRelationalResolver = ((ObjectRelational<?>) members).objectRelationalResolver;
		}
	}

	public ObjectRelational(Structure<? extends Field> members, Function<? super Field, ? extends T> map) {
		super(members, map);
		if (members instanceof ObjectRelational) {
			this.comment = ((ObjectRelational<?>) members).comment;
			this.charsetName = ((ObjectRelational<?>) members).charsetName;
			this.objectRelationalResolver = ((ObjectRelational<?>) members).objectRelationalResolver;
		}
	}

	public ObjectRelationalResolver getObjectRelationalResolver() {
		return objectRelationalResolver;
	}

	@Override
	protected T map(T source) {
		T t = super.map(source);
		if (t == null) {
			return t;
		}

		if (this.objectRelationalResolver != null && t.objectRelationalResolver == null) {
			if (t == source) {
				t = clone(t);
			}

			t.objectRelationalResolver = this.objectRelationalResolver;
		}
		return t;
	}

	protected ObjectRelational<T> decorate(Mapping<T> structure) {
		ObjectRelational<T> objectRelational = new ObjectRelational<T>(structure);
		if (objectRelational.objectRelationalResolver == null) {
			objectRelational.objectRelationalResolver = this.objectRelationalResolver;
		}

		if (objectRelational.comment == null) {
			objectRelational.comment = this.comment;
		}

		if (objectRelational.charsetName == null) {
			objectRelational.charsetName = this.charsetName;
		}
		return objectRelational;
	}

	public ObjectRelational<T> setObjectRelationalResolver(ObjectRelationalResolver objectRelationalResolver) {
		Structure<T> members = map((e) -> {
			T t = clone(e);
			t.setObjectRelationalResolver(objectRelationalResolver);
			return t;
		});
		Mapping<T> structure = decorate(members);
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

	public Elements<T> columns() {
		return all().getElements().filter((e) -> !e.isEntity()).toList();
	}

	public final Elements<T> getPrimaryKeys() {
		return columns().filter((e) -> e.isPrimaryKey()).toList();
	}

	public final Elements<T> getNotPrimaryKeys() {
		return columns().filter((e) -> !e.isPrimaryKey()).toList();
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

		Structure<T> members = map((e) -> {
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

	public <E extends Throwable> ObjectRelational<T> withEntitys() {
		return withEntitysAfter((e) -> e.withSuperclass().setNameNestingDepth(0).all());
	}

	public ObjectRelational<T> withEntitysAfter(
			Function<? super ObjectRelational<T>, ? extends ObjectRelational<T>> processor) {
		if (processor == null) {
			return this;
		}

		return withEntitys((e) -> {
			if (!e.isSupportSetter()) {
				return null;
			}

			ObjectRelational<T> objectRelational = jumpTo(e.getSetter().getType());
			if (objectRelational == null) {
				return null;
			}

			return processor.apply(objectRelational);
		});
	}

	public ObjectRelational<T> withEntitys(Function<? super T, ? extends ObjectRelational<T>> processor) {
		if (processor == null) {
			return this;
		}

		List<Elements<T>> withs = new LinkedList<>();
		ObjectRelational<T> objectRelational = this.filter((property) -> {
			if (property == null || !property.isEntity()) {
				return true;
			}

			ObjectRelational<T> with = processor.apply(property);
			if (with == null) {
				return true;
			}

			if (logger.isTraceEnabled()) {
				logger.trace("with entity[{}] for property[]", property.getDeclaringClass(), property.getName());
			}
			withs.add(with.setParent(property).withEntitys(processor).getElements());
			return false;
		}).shared();// 此处因为在filter中进行了逻辑处理，所以此处需要执行shared防止重复执行

		for (Elements<T> with : withs) {
			objectRelational = objectRelational.concat(with);
		}
		return objectRelational;
	}
}
