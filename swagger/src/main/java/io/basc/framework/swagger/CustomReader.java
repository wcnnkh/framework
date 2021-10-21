package io.basc.framework.swagger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

public class CustomReader extends Reader {
	
	@Override
	public OpenAPI read(Class<?> cls, String parentPath, String parentMethod,
			boolean isSubresource, RequestBody parentRequestBody,
			ApiResponses parentResponses, Set<String> parentTags,
			List<Parameter> parentParameters, Set<Class<?>> scannedResources) {
		OpenAPI openAPI = super.read(cls, parentPath, parentMethod, isSubresource,
				parentRequestBody, parentResponses, parentTags, parentParameters,
				scannedResources);
		return openAPI;
	}
	
	@Override
	protected Operation parseMethod(Class<?> cls, Method method,
			List<Parameter> globalParameters, Produces methodProduces,
			Produces classProduces, Consumes methodConsumes,
			Consumes classConsumes,
			List<SecurityRequirement> classSecurityRequirements,
			Optional<ExternalDocumentation> classExternalDocs,
			Set<String> classTags, List<Server> classServers,
			boolean isSubresource, RequestBody parentRequestBody,
			ApiResponses parentResponses, JsonView jsonViewAnnotation,
			ApiResponse[] classResponses, AnnotatedMethod annotatedMethod) {
		Operation operation = super.parseMethod(cls, method, globalParameters,
				methodProduces, classProduces, methodConsumes, classConsumes,
				classSecurityRequirements, classExternalDocs, classTags,
				classServers, isSubresource, parentRequestBody,
				parentResponses, jsonViewAnnotation, classResponses,
				annotatedMethod);
		return operation;
	}
}
