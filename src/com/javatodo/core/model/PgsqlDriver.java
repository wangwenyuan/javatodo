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

public class PgsqlDriver extends Driver {
	private String tablePrefix = "";// 表前缀
	private String sql = "";
	private String tableName = "";
	private String whereSql = "";
	private List<Object> whereValueList = new ArrayList<>();
	private List<Object> dataValueList = new ArrayList<>();
	private List<String> batchDataKeyList = new ArrayList<>();
	private List<List<Object>> batchDataValueList = new ArrayList<>();
	private List<Object> updateValueList = new ArrayList<>();
	private String orderSql = "";
	private String fieldSql = " * ";
	private String limitSql = "";
	private String addSql = "";
	private String asSql = "";
	private List<String> joinSqlList = new ArrayList<String>();
	private String groupSql = "";

	public PgsqlDriver() {
		this.tablePrefix = MC.tablePrefix.get(0);
	}

	public PgsqlDriver(Integer dbIndex) {
		this.tablePrefix = MC.tablePrefix.get(dbIndex);
	}

	// 初始化当前表
	public PgsqlDriver(String table_name) {
		this.tablePrefix = MC.tablePrefix.get(0);
		this.tableName = table_name;
	}

	// 初始化当前表
	public PgsqlDriver(String table_name, Integer dbIndex) {
		this.tablePrefix = MC.tablePrefix.get(dbIndex);
		this.tableName = table_name;
	}

	// 设置表名称
	public void table(String tableName) {
		this.tableName = tableName;
	}

	// where方法
	public PgsqlDriver where(Map<String, W> where) {
		for (String key : where.keySet()) {
			String _where = "";
			Relation relation = where.get(key).getRelation();
			if (Relation.EQ == relation) {
				if (where.get(key).getValue() == null) {
					_where = key + " is null ";
				} else {
					_where = key + " = ? ";
					this.whereValueList.add(where.get(key).getValue());
				}
			}

			if (Relation.NEQ == relation) {
				if (where.get(key).getValue() == null) {
					_where = key + " is not null ";
				} else {
					_where = key + " != ? ";
					this.whereValueList.add(where.get(key).getValue());
				}
			}

			if (Relation.GT == relation) {
				_where = key + " > ? ";
				this.whereValueList.add(where.get(key).getValue());
			}

			if (Relation.EGT == relation) {
				_where = key + " >= ? ";
				this.whereValueList.add(where.get(key).getValue());
			}

			if (Relation.LT == relation) {
				_where = key + " < ? ";
				this.whereValueList.add(where.get(key).getValue());
			}

			if (Relation.ELT == relation) {
				_where = key + " <= ? ";
				this.whereValueList.add(where.get(key).getValue());
			}

			if (Relation.LIKE == relation) {
				_where = key + " like ? ";
				this.whereValueList.add(where.get(key).getValue());
			}

			if (Relation.BETWEEN == relation) {
				List<Object> valueList = where.get(key).getValueList();
				if (valueList.size() > 1) {
					if (valueList.get(0) != null && valueList.get(1) != null) {
						_where = key + " between ? and ? ";
						this.whereValueList.add(valueList.get(0));
						this.whereValueList.add(valueList.get(1));
					}
				}
			}

			if (Relation.NOTBETWEEN == relation) {
				List<Object> valueList = where.get(key).getValueList();
				if (valueList.size() > 1) {
					if (valueList.get(0) != null && valueList.get(1) != null) {
						_where = key + " not between ? and ? ";
						this.whereValueList.add(valueList.get(0));
						this.whereValueList.add(valueList.get(1));
					}
				}
			}

			if (Relation.IN == relation) {
				List<Object> values = new ArrayList<>();
				values.addAll(where.get(key).getValueList());
				if (values.contains(null)) {
					values.remove(null);
					if (values.size() > 1) {
						String paramSql = "?";
						this.whereValueList.add(values.get(0));
						for (Integer integer = 1; integer < values.size(); integer = integer + 1) {
							paramSql = paramSql + ",?";
							this.whereValueList.add(values.get(integer));
						}
						_where = " (" + key + " is null or " + key + " in (" + paramSql + ")) ";
					} else if (values.size() == 1) {
						_where = " (" + key + " is null or " + key + "= ?) ";
						this.whereValueList.add(values.get(0));
					} else if (values.size() == 0) {
						_where = key + " is null ";
					}
				} else {
					if (values.size() > 1) {
						String paramSql = "?";
						this.whereValueList.add(values.get(0));
						for (Integer integer = 1; integer < values.size(); integer = integer + 1) {
							paramSql = paramSql + ",?";
							this.whereValueList.add(values.get(integer));
						}
						_where = key + " in (" + paramSql + ") ";
					} else if (values.size() == 1) {
						_where = key + " = ? ";
						this.whereValueList.add(values.get(0));
					}
				}
			}

			if (Relation.NOTIN == relation) {
				List<Object> notValues = new ArrayList<>();
				notValues.addAll(where.get(key).getValueList());
				if (notValues.contains(null)) {
					notValues.remove(null);
					if (notValues.size() > 1) {
						String paramSql = "?";
						this.whereValueList.add(notValues.get(0));
						for (Integer integer = 1; integer < notValues.size(); integer = integer + 1) {
							paramSql = paramSql + ",?";
							this.whereValueList.add(notValues.get(integer));
						}
						_where = key + " is not null and " + key + " not in (" + paramSql + ") ";
					} else if (notValues.size() == 1) {
						_where = key + " is not null and " + key + " != ? ";
						this.whereValueList.add(notValues.get(0));
					} else if (notValues.size() == 0) {
						_where = key + " is not null ";
					}
				} else {
					if (notValues.size() > 1) {
						String paramSql = "?";
						this.whereValueList.add(notValues.get(0));
						for (Integer integer = 1; integer < notValues.size(); integer = integer + 1) {
							paramSql = paramSql + ",?";
							this.whereValueList.add(notValues.get(integer));
						}
						_where = key + " not in (" + paramSql + ") ";
					} else if (notValues.size() == 1) {
						_where = key + " != ? ";
						this.whereValueList.add(notValues.get(0));
					}
				}
			}
			if (!_where.equals("")) {
				if (this.whereSql.equals("")) {
					this.whereSql = _where;
				} else {
					this.whereSql = this.whereSql + " and " + _where;
				}
			}
		}
		return this;
	}

