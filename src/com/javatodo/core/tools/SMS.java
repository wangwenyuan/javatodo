package com.javatodo.core.tools;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class SMS {
	private boolean flag = true;
	public String err_code = "";
	private String AccessKeyId = "";
	private String AccessKeySecret = "";
	public SMS(String accessKeyId, String accessKeySecret){
		this.AccessKeyId = accessKeyId;
		this.AccessKeySecret = accessKeySecret;
	}
	public boolean send_sms(String mobile, String json_code, String signName, String templateCode) throws ClientException{
		final String product = "Dysmsapi";
		final String domain = "dysmsapi.aliyuncs.com";
		final String accessKeyId = this.AccessKeyId;
		final String accessKeySecret = this.AccessKeySecret;
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);
		 SendSmsRequest request = new SendSmsRequest();
		 request.setMethod(MethodType.POST);
		 request.setPhoneNumbers(mobile);
		 request.setSignName(signName);
		 request.setTemplateCode(templateCode);
		 request.setTemplateParam(json_code);
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
			this.flag = true;
			this.err_code = "";
		}else{
			this.flag = false;
			this.err_code = sendSmsResponse.getMessage();
		}
		return this.flag;
	}
}
