package run.soeasy.framework.core.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.function.Function;

import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.strings.StringUtils;
import run.soeasy.framework.core.type.Members;

public final class Fields extends ReflectionMembers<Field, Fields> {
	@SuppressWarnings("unchecked")
	public static <T> T clone(Elements<Field> fields, T source, boolean deep) {
		Assert.requiredArgument(fields != null, "fields");
		if (source == null) {
			return null;
		}

		T target = (T) ReflectionApi.newInstance(source.getClass());
		copy(fields, source, target, deep);
		return target;
	}

	public static <T> void copy(Elements<Field> fields, T source, T target, boolean deep) {
		Assert.requiredArgument(fields != null, "fields");
		if (source == null || target == null) {
			return;
		}

		fields.filter((e) -> !Modifier.isStatic(e.getModifiers())).forEach((f) -> {
			try {
				Object value = ReflectionUtils.get(f, source);
				if (value == source) {
					value = target;
				} else {
					value = ObjectUtils.clone(value, deep);
				}
				ReflectionUtils.set(f, target, value);
			} catch (Exception e) {
				throw new IllegalStateException("Should never get here", e);
			}
		});
	}

	public static <T> boolean equals(Elements<Field> fields, T left, T right, boolean deep) {
		Assert.requiredArgument(fields != null, "fields");
		if (left == right) {
			return true;
		}

		if (left == null || right == null) {
			return false;
		}

		Iterator<Field> iterator = fields.iterator();
		while (iterator.hasNext()) {
			Field field = iterator.next();
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			if (!ObjectUtils.equals(ReflectionUtils.get(field, left), ReflectionUtils.get(field, right), deep)) {
				return false;
			}
		}
		return true;
	}

	public static int hashCode(Elements<Field> fields, Object entity, boolean deep) {
		Assert.requiredArgument(fields != null, "fields");
		if (entity == null) {
			return 0;
		}

		int hashCode = 1;
		Iterator<Field> iterator = fields.iterator();
		while (iterator.hasNext()) {
			Field field = iterator.next();
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			hashCode = 31 * hashCode + ObjectUtils.hashCode(ReflectionUtils.get(field, entity), deep);
		}
		return hashCode;
	}

	public static String toString(Members<Field> structure, Object entity, boolean deep) {
		Assert.requiredArgument(structure != null, "structure");
		if (entity == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		toString(structure, builder, entity, deep);
		return builder.toString();
	}

	private static void toString(Members<Field> members, StringBuilder sb, Object entity, boolean deep) {
		if (entity == null) {
			return;
		}

		Iterator<Field> iterator = members.getElements().filter((e) -> !Modifier.isStatic(e.getModifiers())).iterator();
		if (!iterator.hasNext()) {
			return;
		}

		sb.append(members.getSource().getRawType().getSimpleName());
		sb.append('(');
		Members<Field> superStructure = members.getSuperclass();
		if (superStructure != null && superStructure.getSource().getRawType() != Object.class) {
			sb.append("super=");
			toString(superStructure, sb, entity, deep);
			if (iterator.hasNext()) {
				sb.append(',').append(' ');
			}
		}

		while (iterator.hasNext()) {
			Field field = iterator.next();
			sb.append(field.getName());
			sb.append('=');
			Object value = ReflectionUtils.get(field, entity);
			if (value == entity) {
				sb.append("(this)");
			} else {
				sb.append(ObjectUtils.toString(value, deep));
			}
			if (iterator.hasNext()) {
				sb.append(',').append(' ');
			}
		}
		sb.append(')');
	}

	private final Function<? super ReflectionMembers<Field, Fields>, ? extends Fields> structureDecorator = (
			source) -> new Fields(source);

	public Fields(Class<?> source, Function<? super Class<?>, ? extends Field[]> processor) {
		super(source, processor);
	}

	private Fields(Members<Field> members) {
		super(members);
	}

	public <T> T clone(T source) {
		return clone(source, false);
	}

	public <T> T clone(T source, boolean deep) {
		if (source == null) {
			return null;
		}

		return clone(getElements(), source, deep);
	}

	public <T> void copy(T source, T target) {
		copy(source, target, false);
	}

	public <T> void copy(T source, T target, boolean deep) {
		Assert.requiredArgument(target != null, "target");
		if (source == null) {
			return;
		}

		copy(getElements(), source, target, deep);
	}

	public <T> boolean equals(T left, T right) {
		return equals(left, right, true);
	}

	public <T> boolean equals(T left, T right, boolean deep) {
		if (left == right) {
			return true;
		}

		if (left == null || right == null) {
			return false;
		}

		return equals(getElements(), left, right, deep);
	}

	public Field find(String name) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		return getElements().filter((e) -> e.getName().equals(name)).first();
	}

	@Override
	public Function<? super ReflectionMembers<Field, Fields>, ? extends Fields> getMemberStructureDecorator() {
		return structureDecorator;
	}

	public int hashCode(Object entity) {
		return hashCode(entity, true);
	}

	public int hashCode(Object entity, boolean deep) {
		if (entity == null) {
			return 0;
		}
		return hashCode(getElements(), entity, deep);
	}

	public String toString(Object entity) {
		return toString(entity, true);
	}

	public String toString(Object instance, boolean deep) {
		return toString(this, instance, deep);
	}
}
