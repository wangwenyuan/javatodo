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
import java.util.Map;

public class MysqlDriver extends Driver {
	private String table_pre = "";// 表前缀
	private String sql = "";
	private String table_name = "";
	private String where_str = " where 1 ";
	private List<Object> where_value_list = new ArrayList<>();
	private List<Object> data_value_list = new ArrayList<>();
	private List<Object> update_value_list = new ArrayList<>();
	private String order_str = "";
	private String field_str = " * ";
	private String limit_str = "";
	private String add_str = "";
	private String as_str = "";
	private List<String> join_str = new ArrayList<String>();

	private String group_str = "";

	public MysqlDriver() {
		this.table_pre = MC.table_pre.get(0);
	}

	public MysqlDriver(Integer dbIndex) {
		this.table_pre = MC.table_pre.get(dbIndex);
	}

	// 初始化当前表
	public MysqlDriver(String table_name) {
		this.table_pre = MC.table_pre.get(0);
		this.table_name = table_name;
	}

	// 初始化当前表
	public MysqlDriver(String table_name, Integer dbIndex) {
		this.table_pre = MC.table_pre.get(dbIndex);
		this.table_name = table_name;
	}

	// 设置表名称
	public void table(String table_name) {
		this.table_name = table_name;
	}

	// where方法
	public MysqlDriver where(Map<String, W> where) {
		for (String key : where.keySet()) {
			switch (where.get(key).get_relation().toLowerCase().trim()) {
			case "eq":
				if (where.get(key).get_value() == null) {
					this.where_str = this.where_str + " and " + key + " is null ";
				} else {
					this.where_str = this.where_str + " and " + key + " =? ";
					this.where_value_list.add(where.get(key).get_value());
				}
				break;
			case "=":
				if (where.get(key).get_value() == null) {
					this.where_str = this.where_str + " and " + key + " is null ";
				} else {
					this.where_str = this.where_str + " and " + key + " =? ";
					this.where_value_list.add(where.get(key).get_value());
				}
				break;
			case "!=":
				if (where.get(key).get_value() == null) {
					this.where_str = this.where_str + " and " + key + " is not null ";
				} else {
					this.where_str = this.where_str + " and " + key + " !=? ";
					this.where_value_list.add(where.get(key).get_value());
				}
				break;
			case "<>":
				if (where.get(key).get_value() == null) {
					this.where_str = this.where_str + " and " + key + " is not null ";
				} else {
					this.where_str = this.where_str + " and " + key + " !=? ";
					this.where_value_list.add(where.get(key).get_value());
				}
				break;
			case "neq":
				if (where.get(key).get_value() == null) {
					this.where_str = this.where_str + " and " + key + " is not null ";
				} else {
					this.where_str = this.where_str + " and " + key + " !=? ";
					this.where_value_list.add(where.get(key).get_value());
				}
				break;
			case ">":
				this.where_str = this.where_str + " and " + key + " >? ";
				this.where_value_list.add(where.get(key).get_value());
				break;
			case "gt":
				this.where_str = this.where_str + " and " + key + " >? ";
				this.where_value_list.add(where.get(key).get_value());
				break;
			case ">=":
				this.where_str = this.where_str + " and " + key + " >=? ";
				this.where_value_list.add(where.get(key).get_value());
				break;
			case "egt":
				this.where_str = this.where_str + " and " + key + " >=? ";
				this.where_value_list.add(where.get(key).get_value());
				break;
			case "lt":
				this.where_str = this.where_str + " and " + key + " <? ";
				this.where_value_list.add(where.get(key).get_value());
				break;
			case "elt":
				this.where_str = this.where_str + " and " + key + " <=? ";
				this.where_value_list.add(where.get(key).get_value());
				break;
			case "<":
				this.where_str = this.where_str + " and " + key + " <? ";
				this.where_value_list.add(where.get(key).get_value());
				break;
			case "<=":
				this.where_str = this.where_str + " and " + key + " <=? ";
				this.where_value_list.add(where.get(key).get_value());
				break;
			case "like":
				this.where_str = this.where_str + " and " + key + " like ? ";
				this.where_value_list.add(where.get(key).get_value());
				break;
			case "between":
				this.where_str = this.where_str + " and " + key + " between ? and ? ";
				if (where.get(key).get_value_list().get(0) != null && where.get(key).get_value_list().get(1) != null) {
					this.where_value_list.add(where.get(key).get_value_list().get(0));
					this.where_value_list.add(where.get(key).get_value_list().get(1));
				} else if (where.get(key).get_value_list().size() == 2) {
					this.where_value_list.add(where.get(key).get_value_list().get(0));
					this.where_value_list.add(where.get(key).get_value_list().get(1));
				}
				break;
			case "not between":
				this.where_str = this.where_str + " and " + key + " not between ? and ? ";
				if (where.get(key).get_value_list().get(0) != null && where.get(key).get_value_list().get(1) != null) {
					this.where_value_list.add(where.get(key).get_value_list().get(0));
					this.where_value_list.add(where.get(key).get_value_list().get(1));
				} else if (where.get(key).get_value_list().size() == 2) {
					this.where_value_list.add(where.get(key).get_value_list().get(0));
					this.where_value_list.add(where.get(key).get_value_list().get(1));
				}
				break;
			case "in":
				List<Object> values = new ArrayList<>();
				values.addAll(where.get(key).get_value_list());
				if (values.contains(null)) {
					values.remove(null);
					if (values.size() > 1) {
						String wenhao_str = "?";
						this.where_value_list.add(values.get(0));
						for (Integer integer = 1; integer < values.size(); integer = integer + 1) {
							wenhao_str = wenhao_str + ",?";
							this.where_value_list.add(values.get(integer));
						}
						this.where_str = this.where_str + " and " + key + " is null or" + key + " in (" + wenhao_str + ") ";
					} else if (values.size() == 1) {
						this.where_str = this.where_str + " and " + key + " is null or " + key + "=? ";
						this.where_value_list.add(values.get(0));
					} else if (values.size() == 0) {
						this.where_str = this.where_str + " and " + key + " is null ";
					}
				} else {
					if (values.size() > 1) {
						String wenhao_str = "?";
						this.where_value_list.add(values.get(0));
						for (Integer integer = 1; integer < values.size(); integer = integer + 1) {
							wenhao_str = wenhao_str + ",?";
							this.where_value_list.add(values.get(integer));
						}
						this.where_str = this.where_str + " and " + key + " in (" + wenhao_str + ") ";
					} else if (values.size() == 1) {
						this.where_str = this.where_str + " and " + key + "=? ";
						this.where_value_list.add(values.get(0));
					}
				}
				break;
			case "not in":
				List<Object> not_values = new ArrayList<>();
				not_values.addAll(where.get(key).get_value_list());
				if (not_values.contains(null)) {
					not_values.remove(null);
					if (not_values.size() > 1) {
						String wenhao_str = "?";
						this.where_value_list.add(not_values.get(0));
						for (Integer integer = 1; integer < not_values.size(); integer = integer + 1) {
							wenhao_str = wenhao_str + ",?";
							this.where_value_list.add(not_values.get(integer));
						}
						this.where_str = this.where_str + " and " + key + " is not null and " + key + " not in (" + wenhao_str + ") ";
					} else if (not_values.size() == 1) {
						this.where_str = this.where_str + " and " + key + " is not null and " + key + "!=? ";
						this.where_value_list.add(not_values.get(0));
					} else if (not_values.size() == 0) {
						this.where_str = this.where_str + " and " + key + " is not null ";
					}
				} else {
					if (not_values.size() > 1) {
						String wenhao_str = "?";
						this.where_value_list.add(not_values.get(0));
						for (Integer integer = 1; integer < not_values.size(); integer = integer + 1) {
							wenhao_str = wenhao_str + ",?";
							this.where_value_list.add(not_values.get(integer));
						}
						this.where_str = this.where_str + " and " + key + " not in (" + wenhao_str + ") ";
					} else if (not_values.size() == 1) {
						this.where_str = this.where_str + " and " + key + "!=? ";
						this.where_value_list.add(not_values.get(0));
					}
				}
				break;
			default:
				break;
			}
		}
		return this;
	}

