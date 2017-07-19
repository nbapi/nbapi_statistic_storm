package com.elong.hotel.biz;

import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class DefaultBizHandler implements IBizHandler{

	@Override
	public List<JSONObject> handle(JSONObject jsonObject) {
		List<JSONObject> ls = new LinkedList<JSONObject>();
		ls.add(jsonObject);
		return ls;
	}

}
