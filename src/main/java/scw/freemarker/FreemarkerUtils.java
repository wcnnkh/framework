package scw.freemarker;

import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import scw.spring.util.NumberUtils;

/**
 * freemarker工具类
 * @author shuchaowen
 *
 */
public class FreemarkerUtils {
	@SuppressWarnings("unchecked")
	public static <T> T getObject(TemplateModel model, String name,
			Class<T> targetClass) throws TemplateModelException {
		if (model instanceof AdapterTemplateModel) {
			return (T) ((AdapterTemplateModel) model)
					.getAdaptedObject(targetClass);
		} 
		return null;
	}

	public static <T> T getObject(Map<String, TemplateModel> params,
			String name, Class<T> targetClass) throws TemplateModelException {
		TemplateModel model = params.get(name);
		return getObject(model, name, targetClass);
	}

	public static String getString(TemplateModel model, String name, String def)
			throws TemplateModelException {
		String text = null;
		if (model == null) {
			text = def;
		} else if (model instanceof TemplateScalarModel) {
			TemplateScalarModel scalarModel = (TemplateScalarModel) model;
			text = scalarModel.getAsString();
		} else if ((model instanceof TemplateNumberModel)) {
			TemplateNumberModel numberModel = (TemplateNumberModel) model;
			Number number = numberModel.getAsNumber();
			text = number.toString();
		}
		return text;
	}

	public static String getString(TemplateModel model, String name)
			throws TemplateModelException {
		return getString(model, name, null);
	}

	public static String getString(Map<String, TemplateModel> params,
			String name, String def) throws TemplateModelException {
		TemplateModel model = params.get(name);
		return getString(model, name, def);
	}

	public static String getString(Map<String, TemplateModel> params,
			String name) throws TemplateModelException {
		TemplateModel model = params.get(name);
		return getString(model, name);
	}

	public static <T extends Number> T getNumber(TemplateModel model,
			String name, Class<T> targetClass) throws TemplateModelException {
		if (model == null) {
			return null;
		} else if (model instanceof TemplateNumberModel) {
			TemplateNumberModel numberModel = (TemplateNumberModel) model;
			Number number = numberModel.getAsNumber();
			return (T) NumberUtils.convertNumberToTargetClass(number, targetClass);
		} else if (model instanceof TemplateScalarModel) {
			TemplateScalarModel scalarModel = (TemplateScalarModel) model;
			String text = scalarModel.getAsString();
			if (text != null) {
				try {
					return (T) NumberUtils.parseNumber(text, targetClass);
				} catch (NumberFormatException e) {
					throw new TemplateModelException(String.format(NOT_MATCH,
							name, "number"), e);
				}
			} else {
				return null;
			}
		} else {
			throw new TemplateModelException(String.format(NOT_MATCH, name,
					"number"));
		}
	}

	public static <T extends Number> T getNumber(
			Map<String, TemplateModel> params, String name, Class<T> targetClass)
			throws TemplateModelException {
		TemplateModel model = params.get(name);
		return getNumber(model, name, targetClass);
	}
	public static Long getLong(Map<String, TemplateModel> params, String name)
			throws TemplateModelException {
		return getNumber(params, name, Long.class);
	}

	public static Integer getInteger(Map<String, TemplateModel> params,
			String name) throws TemplateModelException {
		return getNumber(params, name, Integer.class);
	}
	
	public static String getReqeustParams(Environment env, String key){
		try {
			TemplateModel model = env.getDataModel().get(key);
			if (model instanceof TemplateNumberModel){
				return ((TemplateNumberModel)model).getAsNumber().toString();
			}else if(model instanceof TemplateScalarModel){
				return ((TemplateScalarModel)model).getAsString();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String NOT_MATCH = "The '%s' parameter not a %s";
}
