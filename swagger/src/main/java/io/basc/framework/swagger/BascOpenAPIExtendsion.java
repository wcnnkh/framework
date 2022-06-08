package io.basc.framework.swagger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Copy;
import io.basc.framework.mapper.Field;
import io.basc.framework.orm.ObjectRelational;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.message.annotation.QueryParams;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.ParameterProcessor;
import io.swagger.v3.jaxrs2.ResolvedParameter;
import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtensions;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * 扩展
 * 
 * @author shuchaowen
 * @see io.swagger.v3.jaxrs2.DefaultParameterExtension
 */
@Provider(order = Ordered.LOWEST_PRECEDENCE)
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
		ObjectRelational<? extends Property> fields = OrmUtils.getMapper()
				.getStructure(constructType(type).getRawClass()).all();
		for (final Field field : fields) {
			final Iterator<OpenAPIExtension> extensions = OpenAPIExtensions.chain();
			// skip hidden properties
			boolean hidden = field.isAnnotationPresent(Hidden.class);
			if (!hidden) {
				Schema schema = field.getAnnotation(Schema.class);
				if (schema != null) {
					if (schema.hidden()) {
						hidden = true;
					}
				}
			}
			if (hidden) {
				continue;
			}

			List<Annotation> paramAnnotations = Arrays.asList(field.getAnnotations());
			Type paramType = field.getGetter().getGenericType();

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
				Parameter processedParam = ParameterProcessor.applyAnnotations(resolvedParameter.requestBody,
						field.getGetter().getGenericType(), paramAnnotations, components,
						classConsumes == null ? new String[0] : classConsumes.value(),
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

	private Parameter requestBodyToQueryParameter(List<Annotation> paramAnnotations, Type type, Parameter source,
			Field field) {
		Parameter target = new Parameter();
		if (source.getSchema() != null) {
			Copy.copy(source.getSchema(), target);
		}
		Copy.copy(source, target);

		if (StringUtils.isEmpty(target.getName())) {
			target.setName(field.getGetter().getName());
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
					target.setRequired(schema.required());
					break;
				}
			}
		}
		return target;
	}
}
