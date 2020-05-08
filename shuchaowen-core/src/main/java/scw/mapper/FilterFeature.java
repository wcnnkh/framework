package scw.mapper;

import java.lang.reflect.Modifier;

public enum FilterFeature {
	SUPPORT_GETTER(new SupportGetterFieldFilter()), 
	SUPPORT_SETTER(new SupportSetterFieldFilter()),
	GETTER_PUBLIC(new GetterPublicFieldFilter()),
	SETTER_PUBLIC(new SetterPublicFieldFilter()),
	GETTER_IGNORE_STATIC(new IgnoreGetterStaticFieldFilter()),
	SETTER_IGNORE_STATIC(new IgnoreSetterStaticFieldFilter()),
	GETTER_IGNORE_TRANSIENT(new IgnoreGetterTransientFieldFilter()),
	SETTER_IGNORE_TRANSIENT(new IgnoreSetterTransientFieldFilter()),
	IGNORE_STATIC(new IgnoreStaticFieldFilter())
	;

	private final FieldFilter filter;

	private FilterFeature(FieldFilter filter) {
		this.filter = filter;
	}

	public FieldFilter getFilter() {
		return filter;
	}
	
	private static final class IgnoreStaticFieldFilter implements FieldFilter{

		public boolean accept(Field field) {
			if(field.isSupportGetter() && Modifier.isStatic(field.getGetter().getModifiers())){
				return false;
			}
			
			if(field.isSupportSetter() && Modifier.isStatic(field.getSetter().getModifiers())){
				return false;
			}
			return true;
		}
		
	}
	
	private static final class IgnoreGetterTransientFieldFilter implements FieldFilter {

		public boolean accept(Field field) {
			return field.isSupportGetter() && !Modifier.isTransient(field.getGetter().getModifiers());
		}
	}
	
	private static final class IgnoreSetterTransientFieldFilter implements FieldFilter {

		public boolean accept(Field field) {
			return field.isSupportSetter() && !Modifier.isTransient(field.getSetter().getModifiers());
		}
	}
	
	private static final class IgnoreGetterStaticFieldFilter implements FieldFilter {

		public boolean accept(Field field) {
			return field.isSupportGetter() && !Modifier.isStatic(field.getGetter().getModifiers());
		}
	}
	
	private static final class IgnoreSetterStaticFieldFilter implements FieldFilter {

		public boolean accept(Field field) {
			return field.isSupportSetter() && !Modifier.isStatic(field.getSetter().getModifiers());
		}
	}

	private static final class SupportGetterFieldFilter implements FieldFilter {

		public boolean accept(Field field) {
			return field.isSupportGetter();
		}
	}

	private static final class SupportSetterFieldFilter implements FieldFilter {

		public boolean accept(Field field) {
			return field.isSupportSetter();
		}
	}

	private static final class SetterPublicFieldFilter implements FieldFilter {

		public boolean accept(Field field) {
			return field.isSupportSetter()
					&& Modifier.isPublic(field.getSetter().getModifiers());
		}
	}

	private static final class GetterPublicFieldFilter implements FieldFilter {

		public boolean accept(Field field) {
			return field.isSupportGetter()
					&& Modifier.isPublic(field.getGetter().getModifiers());
		}
	}
}