	// where方法
	public MysqlDriver where(String where_str) {
		if (where_str.equals("")) {
			return this;
		} else {
			this.where_str = this.where_str + " and " + where_str + " ";
			return this;
		}
	}

	// where方法
	public MysqlDriver where(String where_str, Object... params) {
		if (where_str.equals("")) {
			return this;
		} else {
			this.where_str = this.where_str + " and " + where_str + " ";
			for (Integer integer = 0; integer < params.length; integer = integer + 1) {
				this.where_value_list.add(params[integer]);
			}
			return this;
		}
	}

	// order方法
	public MysqlDriver order(String order_str) {
		this.order_str = " order by " + order_str;
		return this;
	}

	// limit方法
	public MysqlDriver limit(String limit_str) {
		this.limit_str = " limit " + limit_str + " ";
		return this;
	}

	// data方法
	public MysqlDriver data(Map<String, Object> data) {
		String key_str = "";
		String value_str = "";
		for (String key : data.keySet()) {
			if (key_str.equals("")) {
				key_str = key;
				value_str = "?";
				this.data_value_list.add(data.get(key));
			} else {
				key_str = key_str + "," + key;
				value_str = value_str + ",?";
				this.data_value_list.add(data.get(key));
			}
		}
		key_str = "(" + key_str + ")";
		value_str = "(" + value_str + ")";
		this.add_str = " " + key_str + " values " + value_str + " ";
		return this;
	}

