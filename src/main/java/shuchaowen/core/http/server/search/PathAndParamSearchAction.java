package shuchaowen.core.http.server.search;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.exception.AlreadyExistsException;
import shuchaowen.core.http.server.Action;
import shuchaowen.core.http.server.Request;
import shuchaowen.core.http.server.SearchAction;
import shuchaowen.core.http.server.action.MethodAction;
import shuchaowen.core.http.server.annotation.Controller;
import shuchaowen.core.util.XUtils;

public class PathAndParamSearchAction implements SearchAction{
	private final Map<String, Map<String, Map<String, Action>>> actionMap = new HashMap<String, Map<String, Map<String, Action>>>();
	private String key;
	private BeanFactory beanFactory;
	
	public PathAndParamSearchAction(BeanFactory beanFactory, String action){
		this.key = action;
		this.beanFactory = beanFactory;
	}

	public void init(Collection<Class<?>> classList) throws Throwable {
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
				shuchaowen.core.http.enums.Method[] types = MethodAction.mergeRequestType(clz, method);
				for(shuchaowen.core.http.enums.Method type : types){
					Map<String, Action> map = clzMap.get(type.name());
					if(map == null){
						map = new HashMap<String, Action>();
					}

					if(map.containsKey(actionName)){
						throw new AlreadyExistsException("存在相同 的action-clz="+ clz.getName() + ",method=" + method.getName() + ",actionName="+ actionName);
					}
					
					map.put(actionName, action);
					clzMap.put(type.name(), map);
					actionMap.put(path, clzMap);
				}
			}
		}
	}

	public Action getAction(Request request) throws Throwable {
		if(key == null){
			return null;
		}
		
		Map<String, Map<String, Action>> map = actionMap.get(request.getPath());
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
