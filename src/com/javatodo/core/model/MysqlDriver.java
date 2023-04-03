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
	private String where_str = "";
	private List<Object> where_value_list = new ArrayList<>();
	private List<Object> data_value_list = new ArrayList<>();
	private List<String> batch_data_key_list = new ArrayList<>();
	private List<List<Object>> batch_data_value_list = new ArrayList<>();
	private List<Object> update_value_list = new ArrayList<>();
	private String order_str = "";
	private String field_str = " * ";
	private String limit_str = "";
	private String add_str = "";
	private String as_str = "";
	private List<String> join_str_list = new ArrayList<String>();
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
			String _where = "";
			String relation = where.get(key).get_relation().toLowerCase().trim();
			if (relation.equals("eq") || relation.equals("=")) {
				if (where.get(key).get_value() == null) {
					_where = key + " is null ";
				} else {
					_where = key + " = ? ";
					this.where_value_list.add(where.get(key).get_value());
				}
			}

			if (relation.equals("neq") || relation.equals("!=") || relation.equals("<>")) {
				if (where.get(key).get_value() == null) {
					_where = key + " is not null ";
				} else {
					_where = key + " != ? ";
					this.where_value_list.add(where.get(key).get_value());
				}
			}

			if (relation.equals(">") || relation.equals("gt")) {
				_where = key + " > ? ";
				this.where_value_list.add(where.get(key).get_value());
			}

			if (relation.equals(">=") || relation.equals("egt")) {
				_where = key + " >= ? ";
				this.where_value_list.add(where.get(key).get_value());
			}

			if (relation.equals("<") || relation.equals("lt")) {
				_where = key + " < ? ";
				this.where_value_list.add(where.get(key).get_value());
			}

			if (relation.equals("<=") || relation.equals("elt")) {
				_where = key + " <= ? ";
				this.where_value_list.add(where.get(key).get_value());
			}

			if (relation.equals("like")) {
				_where = key + " like ? ";
				this.where_value_list.add(where.get(key).get_value());
			}

			if (relation.equals("between")) {
				List<Object> value_list = where.get(key).get_value_list();
				if (value_list.size() > 1) {
					if (value_list.get(0) != null && value_list.get(1) != null) {
						_where = key + " between ? and ? ";
						this.where_value_list.add(value_list.get(0));
						this.where_value_list.add(value_list.get(1));
					}
				}
			}

			if (relation.equals("not between")) {
				List<Object> value_list = where.get(key).get_value_list();
				if (value_list.size() > 1) {
					if (value_list.get(0) != null && value_list.get(1) != null) {
						_where = key + " not between ? and ? ";
						this.where_value_list.add(value_list.get(0));
						this.where_value_list.add(value_list.get(1));
					}
				}
			}

			if (relation.equals("in")) {
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
						_where = " (" + key + " is null or " + key + " in (" + wenhao_str + ")) ";
					} else if (values.size() == 1) {
						_where = " (" + key + " is null or " + key + "= ?) ";
						this.where_value_list.add(values.get(0));
					} else if (values.size() == 0) {
						_where = key + " is null ";
					}
				} else {
					if (values.size() > 1) {
						String wenhao_str = "?";
						this.where_value_list.add(values.get(0));
						for (Integer integer = 1; integer < values.size(); integer = integer + 1) {
							wenhao_str = wenhao_str + ",?";
							this.where_value_list.add(values.get(integer));
						}
						_where = key + " in (" + wenhao_str + ") ";
					} else if (values.size() == 1) {
						_where = key + " = ? ";
						this.where_value_list.add(values.get(0));
					}
				}
			}

			if (relation.equals("not in")) {
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
						_where = key + " is not null and " + key + " not in (" + wenhao_str + ") ";
					} else if (not_values.size() == 1) {
						_where = key + " is not null and " + key + " != ? ";
						this.where_value_list.add(not_values.get(0));
					} else if (not_values.size() == 0) {
						_where = key + " is not null ";
					}
				} else {
					if (not_values.size() > 1) {
						String wenhao_str = "?";
						this.where_value_list.add(not_values.get(0));
						for (Integer integer = 1; integer < not_values.size(); integer = integer + 1) {
							wenhao_str = wenhao_str + ",?";
							this.where_value_list.add(not_values.get(integer));
						}
						_where = key + " not in (" + wenhao_str + ") ";
					} else if (not_values.size() == 1) {
						_where = key + " != ? ";
						this.where_value_list.add(not_values.get(0));
					}
				}
			}
			if (!_where.equals("")) {
				if (this.where_str.equals("")) {
					this.where_str = _where;
				} else {
					this.where_str = this.where_str + " and " + _where;
				}
			}
		}
		return this;
	}

	// where方法
	public MysqlDriver where(String where_str) {
		if (where_str.equals("")) {
			return this;
		} else {
			if (this.where_str.equals("")) {
				this.where_str = where_str;
			} else {
				this.where_str = this.where_str + " and " + where_str + " ";
			}
			return this;
		}
	}

	// where方法
	public MysqlDriver where(String where_str, Object... params) {
		if (where_str.equals("")) {
			return this;
		} else {
			if (this.where_str.equals("")) {
				this.where_str = where_str;
			} else {
				this.where_str = this.where_str + " and " + where_str + " ";
			}
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
		this.join_str_list.add(sql);
		return this;
	}

	// join
	public MysqlDriver join(String table_name, String on_sql, String type) {
		table_name = this.table_pre + table_name;
		String sql;
		sql = " " + type + " join " + table_name + " " + on_sql + " ";
		this.join_str_list.add(sql);
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

	public MysqlDriver add(List<Map<String, Object>> list) {
		if (list.size() > 0) {
			Map<String, Object> data = list.get(0);
			String key_str = "";
			String value_str = "";
			List<Object> _list = new ArrayList<>();
			for (String key : data.keySet()) {
				if (key_str.equals("")) {
					key_str = key;
					value_str = "?";
				} else {
					key_str = key_str + "," + key;
					value_str = value_str + ",?";
				}
				this.batch_data_key_list.add(key);
				_list.add(data.get(key));
			}
			this.batch_data_value_list.add(_list);

			for (Integer i = 1; i < list.size(); i = i + 1) {
				_list = new ArrayList<>();
				for (Integer kn = 0; kn < batch_data_key_list.size(); kn = kn + 1) {
					String _key = batch_data_key_list.get(kn);
					_list.add(list.get(i).get(_key));
				}
				this.batch_data_value_list.add(_list);
			}

			key_str = "(" + key_str + ")";
			value_str = "(" + value_str + ")";
			this.add_str = " " + key_str + " values " + value_str + " ";
			this.sql = "insert into `" + this.table_pre + this.table_name + "` " + this.add_str + ";";
		}

		return this;
	}

	// save方法
	public MysqlDriver save(Map<String, Object> data) {
		String str = "";
		for (String key : data.keySet()) {
			if (str.equals("")) {
				str = " " + key + " = ? ";
			} else {
				str = str + "," + key + " = ? ";
			}
			this.update_value_list.add(data.get(key));
		}
		str = " set " + str;
		if (!this.where_str.equals("")) {
			this.where_str = " where " + this.where_str;
		}
		this.sql = "update `" + this.table_pre + this.table_name + "`" + str + this.where_str + ";";
		return this;
	}

	// delete方法
	public MysqlDriver delete() {
		if (!this.where_str.equals("")) {
			this.where_str = " where " + this.where_str;
		}
		this.sql = "delete from `" + this.table_pre + this.table_name + "`" + this.where_str + ";";
		return this;
	}

	// select方法
	public MysqlDriver select() {
		String join_sql = "";
		int i = 0;
		while (i < this.join_str_list.size()) {
			join_sql = join_sql + this.join_str_list.get(i);
			i = i + 1;
		}
		if (!this.where_str.equals("")) {
			this.where_str = " where " + this.where_str;
		}
		this.sql = "select " + this.field_str + " from `" + this.table_pre + this.table_name + "`" + this.as_str + join_sql + this.where_str + this.group_str + this.order_str + this.limit_str + ";";
		return this;
	}

	// find方法
	public MysqlDriver find() {
		return this.limit("1").select();
	}

	// 获取sql
	public String get_sql() {
		return this.sql;
	}

	// 获取add参数
	public List<Object> get_add_data() {
		return this.data_value_list;
	}

	public List<List<Object>> get_batch_add_data_list() {
		return this.batch_data_value_list;
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
	public MysqlDriver group(String fields) {
		this.group_str = " group by " + fields + " ";
		return this;
	}

	public MysqlDriver setInc(String field, Integer value) {
		String str = "";
		str = " set " + field + "=" + field + "+" + value;
		if (!this.where_str.equals("")) {
			this.where_str = " where " + this.where_str;
		}
		this.sql = "update `" + this.table_pre + this.table_name + "`" + str + this.where_str + ";";
		return this;
	}

	public MysqlDriver setDec(String field, Integer value) {
		String str = "";
		str = " set " + field + "=" + field + "-" + value;
		if (!this.where_str.equals("")) {
			this.where_str = " where " + this.where_str;
		}
		this.sql = "update `" + this.table_pre + this.table_name + "`" + str + this.where_str + ";";
		return this;
	}

	public MysqlDriver setInc(String field) {
		return setInc(field, 1);
	}

	public MysqlDriver setDec(String field) {
		return setDec(field, 1);
	}

	// 清理数据
	public void clear() {
		// this.table_pre = "";
		this.sql = "";
		this.where_str = "";
		this.where_value_list = new ArrayList<>();
		this.data_value_list = new ArrayList<>();
		this.update_value_list = new ArrayList<>();
		this.order_str = "";
		this.field_str = " * ";
		this.limit_str = "";
		this.add_str = "";
		this.as_str = "";
		this.group_str = "";
		this.join_str_list = new ArrayList<String>();
		this.batch_data_key_list = new ArrayList<>();
		this.batch_data_value_list = new ArrayList<>();
	}
}
