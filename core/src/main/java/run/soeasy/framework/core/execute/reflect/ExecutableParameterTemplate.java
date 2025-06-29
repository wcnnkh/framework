package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.execute.ParameterTemplate;
import run.soeasy.framework.core.transform.property.PropertyDescriptor;

@EqualsAndHashCode(of = "executable")
@ToString(of = "executable")
@RequiredArgsConstructor
public class ExecutableParameterTemplate implements ParameterTemplate, Provider<PropertyDescriptor> {
	@NonNull
	@Getter
	private final Executable executable;
	@Getter
	@Setter
	private ParameterNameDiscoverer parameterNameDiscoverer = SystemParameterNameDiscoverer.getInstance();
	private volatile String[] names;
	private volatile PropertyDescriptor[] parameterDescriptors;

	@Override
	public void reload() {
		reload(true);
	}

	public boolean reload(boolean force) {
		if (force || parameterDescriptors == null) {
			synchronized (this) {
				if (force || parameterDescriptors == null) {
					Parameter[] parameters = executable.getParameters();
					if (parameters.length == 0) {
						parameterDescriptors = PropertyDescriptor.EMPTY_ARRAY;
					} else {
						names = parameterNameDiscoverer.getParameterNames(executable);
						PropertyDescriptor[] array = new PropertyDescriptor[parameters.length];
						for (int i = 0; i < parameters.length; i++) {
							PropertyDescriptor parameterDescriptor = new ExecutableParameterDescriptor(parameters[i]);
							if (names != null && i < names.length && names[i] != null) {
								parameterDescriptor = parameterDescriptor.rename(names[i]);
							}
							array[i] = parameterDescriptor;
						}
						parameterDescriptors = array;
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public PropertyDescriptor get(int index) {
		reload(false);
		return parameterDescriptors[index];
	}

	@Override
	public Iterator<PropertyDescriptor> iterator() {
		reload(false);
		return Arrays.asList(parameterDescriptors).iterator();
	}

	@Override
	public final Stream<PropertyDescriptor> stream() {
		return Provider.super.stream();
	}

}