	// alias方法
	public MysqlDriver alias(String as_str) {
		this.as_str = " as " + as_str + " ";
		return this;
	}

	// join
	public MysqlDriver join(String table_name, String on_sql) {
		table_name = this.table_pre + table_name;
		String sql;
		sql = " inner join " + table_name + " " + on_sql + " ";
		this.join_str.add(sql);
		return this;
	}

	// join
	public MysqlDriver join(String table_name, String on_sql, String type) {
		table_name = this.table_pre + table_name;
		String sql;
		sql = " " + type + " join " + table_name + " " + on_sql + " ";
		this.join_str.add(sql);
		return this;
	}

	// filed方法
	public MysqlDriver field(String field_str) {
		this.field_str = " " + field_str + " ";
		return this;
	}

	// add方法
	public MysqlDriver add() {
		this.sql = "insert into `" + this.table_pre + this.table_name + "` " + this.add_str + ";";
		return this;
	}

	// save方法
	public MysqlDriver save(Map<String, Object> data) {
		String key_str = "";
		String value_str = "";
		String str = "";
		for (String key : data.keySet()) {
			if (key_str == "") {
				key_str = key;
				value_str = "?";
				this.update_value_list.add(data.get(key));
				str = " " + key_str + "=" + value_str + " ";
			} else {
				key_str = key;
				value_str = "?";
				this.update_value_list.add(data.get(key));
				str = str + "," + key_str + "=" + value_str + " ";
			}
		}
		str = " set " + str;
		this.sql = "update `" + this.table_pre + this.table_name + "`" + str + this.where_str + ";";
		return this;
	}

	// delete方法
	public MysqlDriver delete() {
		this.sql = "delete from `" + this.table_pre + this.table_name + "`" + this.where_str + ";";
		return this;
	}

	// select方法
	public MysqlDriver select() {
		String join_sql = "";
		int i = 0;
		while (i < this.join_str.size()) {
			join_sql = join_sql + this.join_str.get(i);
			i = i + 1;
		}
		this.sql = "select " + this.field_str + " from `" + this.table_pre + this.table_name + "`" + this.as_str + join_sql + this.where_str + this.group_str + this.order_str + this.limit_str + ";";
		return this;
	}

	// find方法
	public MysqlDriver find() {
		String join_sql = "";
		int i = 0;
		while (i < this.join_str.size()) {
			join_sql = join_sql + this.join_str.get(i);
			i = i + 1;
		}
		this.sql = "select " + this.field_str + " from `" + this.table_pre + this.table_name + "`" + this.as_str + join_sql + this.where_str + this.group_str + this.order_str + " limit 1;";
		return this;
	}

	// 获取sql
	public String get_sql() {
		return this.sql;
	}

	// 获取add参数
	public List<Object> get_add_data() {
		return this.data_value_list;
	}

	// 获取update参数
	public List<Object> get_update_data() {
		return this.update_value_list;
	}

	// 获取where参数
	public List<Object> get_where_data() {
		return this.where_value_list;
	}

	// group方法
	public void group(String fields) {
		this.group_str = " group by " + fields + " ";
	}

	public MysqlDriver setInc(String field, Integer value) {
		String str = "";
		str = " set " + field + "=" + field + "+" + value;
		this.sql = "update `" + this.table_pre + this.table_name + "`" + str + this.where_str + ";";
		return this;
	}

	public MysqlDriver setDec(String field, Integer value) {
		String str = "";
		str = " set " + field + "=" + field + "-" + value;
		this.sql = "update `" + this.table_pre + this.table_name + "`" + str + this.where_str + ";";
		return this;
	}

	public MysqlDriver setInc(String field) {
		String str = "";
		str = " set " + field + "=" + field + "+1";
		this.sql = "update `" + this.table_pre + this.table_name + "`" + str + this.where_str + ";";
		return this;
	}

	public MysqlDriver setDec(String field) {
		String str = "";
		str = " set " + field + "=" + field + "-1";
		this.sql = "update `" + this.table_pre + this.table_name + "`" + str + this.where_str + ";";
		return this;
	}

	// 清理数据
	public void clear() {
		// this.table_pre = "";
		this.sql = "";
		this.where_str = " where 1 ";
		this.where_value_list = new ArrayList<>();
		this.data_value_list = new ArrayList<>();
		this.update_value_list = new ArrayList<>();
		this.order_str = "";
		this.field_str = " * ";
		this.limit_str = "";
		this.add_str = "";
		this.as_str = "";
		this.group_str = "";
		this.join_str = new ArrayList<String>();
	}
}
