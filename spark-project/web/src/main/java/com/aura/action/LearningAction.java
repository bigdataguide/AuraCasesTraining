package com.aura.action;

import com.aura.basic.BasicActionSupportImpl;
import com.aura.model.Learning;
import com.aura.service.LearningService;
import com.aura.util.JsonHelper;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@Controller("learningAction")
public class LearningAction extends BasicActionSupportImpl {
	
	private static final long serialVersionUID = 1L;
	
	@Resource(name="learningService")
	private LearningService learningService;
	
	/**
	 * Spark MLlib 性别分类
	 */
	public void getGenderList() {		
		Learning learning = new Learning();
		List<Learning> list = learningService.getGenderList(learning);
		JsonHelper.printBasicJsonList(getResponse(), list);
	}
	
	/**
	 * Spark MLlib 频道分类
	 */
	public void getChannelList() {		
		Learning learning = new Learning();
		List<Learning> list = learningService.getChannelList(learning);
		JsonHelper.printBasicJsonList(getResponse(), list);
	}
	
}
