package shuchaowen.web.servlet.view;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.core.http.enums.ContentType;
import shuchaowen.web.servlet.WebResponse;

public abstract class JsonView extends AbstractTextView{

	@Override
	public String getResponseText() {
		return JSONObject.toJSONString(this);
	}
	
	@Override
	public void render(WebResponse response) throws IOException {
		if(response.getContentType() == null){
			response.setContentType(ContentType.JSON.getValue());
		}
		super.render(response);
	}
}
