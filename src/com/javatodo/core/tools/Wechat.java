package com.javatodo.core.tools;

import com.alibaba.fastjson.JSONObject;

public class Wechat {
	private Http http = new Http();

	public String errorMsg = "";

	public String getAccessToken(String appid, String appsecret) {
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret="
				+ appsecret;
		String ret = this.http.get(url);
		if (ret == null) {
			this.errorMsg = "网络连接错误";
			return null;
		} else {
			JSONObject jsonObject = (JSONObject) JSONObject.parse(ret);
			if (jsonObject.containsKey("access_token")) {
				return jsonObject.getString("access_token");
			} else {
				this.errorMsg = jsonObject.getString("errmsg");
				return null;
			}
		}
	}

}
