package scw.oas.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import scw.core.annotation.AnnotationUtils;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.io.ResourceUtils;
import scw.lang.Nullable;
import scw.oas.ApiInfo;
import scw.oas.ApiParameter;
import scw.oas.annotation.ApiRequest;
import scw.oas.annotation.ApiResponse;
import scw.orm.Mapper;
import scw.orm.MappingContext;
import scw.orm.ObjectRelationalMapping;

public class ApiDocumentByAnnotation extends SimpleApiDocument {
	private static final long serialVersionUID = 1L;

	public ApiDocumentByAnnotation scann(String packageName, Mapper mapper) {
		List<ApiInfo> list = new ArrayList<ApiInfo>();
		for (Class<?> clazz : ResourceUtils.getPackageScan().getClasses(packageName)) {
			list.addAll(getApiInfoList(clazz, mapper));
		}
		setApiInfoList(list);
		return this;
	}

	protected List<ApiInfo> getApiInfoList(Class<?> clazz, Mapper mapper) {
		List<ApiInfo> list = new ArrayList<ApiInfo>();
		for (Method method : clazz.getDeclaredMethods()) {
			ApiRequest apiRequest = method.getAnnotation(ApiRequest.class);
			if (apiRequest == null) {
				continue;
			}

			ApiInfo apiInfo = getApiInfo(method, apiRequest,
					method.getAnnotation(ApiResponse.class), mapper);
			list.add(apiInfo);
		}
		return list;
	}

	protected ApiInfo getApiInfo(Method method, ApiRequest apiRequest,
			@Nullable ApiResponse apiResponse, Mapper mapper) {
		SimpleApiInfo simpleApiInfo = new SimpleApiInfo();
		if (StringUtils.isNotEmpty(apiRequest.contentType())) {
			simpleApiInfo.setRequestContentType(apiRequest.contentType());
		}

		if (StringUtils.isNotEmpty(apiRequest.description())) {
			simpleApiInfo.setDescription(apiRequest.description());
		}

		if (StringUtils.isNotEmpty(apiRequest.name())) {
			simpleApiInfo.setDescription(apiRequest.name());
		}

		if (apiResponse != null
				&& StringUtils.isNotEmpty(apiResponse.contentType())) {
			simpleApiInfo.setResponseContentType(apiResponse.contentType());
		}

		if (ArrayUtils.isEmpty(apiRequest.parameters())) {
			simpleApiInfo.setRequestParameterList(getApiParameter(method,
					mapper));
		} else {
			simpleApiInfo.setRequestParameterList(getApiParameter(
					apiRequest.parameters(), mapper));
		}

		if (apiResponse == null || ArrayUtils.isEmpty(apiResponse.parameters())) {
			if (isEntityType(method.getReturnType())) {
				simpleApiInfo.setResponseParameterList(toEntityApiParameter(
						mapper, method.getReturnType()));
			}
			// 如果不是一个entity不用返回字段描述
		} else {
			simpleApiInfo.setRequestParameterList(getApiParameter(
					apiResponse.parameters(), mapper));
		}
		return simpleApiInfo;
	}

	protected List<ApiParameter> getApiParameter(Method method, Mapper mapper) {
		List<ApiParameter> apiParameters = new ArrayList<ApiParameter>();
		for (ParameterDescriptor parameterConfig : ParameterUtils
				.getParameterDescriptors(method)) {
			SimpleApiParameter parameter = new SimpleApiParameter();
			parameter.setType(parameterConfig.getType().getName());
			parameter.setName(parameterConfig.getName());
			parameter.setRequired(ParameterUtils.isNullAble(parameterConfig));
			parameter.setDescription(AnnotationUtils.getDescription(
					parameterConfig.getAnnotatedElement(), null));
			if (isEntityType(parameterConfig.getType())) {
				parameter.setSubList(toEntityApiParameter(mapper,
						parameterConfig.getType()));
			}
			apiParameters.add(parameter);
		}
		return apiParameters;
	}

	protected List<ApiParameter> getApiParameter(
			scw.oas.annotation.ApiParam[] apiParameters, Mapper mapper) {
		List<ApiParameter> list = new ArrayList<ApiParameter>();
		for (scw.oas.annotation.ApiParam apiParam : apiParameters) {
			SimpleApiParameter parameter = new SimpleApiParameter();
			if (StringUtils.isNotEmpty(apiParam.defaultValue())) {
				parameter.setDefaultValue(apiParam.defaultValue());
			}

			if (StringUtils.isNotEmpty(apiParam.name())) {
				parameter.setName(apiParam.name());
			}

			if (StringUtils.isNotEmpty(apiParam.description())) {
				parameter.setDescription(apiParam.description());
			}

			if (StringUtils.isNotEmpty(apiParam.type())) {
				parameter.setType(apiParam.type());
			}

			if (apiParam.typeClass() != null
					&& apiParam.typeClass() != Void.class) {
				if (isEntityType(apiParam.typeClass())) {
					parameter.setSubList(toEntityApiParameter(mapper,
							apiParam.typeClass()));
				} else {
					parameter.setType(apiParam.typeClass().getName());
				}
			}
			list.add(parameter);
		}
		return list;
	}

	protected List<ApiParameter> toEntityApiParameter(Mapper mapper,
			Class<?> entityClass) {
		List<ApiParameter> list = new ArrayList<ApiParameter>();
		ObjectRelationalMapping objectRelationalMapping = mapper
				.getObjectRelationalMapping(entityClass);
		for (MappingContext mappingContext : objectRelationalMapping) {
			SimpleApiParameter parameter = new SimpleApiParameter();
			parameter.setType(mappingContext.getColumn().getType().toString()
					.split(" ")[1]);
			parameter.setName(mappingContext.getColumn().getName());
			parameter.setRequired(mapper.isNullable(mappingContext));
			parameter.setDescription(mappingContext.getColumn()
					.getDescription());

			if (mapper.isEntity(mappingContext)) {
				parameter.setSubList(toEntityApiParameter(mapper,
						mappingContext.getColumn().getType()));
			}
			list.add(parameter);
		}
		return list;
	}

	protected boolean isEntityType(Class<?> type) {
		if (TypeUtils.isPrimitiveOrWrapper(type)) {
			return false;
		}

		if (String.class == type || Number.class.isAssignableFrom(type)
				|| type.isArray() || type.isAnnotation()) {
			return false;
		}

		// TODO 未完成
		return true;
	}
}
