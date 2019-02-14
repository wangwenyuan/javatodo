/**
 * Copyright (c) 2017, Wang Wenyuan 王文渊 (827287829@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javatodo.core.tools;

import java.util.Map;

public class Validate {
	public boolean status=false;
	public String info="";
	
	/**
	 * 实例化一个验证类
	 * @param value 要验证的数据
	 * @param type 数据格式
	 * @param info 如果数据格式不是所给定的数据格式，那么此处存放提示给用户的相关信息。
	 */
	public Validate(Object value,String type,String info) {
		this.status=T.detect(value, type);
		this.info=info;
	}
	
	/**
	 * 实例化一个验证类
	 * @param map Map<String, String> 将所有要验证的数据，以 键值对的形式存入map对象中
	 * @param fieldArray String[] map对象中所有的键名，以此形成的数组
	 * @param typeArray String[] map对象中所有要验证的数据对应的数据格式
	 * @param infoArray String[] map对象中所有要验证的数据如果与对应的数据格式不一致，那么提示给用户的相关信息
	 */
	public Validate(Map<String, String> map,String[] fieldArray,String[] typeArray, String[] infoArray){
		if(typeArray==null||infoArray==null){
			status=false;
			info="验证类型错误";
			return;
		}
		if(fieldArray.length!=typeArray.length){
			status=false;
			info="字段个数与验证类型个数不一致";
			return;
		}
		if(typeArray.length!=infoArray.length){
			status=false;
			info="验证类型个数与返回信息个数不一致";
			return;
		}
		for(Integer i=0; i<fieldArray.length; i=i+1){
			if(!map.containsKey(fieldArray[i])){
				status=false;
				info="验证参数：“"+fieldArray[i]+"”不存在";
				return;
			}else{
				if(!T.detect(map.get(fieldArray[i]), typeArray[i])){
					status=false;
					info=infoArray[i];
					return;
				}
			}
		}
		status=true;
		info="";
		return;
	}
}
