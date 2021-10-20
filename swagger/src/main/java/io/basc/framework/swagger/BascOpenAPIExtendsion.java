package io.basc.framework.swagger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.jaxrs2.ResolvedParameter;
import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * 扩展
 * 
 * @author shuchaowen
 * @see io.swagger.v3.jaxrs2.DefaultParameterExtension
 */

public class BascOpenAPIExtendsion extends AbstractOpenAPIExtension {
	private static final String QUERY_PARAM = "query";
	
	@Override
	public ResolvedParameter extractParameters(List<Annotation> annotations, Type type, Set<Type> typesToSkip,
			Components components, javax.ws.rs.Consumes classConsumes, javax.ws.rs.Consumes methodConsumes,
			boolean includeRequestBody, JsonView jsonViewAnnotation, Iterator<OpenAPIExtension> chain) {
		for(Annotation annotation : annotations) {
			if(annotation instanceof GET) {
				ResolvedParameter parameter = chain.next().extractParameters(annotations, type, typesToSkip, components, classConsumes, methodConsumes, includeRequestBody, jsonViewAnnotation, chain);
				return toGetParams(parameter);
			}
		}
		return super.extractParameters(annotations, type, typesToSkip, components, classConsumes, methodConsumes, includeRequestBody, jsonViewAnnotation, chain);
	}
	
	public ResolvedParameter toGetParams(ResolvedParameter parameter) {
		ResolvedParameter resolvedParameter = new ResolvedParameter();
		resolvedParameter.parameters = parameter.parameters;
		resolvedParameter.requestBody = parameter.requestBody;
		for(Parameter param : parameter.formParameters) {
			param.setIn(QUERY_PARAM);
		}
		resolvedParameter.parameters.addAll(parameter.formParameters);
		return resolvedParameter;
	}
}
