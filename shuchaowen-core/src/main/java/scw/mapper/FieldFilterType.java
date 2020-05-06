package scw.mapper;

import java.lang.reflect.Modifier;

public enum FieldFilterType {
	SUPPORT_GETTER(new SupportGetterFieldContextFilter()), 
	SUPPORT_SETTER(new SupportSetterFieldContextFilter()),
	GETTER_PUBLIC(new GetterPublicFieldContextFilter()),
	SETTER_PUBLIC(new SetterPublicFieldContextFilter()),
	GETTER_IGNORE_STATIC(new IgnoreGetterStaticFieldContextFilter()),
	SETTER_IGNORE_STATIC(new IgnoreSetterStaticFieldContextFilter()),
	GETTER_IGNORE_TRANSIENT(new IgnoreGetterTransientFieldContextFilter()),
	SETTER_IGNORE_TRANSIENT(new IgnoreSetterTransientFieldContextFilter()),
	;

	private final FieldContextFilter filter;

	private FieldFilterType(FieldContextFilter filter) {
		this.filter = filter;
	}

	public FieldContextFilter getFilter() {
		return filter;
	}
	
	private static final class IgnoreGetterTransientFieldContextFilter implements FieldContextFilter {

		public boolean accept(FieldContext fieldContext) {
			return fieldContext.getField().isSupportGetter() && !Modifier.isTransient(fieldContext.getField().getGetter().getModifiers());
		}
	}
	
	private static final class IgnoreSetterTransientFieldContextFilter implements FieldContextFilter {

		public boolean accept(FieldContext fieldContext) {
			return fieldContext.getField().isSupportSetter() && !Modifier.isTransient(fieldContext.getField().getSetter().getModifiers());
		}
	}
	
	private static final class IgnoreGetterStaticFieldContextFilter implements FieldContextFilter {

		public boolean accept(FieldContext fieldContext) {
			return fieldContext.getField().isSupportGetter() && !Modifier.isStatic(fieldContext.getField().getGetter().getModifiers());
		}
	}
	
	private static final class IgnoreSetterStaticFieldContextFilter implements FieldContextFilter {

		public boolean accept(FieldContext fieldContext) {
			return fieldContext.getField().isSupportSetter() && !Modifier.isStatic(fieldContext.getField().getSetter().getModifiers());
		}
	}

	private static final class SupportGetterFieldContextFilter implements FieldContextFilter {

		public boolean accept(FieldContext fieldContext) {
			return fieldContext.getField().isSupportGetter();
		}
	}

	private static final class SupportSetterFieldContextFilter implements FieldContextFilter {

		public boolean accept(FieldContext fieldContext) {
			return fieldContext.getField().isSupportSetter();
		}
	}

	private static final class SetterPublicFieldContextFilter implements FieldContextFilter {

		public boolean accept(FieldContext fieldContext) {
			return fieldContext.getField().isSupportSetter()
					&& Modifier.isPublic(fieldContext.getField().getSetter().getModifiers());
		}
	}

	private static final class GetterPublicFieldContextFilter implements FieldContextFilter {

		public boolean accept(FieldContext fieldContext) {
			return fieldContext.getField().isSupportGetter()
					&& Modifier.isPublic(fieldContext.getField().getGetter().getModifiers());
		}
	}
}