	// where方法
	public PgsqlDriver where(String whereSql) {
		if (whereSql.equals("")) {
			return this;
		} else {
			if (this.whereSql.equals("")) {
				this.whereSql = whereSql;
			} else {
				this.whereSql = this.whereSql + " and " + whereSql + " ";
			}
			return this;
		}
	}

	// where方法
	public PgsqlDriver where(String whereSql, Object... params) {
		if (whereSql.equals("")) {
			return this;
		} else {
			if (this.whereSql.equals("")) {
				this.whereSql = whereSql;
			} else {
				this.whereSql = this.whereSql + " and " + whereSql + " ";
			}
			for (Integer i = 0; i < params.length; i = i + 1) {
				this.whereValueList.add(params[i]);
			}
			return this;
		}
	}

	// order方法
	public PgsqlDriver order(String orderSql) {
		this.orderSql = " order by " + orderSql;
		return this;
	}

	// limit方法
	public PgsqlDriver limit(String limitSql) {
		String[] limitArr = new String[2];
		if (limitSql.contains(",")) {
			limitArr = limitSql.split(",");
			this.limitSql = " limit " + limitArr[1] + " offset " + limitArr[0] + " ";
		} else {
			this.limitSql = " limit " + limitSql + " ";
		}
		return this;
	}

	// data方法
	public PgsqlDriver data(Map<String, Object> data) {
		String _key = "";
		String _value = "";
		for (String key : data.keySet()) {
			if (_key.equals("")) {
				_key = key;
				_value = "?";
				this.dataValueList.add(data.get(key));
			} else {
				_key = _key + "," + key;
				_value = _value + ",?";
				this.dataValueList.add(data.get(key));
			}
		}
		_key = "(" + _key + ")";
		_value = "(" + _value + ")";
		this.addSql = " " + _key + " values " + _value + " ";
		return this;
	}

	// alias方法
	public PgsqlDriver alias(String asSql) {
		this.asSql = " as " + asSql + " ";
		return this;
	}

	// join
	public PgsqlDriver join(String tableName, String onSql) {
		tableName = this.tablePrefix + tableName;
		String sql;
		sql = " inner join " + tableName + " " + onSql + " ";
		this.joinSqlList.add(sql);
		return this;
	}

	// join
	public PgsqlDriver join(String tableName, String onSql, String type) {
		tableName = this.tablePrefix + tableName;
		String sql;
		sql = " " + type + " join " + tableName + " " + onSql + " ";
		this.joinSqlList.add(sql);
		return this;
	}

	// filed方法
	public PgsqlDriver field(String fieldSql) {
		this.fieldSql = " " + fieldSql + " ";
		return this;
	}

	// add方法
	public PgsqlDriver add() {
		this.sql = "insert into `" + this.tablePrefix + this.tableName + "` " + this.addSql + ";";
		return this;
	}

