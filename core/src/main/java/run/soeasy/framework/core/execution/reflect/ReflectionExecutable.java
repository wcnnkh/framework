package run.soeasy.framework.core.execution.reflect;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;

import lombok.NonNull;
import run.soeasy.framework.core.AnnotatedElementWrapper;
import run.soeasy.framework.core.AnnotationArrayAnnotatedElement;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.param.ParameterDescriptor;
import run.soeasy.framework.util.ResolvableType;
import run.soeasy.framework.util.collections.Elements;

public class ReflectionExecutable<T extends Executable> extends ReflectionMember<T>
		implements AnnotatedElementWrapper<T> {

	@NonNull
	private volatile TypeDescriptor declaringTypeDescriptor;

	@NonNull
	private volatile Elements<TypeDescriptor> exceptionTypeDescriptors;
	@NonNull
	private volatile Elements<ParameterDescriptor> parameterDescriptors;
	@NonNull
	private volatile TypeDescriptor returnTypeDescriptor;

	public ReflectionExecutable(@NonNull T member) {
		super(member);
	}

	@Override
	public TypeDescriptor getDeclaringTypeDescriptor() {
		if (declaringTypeDescriptor == null) {
			synchronized (this) {
				if (declaringTypeDescriptor == null) {
					declaringTypeDescriptor = new TypeDescriptor(
							ResolvableType.forType(getSource().getDeclaringClass()), getSource().getDeclaringClass(),
							new AnnotationArrayAnnotatedElement(getSource().getDeclaredAnnotations()));
				}
			}
		}
		return declaringTypeDescriptor;
	}

	@Override
	public Elements<TypeDescriptor> getExceptionTypeDescriptors() {
		if (exceptionTypeDescriptors == null) {
			synchronized (this) {
				if (exceptionTypeDescriptors == null) {
					AnnotatedType[] annotatedExceptionTypes = getSource().getAnnotatedExceptionTypes();
					Class<?>[] exceptionTypes = getSource().getExceptionTypes();
					Type[] genericExceptionTypes = getSource().getGenericExceptionTypes();
					TypeDescriptor[] typeDescriptors = new TypeDescriptor[exceptionTypes.length];
					for (int i = 0; i < typeDescriptors.length; i++) {
						typeDescriptors[i] = new TypeDescriptor(ResolvableType.forType(genericExceptionTypes[i]),
								exceptionTypes[i], annotatedExceptionTypes[i]);
					}
					exceptionTypeDescriptors = Elements.forArray(typeDescriptors);
				}
			}
		}
		return exceptionTypeDescriptors;
	}

	@Override
	public Elements<ParameterDescriptor> getParameterDescriptors() {
		if (parameterDescriptors == null) {
			synchronized (this) {
				if (parameterDescriptors == null) {
					parameterDescriptors = ParameterDescriptor.forExecutable(getSource());
				}
			}
		}
		return parameterDescriptors;
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		if (returnTypeDescriptor == null) {
			synchronized (this) {
				if (returnTypeDescriptor == null) {
					returnTypeDescriptor = TypeDescriptor.forMethodReturnType(getSource());
				}
			}
		}
		return returnTypeDescriptor;
	}
}
