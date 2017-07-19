/**   
 * @(#)AgentNameClassifierImpl.java	2016年6月22日	下午7:24:13	   
 *     
 * Copyrights (C) 2016艺龙旅行网保留所有权利
 */
package com.elong.hotel.biz.order;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elong.hotel.util.PropertiesHelper;

/**
 * 代理商名称归类器
 *
 * <p>
 * 修改历史:											<br>  
 * 修改日期    		修改人员   	版本	 		修改内容<br>  
 * -------------------------------------------------<br>  
 * 2016年6月22日 下午7:24:13   user     1.0    	初始化创建<br>
 * </p> 
 *
 * @author		user 
 * @version		1.0  
 * @since		JDK1.7
 */
public class AgentNameClassifier{

	private static String agentNameTypeStr = PropertiesHelper.getEnvPropertise("agentNameType", "GlobalConfig").toString();
	
	private static String other = "其他";
	
	/** 
	 * 代理商名称归类
	 *
	 * @param agentName
	 * @return 
	 *
	 * @see com.elong.nb.service.AgentNameClassifier#classify(java.lang.String)    
	 */
	public static String classify(String agentId) {
		if (StringUtils.isEmpty(agentId)) {
			throw new IllegalStateException("the agentId is null or empty,please check it!!!");
		}
		if (StringUtils.isEmpty(agentNameTypeStr)) {
			throw new IllegalStateException("the key 'agentNameType' in config.properties  does't exists,please check it!!!");
		}
		JSONObject jsonObj = JSON.parseObject(agentNameTypeStr);
		for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
			Object value = entry.getValue();
			String valueStr = value == null ? StringUtils.EMPTY : (String) value;
			if (Arrays.asList(StringUtils.split(valueStr, "&", -1)).contains(agentId)) {
				return entry.getKey();
			}
		}
		return other;
	}

}
