package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Executable;

import lombok.NonNull;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.annotation.AnnotationArrayAnnotatedElement;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execute.ParameterTemplate;
import run.soeasy.framework.core.type.ResolvableType;

public class ReflectionExecutable<T extends Executable> extends AbstractReflectionExecutable<T>
		implements AnnotatedElementWrapper<T> {

	@NonNull
	private transient volatile TypeDescriptor declaringTypeDescriptor;

	@NonNull
	private transient volatile Elements<TypeDescriptor> exceptionTypeDescriptors;
	private transient volatile ParameterTemplate parameterTemplate;
	@NonNull
	private transient volatile TypeDescriptor returnTypeDescriptor;

	public ReflectionExecutable(@NonNull T member) {
		super(member);
	}

	@Override
	public synchronized void setSource(@NonNull T source) {
		super.setSource(source);
		this.declaringTypeDescriptor = null;
		this.exceptionTypeDescriptors = null;
		this.parameterTemplate = null;
		this.returnTypeDescriptor = null;
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
					exceptionTypeDescriptors = new ExecutableExceptionTypeDescriptors(getSource());
				}
			}
		}
		return exceptionTypeDescriptors;
	}

	@Override
	public ParameterTemplate getParameterTemplate() {
		if (parameterTemplate == null) {
			synchronized (this) {
				if (parameterTemplate == null) {
					parameterTemplate = new ExecutableParameterTemplate(getSource());
				}
			}
		}
		return parameterTemplate;
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
