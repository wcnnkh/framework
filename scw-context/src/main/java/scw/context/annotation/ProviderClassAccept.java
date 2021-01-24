package scw.context.annotation;

import scw.util.Accept;

public class ProviderClassAccept implements Accept<Class<?>> {
	public static final ProviderClassAccept INSTANCE = new ProviderClassAccept();

	public boolean accept(Class<?> e) {
		return e.getAnnotation(Provider.class) != null;
	}
}
