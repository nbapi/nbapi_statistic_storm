package com.elong.hotel.biz;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface IBizHandler {

	public List<JSONObject> handle(JSONObject jsonObject);
	
}
