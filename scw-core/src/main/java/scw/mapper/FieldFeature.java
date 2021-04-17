package scw.mapper;

import java.lang.reflect.Modifier;

import scw.lang.Ignore;
import scw.util.Accept;

public enum FieldFeature {
	SUPPORT_GETTER(new SupportGetterFieldFilter()), SUPPORT_SETTER(new SupportSetterFieldFilter()),
	GETTER_PUBLIC(new GetterPublicFieldFilter()), SETTER_PUBLIC(new SetterPublicFieldFilter()),
	GETTER_IGNORE_STATIC(new IgnoreGetterStaticFieldFilter()),
	SETTER_IGNORE_STATIC(new IgnoreSetterStaticFieldFilter()),
	GETTER_IGNORE_TRANSIENT(new IgnoreGetterTransientFieldFilter()),
	SETTER_IGNORE_TRANSIENT(new IgnoreSetterTransientFieldFilter()),
	/**
	 * 忽略静态字段
	 */
	IGNORE_STATIC(new IgnoreStaticFieldFilter()),
	/**
	 * 对象公有的setter字段，忽略static, final字段,必须存在实际的java.lang.Field
	 */
	SETTER(new SetterFieldFilter()),

	/**
	 * 对象公有的getter字段,忽略static字段
	 */
	GETTER(new GetterFieldFilter()),

	EXISTING_GETTER_FIELD(new ExistingFieldFilter(true)),

	EXISTING_SETTER_FIELD(new ExistingFieldFilter(false)),

	IGNORE_GETTER_FINAL(new IgnoreFinalFieldFilter(true)),

	IGNORE_SETTER_FINAL(new IgnoreFinalFieldFilter(false)),
	
	/**
	 * @see Ignore
	 */
	IGNORE_ANNOTATION(new SupportedIgnoreAnnotation()),
	;

	private final Accept<Field> accept;

	private FieldFeature(Accept<Field> accept) {
		this.accept = accept;
	}

	public Accept<Field> getAccept() {
		return accept;
	}

	private static final class IgnoreFinalFieldFilter implements Accept<Field> {
		private final boolean getter;

		public IgnoreFinalFieldFilter(boolean getter) {
			this.getter = getter;
		}

		@Override
		public boolean accept(Field field) {
			if (getter) {
				if (field.isSupportGetter() && field.getGetter().getField() != null
						&& Modifier.isFinal(field.getGetter().getField().getModifiers())) {
					return false;
				}
			} else {
				if (field.isSupportSetter() && field.getSetter().getField() != null
						&& Modifier.isFinal(field.getSetter().getField().getModifiers())) {
					return false;
				}
			}
			return true;
		}
	}
	
	private static final class SupportedIgnoreAnnotation implements Accept<Field>{
		@Override
		public boolean accept(Field e) {
			return e.getAnnotatedElement().getAnnotation(Ignore.class) != null;
		}
	}

	private static final class ExistingFieldFilter implements Accept<Field> {
		private final boolean getter;

		public ExistingFieldFilter(boolean getter) {
			this.getter = getter;
		}

		public boolean accept(Field field) {
			if (getter) {
				return field.isSupportGetter() && field.getGetter().getField() != null;
			} else {
				return field.isSupportSetter() && field.getSetter().getField() != null;
			}
		}
	}

	private static final class GetterFieldFilter implements Accept<Field> {
		public boolean accept(Field field) {
			return field.isSupportGetter() && !Modifier.isStatic(field.getGetter().getModifiers())
					&& Modifier.isPublic(field.getGetter().getModifiers());
		}
	}

	private static final class SetterFieldFilter implements Accept<Field> {

		public boolean accept(Field field) {
			return field.isSupportSetter() && field.getSetter().getField() != null
					&& !Modifier.isStatic(field.getSetter().getField().getModifiers())
					&& !Modifier.isFinal(field.getSetter().getField().getModifiers());
		}
	}

	private static final class IgnoreStaticFieldFilter implements Accept<Field> {

		public boolean accept(Field field) {
			if (field.isSupportGetter() && Modifier.isStatic(field.getGetter().getModifiers())) {
				return false;
			}

			if (field.isSupportSetter() && Modifier.isStatic(field.getSetter().getModifiers())) {
				return false;
			}
			return true;
		}

	}

	private static final class IgnoreGetterTransientFieldFilter implements Accept<Field> {

		public boolean accept(Field field) {
			return field.isSupportGetter() && !Modifier.isTransient(field.getGetter().getModifiers());
		}
	}

	private static final class IgnoreSetterTransientFieldFilter implements Accept<Field> {

		public boolean accept(Field field) {
			return field.isSupportSetter() && !Modifier.isTransient(field.getSetter().getModifiers());
		}
	}

	private static final class IgnoreGetterStaticFieldFilter implements Accept<Field> {

		public boolean accept(Field field) {
			return field.isSupportGetter() && !Modifier.isStatic(field.getGetter().getModifiers());
		}
	}

	private static final class IgnoreSetterStaticFieldFilter implements Accept<Field> {

		public boolean accept(Field field) {
			return field.isSupportSetter() && !Modifier.isStatic(field.getSetter().getModifiers());
		}
	}

	private static final class SupportGetterFieldFilter implements Accept<Field> {

		public boolean accept(Field field) {
			return field.isSupportGetter();
		}
	}

	private static final class SupportSetterFieldFilter implements Accept<Field> {

		public boolean accept(Field field) {
			return field.isSupportSetter();
		}
	}

	private static final class SetterPublicFieldFilter implements Accept<Field> {

		public boolean accept(Field field) {
			return field.isSupportSetter() && Modifier.isPublic(field.getSetter().getModifiers());
		}
	}

	private static final class GetterPublicFieldFilter implements Accept<Field> {

		public boolean accept(Field field) {
			return field.isSupportGetter() && Modifier.isPublic(field.getGetter().getModifiers());
		}
	}
}
