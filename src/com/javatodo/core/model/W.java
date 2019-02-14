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
package com.javatodo.core.model;

import java.util.ArrayList;
import java.util.List;

public class W {
	private String relation = "";
	private Object value = new Object();
	private List<Object> value_list = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public W(String relation, Object value) {
		if ((!value.getClass().isArray())&&(!value.getClass().getName().toLowerCase().contains("list"))) {
			this.relation = relation;
			this.value = value;
		}else{
			if(value.getClass().getName().toLowerCase().contains("list")){
				this.relation = relation;
				this.value_list = (ArrayList<Object>)value;
			}
		}
	}

	public W(String relation, Object[] values) {
		this.relation = relation;
		this.value_list = new ArrayList<>();
		for (Integer i = 0; i < values.length; i = i + 1) {
			this.value_list.add(values[i]);
		}
	}

	public String get_relation() {
		return this.relation;
	}

	public Object get_value() {
		return this.value;
	}

	public List<Object> get_value_list() {
		return this.value_list;
	}
}
