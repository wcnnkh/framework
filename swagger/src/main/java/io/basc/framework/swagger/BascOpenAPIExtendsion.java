package io.basc.framework.swagger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.Ordered;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Copy;
import io.basc.framework.mapper.entity.FieldDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.ColumnDescriptor;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.web.message.annotation.QueryParams;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.ParameterProcessor;
import io.swagger.v3.jaxrs2.ResolvedParameter;
import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtensions;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * 扩展
 * 
 * @author wcnnkh
 * @see io.swagger.v3.jaxrs2.DefaultParameterExtension
 */
@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class BascOpenAPIExtendsion extends AbstractOpenAPIExtension {
	private static Logger logger = LoggerFactory.getLogger(BascOpenAPIExtendsion.class);
	final ObjectMapper mapper = Json.mapper();

	@Override
	public ResolvedParameter extractParameters(List<Annotation> annotations, Type type, Set<Type> typesToSkip,
			Components components, javax.ws.rs.Consumes classConsumes, javax.ws.rs.Consumes methodConsumes,
			boolean includeRequestBody, JsonView jsonViewAnnotation, Iterator<OpenAPIExtension> chain) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof QueryParams) {
				ResolvedParameter resolvedParameter = new ResolvedParameter();
				resoleQueryParams(resolvedParameter.parameters, resolvedParameter.formParameters, type, typesToSkip,
						classConsumes, methodConsumes, components, includeRequestBody, jsonViewAnnotation);
				return resolvedParameter;
			}
		}
		return super.extractParameters(annotations, type, typesToSkip, components, classConsumes, methodConsumes,
				includeRequestBody, jsonViewAnnotation, chain);
	}

	public void resoleQueryParams(List<Parameter> parameters, List<Parameter> formParameters, final Type type,
			Set<Type> typesToSkip, javax.ws.rs.Consumes classConsumes, javax.ws.rs.Consumes methodConsumes,
			Components components, boolean includeRequestBody, JsonView jsonViewAnnotation) {
		EntityMapping<? extends ColumnDescriptor> fields = OrmUtils.getMapper().getMapping(constructType(type).getRawClass());
		for (final FieldDescriptor field : fields.getElements()) {
			final Iterator<OpenAPIExtension> extensions = OpenAPIExtensions.chain();
			// skip hidden properties
			boolean hidden = field.getGetters()
					.anyMatch((e) -> e.getTypeDescriptor().isAnnotationPresent(Hidden.class));
			if (!hidden) {
				Schema schema = field.getGetters().map((e) -> e.getTypeDescriptor().getAnnotation(Schema.class))
						.filter((e) -> e != null).first();
				if (schema != null) {
					if (schema.hidden()) {
						hidden = true;
					}
				}
			}
			if (hidden) {
				continue;
			}

			List<Annotation> paramAnnotations = field.getGetters()
					.flatMap((e) -> Elements.forArray(e.getTypeDescriptor().getAnnotations())).toList();
			Type paramType = field.getGetters().first().getTypeDescriptor().getResolvableType().getType();

			// Re-process all Bean fields and let the default
			// swagger-jaxrs/swagger-jersey-jaxrs processors do their thing
			ResolvedParameter resolvedParameter = extensions.next().extractParameters(paramAnnotations, paramType,
					typesToSkip, components, classConsumes, methodConsumes, includeRequestBody, jsonViewAnnotation,
					extensions);

			List<Parameter> extractedParameters = resolvedParameter.parameters;

			for (Parameter p : extractedParameters) {
				Parameter processedParam = ParameterProcessor.applyAnnotations(p, paramType, paramAnnotations,
						components, classConsumes == null ? new String[0] : classConsumes.value(),
						methodConsumes == null ? new String[0] : methodConsumes.value(), jsonViewAnnotation);
				if (processedParam != null) {
					parameters.add(processedParam);
				}
			}

			List<Parameter> extractedFormParameters = resolvedParameter.formParameters;

			for (Parameter p : extractedFormParameters) {
				Parameter processedParam = ParameterProcessor.applyAnnotations(p, paramType, paramAnnotations,
						components, classConsumes == null ? new String[0] : classConsumes.value(),
						methodConsumes == null ? new String[0] : methodConsumes.value(), jsonViewAnnotation);
				if (processedParam != null) {
					formParameters.add(processedParam);
				}
			}

			// request body
			if (resolvedParameter.requestBody != null) {
				Parameter processedParam = ParameterProcessor.applyAnnotations(resolvedParameter.requestBody, paramType,
						paramAnnotations, components, classConsumes == null ? new String[0] : classConsumes.value(),
						methodConsumes == null ? new String[0] : methodConsumes.value(), jsonViewAnnotation);
				if (processedParam != null) {
					Parameter parameter = requestBodyToQueryParameter(paramAnnotations, type, processedParam, field);
					if (parameter != null) {
						parameters.add(parameter);
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private Parameter requestBodyToQueryParameter(List<Annotation> paramAnnotations, Type type, Parameter source,
			FieldDescriptor field) {
		Parameter target = new Parameter();
		if (source.getSchema() != null) {
			Copy.copy(source.getSchema(), target);
		}

		Copy.copy(source, target);

		if (StringUtils.isEmpty(target.getName())) {
			target.setName(field.getName());
		}

		if (StringUtils.isNotEmpty(target.get$ref())
				|| (target.getSchema() != null && StringUtils.isNotEmpty(target.getSchema().get$ref()))) {
			logger.error("Query parameters[{}] do not support Ref: {}", type, target);
			return null;
		}

		if (StringUtils.isNotEmpty(target.getIn())) {
			logger.debug("Overridden 'in' properties name[{}] value[{}]", source.getName(), "query");
		}
		target.setIn("query");

		if (target.getRequired() == null) {
			for (Annotation annotation : paramAnnotations) {
				if (annotation instanceof Schema) {
					Schema schema = (Schema) annotation;
					target.setRequired(schema.requiredMode() == RequiredMode.REQUIRED || schema.required());
					break;
				}
			}
		}
		return target;
	}
}
