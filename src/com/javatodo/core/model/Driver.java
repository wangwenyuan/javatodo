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

import java.util.List;
import java.util.Map;

public abstract class Driver {
	public abstract void table(String table_name);

	public abstract Driver where(Map<String, W> where);

	public abstract Driver where(String where_str);

	public abstract Driver where(String where_str, Object... params);

	public abstract Driver order(String order_str);

	public abstract Driver limit(String limit_str);

	public abstract Driver data(Map<String, Object> data);

	public abstract Driver alias(String as_str);

	public abstract Driver join(String table_name, String on_sql);

	public abstract Driver join(String table_name, String on_sql, String type);

	public abstract Driver field(String field_str);

	public abstract Driver group(String field_str);

	public abstract Driver add();

	public abstract Driver save(Map<String, Object> data);

	public abstract Driver delete();

	public abstract Driver select();

	public abstract Driver find();

	public abstract String get_sql();

	public abstract List<Object> get_add_data();

	public abstract List<Object> get_update_data();

	public abstract List<Object> get_where_data();

	public abstract Driver setInc(String field, Integer value);

	public abstract Driver setDec(String field, Integer value);

	public abstract Driver setInc(String field);

	public abstract Driver setDec(String field);

	public abstract void clear();
}