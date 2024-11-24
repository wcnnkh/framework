package io.basc.framework.core.execution.reflect;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;

import io.basc.framework.core.MethodParameter;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.AnnotationArrayAnnotatedElement;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.param.ParameterDescriptor;
import io.basc.framework.core.execution.param.ParameterUtils;
import io.basc.framework.util.Elements;
import lombok.NonNull;

public class ReflectionExecutable<T extends Executable> extends ReflectionMember<T> {

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
							ResolvableType.forClass(getMember().getDeclaringClass()), getMember().getDeclaringClass(),
							new AnnotationArrayAnnotatedElement(getMember().getDeclaredAnnotations()));
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
					AnnotatedType[] annotatedExceptionTypes = getMember().getAnnotatedExceptionTypes();
					Class<?>[] exceptionTypes = getMember().getExceptionTypes();
					Type[] genericExceptionTypes = getMember().getGenericExceptionTypes();
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
					parameterDescriptors = Elements.forArray(ParameterUtils.getParameterDescriptors(getMember()));
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
					returnTypeDescriptor = new TypeDescriptor(MethodParameter.forExecutable(getMember(), -1));
				}
			}
		}
		return returnTypeDescriptor;
	}

	private volatile MergedAnnotations annotations;

	@Override
	public MergedAnnotations getAnnotations() {
		if (annotations == null) {
			synchronized (this) {
				if (annotations == null) {
					annotations = MergedAnnotations.from(getMember());
				}
			}
		}
		return annotations;
	}
}
