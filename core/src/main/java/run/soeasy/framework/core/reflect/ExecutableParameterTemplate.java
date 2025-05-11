package run.soeasy.framework.core.reflect;

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
import run.soeasy.framework.core.transform.indexed.IndexedDescriptor;
import run.soeasy.framework.core.transform.indexed.PropertyTemplate;

@EqualsAndHashCode(of = "executable")
@ToString(of = "executable")
@RequiredArgsConstructor
public class ExecutableParameterTemplate implements PropertyTemplate, Provider<IndexedDescriptor> {
	@NonNull
	@Getter
	private final Executable executable;
	@Getter
	@Setter
	private ParameterNameDiscoverer parameterNameDiscoverer = SystemParameterNameDiscoverer.getInstance();
	private volatile String[] names;
	private volatile IndexedDescriptor[] parameterDescriptors;

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
						parameterDescriptors = IndexedDescriptor.EMPTY_INDEXED_DESCRIPTORS;
					} else {
						names = parameterNameDiscoverer.getParameterNames(executable);
						IndexedDescriptor[] array = new IndexedDescriptor[parameters.length];
						for (int i = 0; i < parameters.length; i++) {
							IndexedDescriptor parameterDescriptor = new ExecutableParameterDescriptor(parameters[i]);
							if (i < names.length && names[i] != null) {
								parameterDescriptor = parameterDescriptor.reindex(names[i]);
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
	public Iterator<IndexedDescriptor> iterator() {
		reload(false);
		return Arrays.asList(parameterDescriptors).iterator();
	}

	@Override
	public final Stream<IndexedDescriptor> stream() {
		return Provider.super.stream();
	}

}
