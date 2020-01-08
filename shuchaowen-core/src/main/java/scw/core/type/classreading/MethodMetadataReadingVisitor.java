/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.core.type.classreading;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import scw.asm.AnnotationVisitor;
import scw.asm.MethodVisitor;
import scw.asm.Opcodes;
import scw.asm.Type;
import scw.core.Constants;
import scw.core.annotation.AnnotationAttributes;
import scw.core.type.MethodMetadata;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

/**
 * ASM method visitor which looks for the annotations defined on a method,
 * exposing them through the {@link org.springframework.core.type.MethodMetadata}
 * interface.
 *
 * @author Juergen Hoeller
 * @author Mark Pollack
 * @author Costin Leau
 * @author Chris Beams
 * @author Phillip Webb
 * @since 3.0
 */
public class MethodMetadataReadingVisitor extends MethodVisitor implements MethodMetadata {

	protected final String methodName;

	protected final int access;

	protected final String declaringClassName;

	protected final String returnTypeName;

	protected final ClassLoader classLoader;

	protected final Set<MethodMetadata> methodMetadataSet;

	protected final Map<String, Set<String>> metaAnnotationMap = new LinkedHashMap<String, Set<String>>(4);

	protected final LinkedMultiValueMap<String, AnnotationAttributes> attributesMap =
			new LinkedMultiValueMap<String, AnnotationAttributes>(4);


	public MethodMetadataReadingVisitor(String methodName, int access, String declaringClassName,
			String returnTypeName, ClassLoader classLoader, Set<MethodMetadata> methodMetadataSet) {

		super(Constants.ASM_VERSION);
		this.methodName = methodName;
		this.access = access;
		this.declaringClassName = declaringClassName;
		this.returnTypeName = returnTypeName;
		this.classLoader = classLoader;
		this.methodMetadataSet = methodMetadataSet;
	}


	@Override
	public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
		this.methodMetadataSet.add(this);
		String className = Type.getType(desc).getClassName();
		return new AnnotationAttributesReadingVisitor(
				className, this.attributesMap, this.metaAnnotationMap, this.classLoader);
	}


	public String getMethodName() {
		return this.methodName;
	}

	public boolean isAbstract() {
		return ((this.access & Opcodes.ACC_ABSTRACT) != 0);
	}

	public boolean isStatic() {
		return ((this.access & Opcodes.ACC_STATIC) != 0);
	}

	public boolean isFinal() {
		return ((this.access & Opcodes.ACC_FINAL) != 0);
	}

	public boolean isOverridable() {
		return (!isStatic() && !isFinal() && ((this.access & Opcodes.ACC_PRIVATE) == 0));
	}

	public boolean isAnnotated(String annotationName) {
		return this.attributesMap.containsKey(annotationName);
	}

	public AnnotationAttributes getAnnotationAttributes(String annotationName) {
		return getAnnotationAttributes(annotationName, false);
	}

	public AnnotationAttributes getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		AnnotationAttributes raw = AnnotationReadingVisitorUtils.getMergedAnnotationAttributes(
				this.attributesMap, this.metaAnnotationMap, annotationName);
		return AnnotationReadingVisitorUtils.convertClassValues(
				"method '" + getMethodName() + "'", this.classLoader, raw, classValuesAsString);
	}

	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
		return getAllAnnotationAttributes(annotationName, false);
	}

	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		if (!this.attributesMap.containsKey(annotationName)) {
			return null;
		}
		MultiValueMap<String, Object> allAttributes = new LinkedMultiValueMap<String, Object>();
		for (AnnotationAttributes annotationAttributes : this.attributesMap.get(annotationName)) {
			AnnotationAttributes convertedAttributes = AnnotationReadingVisitorUtils.convertClassValues(
					"method '" + getMethodName() + "'", this.classLoader, annotationAttributes, classValuesAsString);
			for (Map.Entry<String, Object> entry : convertedAttributes.entrySet()) {
				allAttributes.add(entry.getKey(), entry.getValue());
			}
		}
		return allAttributes;
	}

	public String getDeclaringClassName() {
		return this.declaringClassName;
	}

	public String getReturnTypeName() {
		return this.returnTypeName;
	}

}
