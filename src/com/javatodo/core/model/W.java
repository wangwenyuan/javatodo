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
	private Relation relation;
	private Object value = new Object();
	private List<Object> valueList = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public W(Relation relation, Object value) {
		if ((!value.getClass().isArray())&&(!value.getClass().getName().toLowerCase().contains("list"))) {
			this.relation = relation;
			this.value = value;
		}else{
			if(value.getClass().getName().toLowerCase().contains("list")){
				this.relation = relation;
				this.valueList = (ArrayList<Object>)value;
			}
		}
	}

	public W(Relation relation, Object[] values) {
		this.relation = relation;
		this.valueList = new ArrayList<>();
		for (Integer i = 0; i < values.length; i = i + 1) {
			this.valueList.add(values[i]);
		}
	}

	public Relation getRelation() {
		return this.relation;
	}

	public Object getValue() {
		return this.value;
	}

	public List<Object> getValueList() {
		return this.valueList;
	}
}
