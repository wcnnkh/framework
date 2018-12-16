package shuchaowen.web.servlet.action;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.beans.BeanFactory;
import shuchaowen.common.exception.AlreadyExistsException;
import shuchaowen.common.utils.XUtils;
import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.action.annotation.Controller;

public class PathAndParamSearchAction implements SearchAction{
	private final Map<String, Map<String, Map<String, Action>>> actionMap = new HashMap<String, Map<String, Map<String, Action>>>();
	private String key;
	private BeanFactory beanFactory;
	
	public PathAndParamSearchAction(BeanFactory beanFactory, String action){
		this.key = action;
		this.beanFactory = beanFactory;
	}
	
	private static String getExistActionErrMsg(Action action, Action oldAction){
		StringBuilder sb = new StringBuilder();
		sb.append("存在同样的controller[");
		sb.append(action.toString());
		sb.append("],原来的[");
		sb.append(oldAction.toString());
		sb.append("]");
		return sb.toString();
	}

	public void init(Collection<Class<?>> classList) throws Exception {
		for(Class<?> clz : classList){
			Deprecated d = clz.getAnnotation(Deprecated.class);
			if(d != null){
				return ;
			}
			
			Controller clzController = clz.getAnnotation(Controller.class);
			if(clzController == null){
				continue;
			}
			
			String clzPath = clzController == null? "":clzController.value();
			String path = XUtils.mergePath("/", clzPath);
			Map<String, Map<String, Action>> clzMap = actionMap.get(path);
			if(clzMap == null){
				clzMap = new HashMap<String, Map<String, Action>>();
			}
			
			for (Method method : clz.getDeclaredMethods()) {
				Deprecated deprecated = method.getAnnotation(Deprecated.class);
				if(deprecated != null){
					continue;
				}
				
				Controller methodController = method.getAnnotation(Controller.class);
				if(methodController == null){
					continue;
				}
				
				String actionName = methodController.value();
				if("".equals(actionName)){
					actionName = method.getName();
				}
				
				Action action = new MethodAction(beanFactory, clz, method);
				shuchaowen.connection.http.enums.Method[] types = MethodAction.mergeRequestType(clz, method);
				for(shuchaowen.connection.http.enums.Method type : types){
					Map<String, Action> map = clzMap.get(type.name());
					if(map == null){
						map = new HashMap<String, Action>();
					}

					if(map.containsKey(actionName)){
						throw new AlreadyExistsException(getExistActionErrMsg(action, map.get(actionName)));
					}
					
					map.put(actionName, action);
					clzMap.put(type.name(), map);
					actionMap.put(path, clzMap);
				}
			}
		}
	}

	public Action getAction(Request request) throws Exception {
		if(key == null){
			return null;
		}
		
		Map<String, Map<String, Action>> map = actionMap.get(request.getServletPath());
		if(map == null){
			return null;
		}
		
		Map<String, Action> methodMap = map.get(request.getMethod());
		if(methodMap == null){
			return null;
		}

		String action = request.getParameter(String.class, key);
		if(action == null){
			return null;
		}
		return methodMap.get(action);
	}
}