	public PgsqlDriver add(List<Map<String, Object>> list) {
		if (list.size() > 0) {
			Map<String, Object> data = list.get(0);
			String dataKeys = "";
			String dataValues = "";
			List<Object> _list = new ArrayList<>();
			for (String key : data.keySet()) {
				if (dataKeys.equals("")) {
					dataKeys = key;
					dataValues = "?";
				} else {
					dataKeys = dataKeys + "," + key;
					dataValues = dataValues + ",?";
				}
				this.batchDataKeyList.add(key);
				_list.add(data.get(key));
			}
			this.batchDataValueList.add(_list);

			for (Integer i = 1; i < list.size(); i = i + 1) {
				_list = new ArrayList<>();
				for (Integer kn = 0; kn < batchDataKeyList.size(); kn = kn + 1) {
					String _key = batchDataKeyList.get(kn);
					_list.add(list.get(i).get(_key));
				}
				this.batchDataValueList.add(_list);
			}

			dataKeys = "(" + dataKeys + ")";
			dataValues = "(" + dataValues + ")";
			this.addSql = " " + dataKeys + " values " + dataValues + " ";
			this.sql = "insert into `" + this.tablePrefix + this.tableName + "` " + this.addSql + ";";
		}

		return this;
	}

	// save方法
	public PgsqlDriver save(Map<String, Object> data) {
		String dataKeys = "";
		for (String key : data.keySet()) {
			if (dataKeys.equals("")) {
				dataKeys = " " + key + " = ? ";
			} else {
				dataKeys = dataKeys + "," + key + " = ? ";
			}
			this.updateValueList.add(data.get(key));
		}
		dataKeys = " set " + dataKeys;
		if (!this.whereSql.equals("")) {
			this.whereSql = " where " + this.whereSql;
		}
		this.sql = "update `" + this.tablePrefix + this.tableName + "`" + dataKeys + this.whereSql + ";";
		return this;
	}

	// delete方法
	public PgsqlDriver delete() {
		if (!this.whereSql.equals("")) {
			this.whereSql = " where " + this.whereSql;
		}
		this.sql = "delete from `" + this.tablePrefix + this.tableName + "`" + this.whereSql + ";";
		return this;
	}

	// select方法
	public PgsqlDriver select() {
		String joinSql = "";
		int i = 0;
		while (i < this.joinSqlList.size()) {
			joinSql = joinSql + this.joinSqlList.get(i);
			i = i + 1;
		}
		if (!this.whereSql.equals("")) {
			this.whereSql = " where " + this.whereSql;
		}
		this.sql = "select " + this.fieldSql + " from `" + this.tablePrefix + this.tableName + "`" + this.asSql
				+ joinSql + this.whereSql + this.groupSql + this.orderSql + this.limitSql + ";";
		return this;
	}

	// find方法
	public PgsqlDriver find() {
		return this.limit("1").select();
	}

	// 获取sql
	public String getSql() {
		return this.sql;
	}

	// 获取add参数
	public List<Object> getAddData() {
		return this.dataValueList;
	}

	public List<List<Object>> getBatchAddDataList() {
		return this.batchDataValueList;
	}

	// 获取update参数
	public List<Object> getUpdateData() {
		return this.updateValueList;
	}

	// 获取where参数
	public List<Object> getWhereData() {
		return this.whereValueList;
	}

	// group方法
	public PgsqlDriver group(String fields) {
		this.groupSql = " group by " + fields + " ";
		return this;
	}

	public PgsqlDriver setInc(String field, Integer value) {
		String str = "";
		str = " set " + field + "=" + field + "+" + value;
		if (!this.whereSql.equals("")) {
			this.whereSql = " where " + this.whereSql;
		}
		this.sql = "update `" + this.tablePrefix + this.tableName + "`" + str + this.whereSql + ";";
		return this;
	}

	public PgsqlDriver setDec(String field, Integer value) {
		String str = "";
		str = " set " + field + "=" + field + "-" + value;
		if (!this.whereSql.equals("")) {
			this.whereSql = " where " + this.whereSql;
		}
		this.sql = "update `" + this.tablePrefix + this.tableName + "`" + str + this.whereSql + ";";
		return this;
	}

	public PgsqlDriver setInc(String field) {
		return setInc(field, 1);
	}

	public PgsqlDriver setDec(String field) {
		return setDec(field, 1);
	}

	// 清理数据
	public void clear() {
		this.sql = "";
		this.whereSql = "";
		this.whereValueList = new ArrayList<>();
		this.dataValueList = new ArrayList<>();
		this.updateValueList = new ArrayList<>();
		this.orderSql = "";
		this.fieldSql = " * ";
		this.limitSql = "";
		this.addSql = "";
		this.asSql = "";
		this.groupSql = "";
		this.joinSqlList = new ArrayList<String>();
		this.batchDataKeyList = new ArrayList<>();
		this.batchDataValueList = new ArrayList<>();
	}
}
