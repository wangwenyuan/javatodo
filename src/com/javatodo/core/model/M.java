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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class M {
	private Connection connection = null;
	private Driver db = null;

	private String lastSql = "";
	private Object sqlParams;
	private boolean isTransaction = false;

	/**
	 * 实例化M对象，默认使用mysql驱动
	 * 
	 * @param connection Connection 数据库连接
	 */
	public M(Connection connection) {
		if (connection == null) {
			throw new Error("找不到数据源");
		}
		this.connection = connection;
		this.db = new MysqlDriver();
	}

	/**
	 * 实例化M对象，默认使用mysql驱动
	 * 
	 * @param connection Connection 数据库连接
	 * @param tableName  String 表名
	 */
	public M(Connection connection, String tableName) {
		if (connection == null) {
			throw new Error("找不到数据源");
		}
		this.connection = connection;
		this.db = new MysqlDriver(tableName);
	}

	/**
	 * table方法属于模型类的连贯操作方法之一，主要用于指定操作的数据表。
	 * 
	 * @param table_name String 数据表名
	 * @return
	 */
	public M table(String table_name) {
		this.db.table(table_name);
		return this;
	}

	/**
	 * 开启事务（注意：mysql中的MyISAM引擎不支持事务，建议使用InnoDB）
	 * 
	 * @throws SQLException
	 */
	public void transaction() throws SQLException {
		this.isTransaction = true;
		this.connection.setAutoCommit(false);
		Log.javatodo_sql_log(connection, "开启transaction");
	}

	public boolean getTransaction() {
		return isTransaction;
	}

	/**
	 * 提交（确保已开启事务）
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		if (isTransaction == false) {
			return;
		} else {
			this.isTransaction = false;
			this.connection.commit();
			Log.javatodo_sql_log(connection, "执行commit");
			this.close();
		}
	}

	/**
	 * 回滚（确保已开启事务）
	 */
	public void rollback() {
		if (isTransaction == false) {
			return;
		} else {
			this.isTransaction = false;
			try {
				this.connection.rollback();
				Log.javatodo_sql_log(connection, "执行rollback");
				this.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.javatodo_error_log(e);
			}

		}
	}

	/**
	 * 模型类的连贯操作方法之一，使用查询表达式可以支持更多的SQL查询语法
	 * 
	 * @param where Map<String 数据库字段名,W 查询条件>
	 * @return 模型类<br>
	 *         <br>
	 *         示例：<br>
	 *         M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 *         Map&lt;String, W&gt; w=new HashMap&lt;String, W&gt;();//实例化查询条件 <br>
	 *         w.put("id",new W("=",1));//设置查询条件<br>
	 *         w.put("is_del",new W("=",0));//设置查询条件<br>
	 *         List&lt;Map&lt;String, Object&gt;&gt;
	 *         list=m.where(w).select();//将查询条件传入查询方法<br>
	 *         所生成的sql语句是：select * from javatodo where `id`=1 and `is_del`=0<br>
	 */
	public M where(Map<String, W> where) {
		this.db.where(where);
		return this;
	}

	/**
	 * 模型类的连贯操作方法之一，主要用于字符串条件直接查询和操作
	 * 
	 * @param whereSql 查询条件语句
	 * @return 模型类<br>
	 *         <br>
	 *         示例：<br>
	 *         M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 *         List&lt;Map&lt;String, Object&gt;&gt; list=m.where("id=1 and
	 *         is_del=0").select();//将查询条件传入查询方法<br>
	 *         所生成的sql语句是：select * from javatodo where `id`=1 and `is_del`=0<br>
	 */
	public M where(String whereSql) {
		this.db.where(whereSql);
		return this;
	}

	/**
	 * 模型类的连贯操作方法之一，主要用于字符串条件直接查询和操作
	 * 
	 * @param whereSql 查询条件语句
	 * @param params   相关参数
	 * @return 模型类<br>
	 *         <br>
	 *         示例：<br>
	 *         M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 *         List&lt;Map&lt;String, Object&gt;&gt; list=m.where("id=? and
	 *         is_del=?",1,0).select();//将查询条件传入查询方法<br>
	 */
	public M where(String whereSql, Object... params) {
		this.db.where(whereSql, params);
		return this;
	}

	/**
	 * 用于对操作结果进行排序
	 * 
	 * @param orderSql 排序方式
	 * @return 模型类<br>
	 *         <br>
	 *         示例：<br>
	 *         M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 *         List&lt;Map&lt;String, Object&gt;&gt;
	 *         list=m.where("is_del=0").order("id desc").select();//将查询条件传入查询方法 <br>
	 *         所生成的sql语句是：select * from javatodo where `is_del`=0 order by id
	 *         desc<br>
	 */
	public M order(String orderSql) {
		this.db.order(orderSql);
		return this;
	}

	/**
	 * 用于对结果条数进行限制
	 * 
	 * @param limitSql 限制条数
	 * @return 模型类
	 */
	public M limit(String limitSql) {
		this.db.limit(limitSql);
		return this;
	}

	/**
	 * 设置要操作的数据对象的值，主要用于对数据库增加一条记录
	 * 
	 * @param data
	 * @return 模型类<br>
	 *         <br>
	 *         示例<br>
	 *         M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 *         Map&lt;String,Object&gt;d=new HashMap&lt;String,Object&gt;();<br>
	 *         d.put("name","javatodo");<br>
	 *         d.put("author","wangwenyuan");<br>
	 *         m.data(d).add();<br>
	 */
	public M data(Map<String, Object> data) {
		this.db.data(data);
		return this;
	}

	/**
	 * 设置当前数据表的别名。
	 * 
	 * @param asSql String 当前数据表的别名
	 * @return 模型类
	 */
	public M alias(String asSql) {
		this.db.alias(asSql);
		return this;
	}

	/**
	 * 根据两个或多个表中的列之间的关系，从这些表中查询数据(默认为inner连表查询)
	 * 
	 * @param tableName 所要连表的表名
	 * @param onSql     连表查询的条件
	 * @return 模型类<br>
	 *         <br>
	 *         示例：<br>
	 *         M m=new M("javatodo");<br>
	 *         List&lt;Map&lt;String,Object&gt;&gt; list=
	 *         m.alias("j").join("user","as u on
	 *         j.uid=u.id").where("u.id=1").select();<br>
	 *         生成的sql:select * from javatodo as j inner join user as u on j.uid=u.id
	 */
	public M join(String tableName, String onSql) {
		this.db.join(tableName, onSql);
		return this;
	}

	/**
	 * 根据两个或多个表中的列之间的关系，从这些表中查询数据
	 * 
	 * @param tableName String 所要连表的表名
	 * @param onSql     连表查询的条件
	 * @param type      连表查询的方式
	 * @return 模型类<br>
	 *         <br>
	 *         示例：<br>
	 *         M m=new M("javatodo");<br>
	 *         List&lt;Map&lt;String,Object&gt;&gt; list=
	 *         m.alias("j").join("user","as u on
	 *         j.uid=u.id","left").where("u.id=1").select();<br>
	 *         生成的sql:select * from javatodo as j left join user as u on j.uid=u.id
	 */
	public M join(String tableName, String onSql, String type) {
		this.db.join(tableName, onSql, type);
		return this;
	}

	/**
	 * 标识要返回或者操作的字段
	 * 
	 * @param fieldSql 字段名
	 * @return 模型类
	 */
	public M field(String fieldSql) {
		this.db.field(fieldSql);
		return this;
	}

	/**
	 * 用于结合合计函数，根据一个或多个列对结果集进行分组
	 * 
	 * @param fieldSql 需要分组的字段名
	 * @return 模型类
	 */
	public M group(String fieldSql) {
		this.db.group(fieldSql);
		return this;
	}

	/**
	 * 将数据写入数据库
	 * 
	 * @throws SQLException <br>
	 *                      示例：<br>
	 *                      M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 *                      Map&lt;String,Object&gt;d=new
	 *                      HashMap&lt;String,Object&gt;(); <br>
	 *                      d.put("name","javatodo");<br>
	 *                      d.put("author","wangwenyuan");<br>
	 *                      m.data(d).add();<br>
	 */
	public Object add() throws SQLException {
		Object lastId = null;
		if (this.connection != null) {
			this.db.add();
			String sql = this.db.getSql();
			List<Object> add_data_list = this.db.getAddData();
			Log.javatodo_sql_log(connection, "开始执行sql：\n" + sql + "\n--------\n add_data_list：" + add_data_list);
			PreparedStatement ptmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (Integer i = 0; i < add_data_list.size(); i = i + 1) {
				ptmt.setObject(i + 1, add_data_list.get(i));
			}
			ptmt.execute();
			ResultSet rs = ptmt.getGeneratedKeys();
			Log.javatodo_sql_log(connection, "sql执行结束");
			while (rs.next()) {
				lastId = rs.getObject(1);
			}
			ptmt.close();
			rs.close();
			this.db.clear();
			this.lastSql = sql;
			this.sqlParams = add_data_list;
			if (!this.isTransaction) {
				this.close();
			}
		}
		return lastId;
	}

	/**
	 * 将数据写入数据库
	 * 
	 * @throws SQLException
	 */
	public void add(List<Map<String, Object>> list) throws SQLException {
		if (this.connection != null) {
			this.db.add(list);
			String sql = this.db.getSql();
			List<List<Object>> batch_add_data_list = this.db.getBatchAddDataList();
			Log.javatodo_sql_log(connection, "开始执行sql：\n" + sql);
			PreparedStatement ptmt = connection.prepareStatement(sql);
			Integer n = 0;
			for (Integer i = 0; i < batch_add_data_list.size(); i = i + 1) {
				List<Object> add_data_list = batch_add_data_list.get(i);
				for (Integer ii = 0; ii < add_data_list.size(); ii = ii + 1) {
					n = n + 1;
					ptmt.setObject(n, add_data_list.get(ii));
				}
			}
			ptmt.execute();
			ptmt.close();
			Log.javatodo_sql_log(connection, "sql执行结束");
			this.db.clear();
			this.lastSql = sql;
			this.sqlParams = batch_add_data_list;
			if (!this.isTransaction) {
				this.close();
			}
		}
	}

	/**
	 * 修改数据库中的某条记录
	 * 
	 * @param data Map<String, Object> 数据参数
	 * @throws SQLException 示例<br>
	 *                      <br>
	 *                      M m=new M("web");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 *                      Map&lt;String,Object&gt;d=new
	 *                      HashMap&lt;String,Object&gt;(); <br>
	 *                      d.put("name","javatodo");<br>
	 *                      d.put("url","javatodo.com");<br>
	 *                      m.where("id=1").save(d);
	 */
	public Integer save(Map<String, Object> data) throws SQLException {
		Integer ret = 0;
		if (this.connection != null) {
			this.db.save(data);
			String sql = this.db.getSql();
			List<Object> update_data_list = this.db.getUpdateData();
			List<Object> where_data_list = this.db.getWhereData();
			Integer all_total = update_data_list.size() + where_data_list.size();
			Object[] params = new Object[all_total];
			for (Integer integer = 0; integer < update_data_list.size(); integer = integer + 1) {
				params[integer] = update_data_list.get(integer);
			}
			for (Integer integer = update_data_list.size(); integer < all_total; integer = integer + 1) {
				params[integer] = where_data_list.get(integer - update_data_list.size());
			}
			Log.javatodo_sql_log(connection, "开始执行sql：\n" + sql + "\n--------\n update参数：" + update_data_list.toString()
					+ "\n--------\n where参数：" + where_data_list);
			PreparedStatement ptmt = connection.prepareStatement(sql);
			for (Integer i = 0; i < params.length; i = i + 1) {
				ptmt.setObject(i + 1, params[i]);
			}
			ret = ptmt.executeUpdate();
			ptmt.close();
			Log.javatodo_sql_log(connection, "sql执行结束");
			this.db.clear();
			this.lastSql = sql;
			this.sqlParams = params;
			if (!this.isTransaction) {
				this.close();
			}
		}
		return ret;
	}

	/**
	 * 对于统计字段（通常指的是数字类型）的更新（增加）
	 * 
	 * @param field 字段名
	 * @param value 增加的值
	 * @throws SQLException 示例<br>
	 *                      <br>
	 *                      M m=new M("web");//实例化M对象;<br>
	 *                      m.where("id=1").setInc("num", 1);
	 */
	public Integer setInc(String field, Integer value) throws SQLException {
		Integer ret = 0;
		if (this.connection != null) {
			this.db.setInc(field, value);
			String sql = this.db.getSql();
			Log.javatodo_sql_log(connection, "开始执行sql：\n" + sql);
			PreparedStatement ptmt = connection.prepareStatement(sql);
			ret = ptmt.executeUpdate();
			Log.javatodo_sql_log(connection, "sql执行结束");
			ptmt.close();
			this.db.clear();
			this.lastSql = sql;
			this.sqlParams = "";
			if (!this.isTransaction) {
				this.close();
			}
		}
		return ret;
	}

	/**
	 * 对于统计字段（通常指的是数字类型）加1
	 * 
	 * @param field 字段名
	 * @throws SQLException 示例<br>
	 *                      <br>
	 *                      M m=new M("web");//实例化M对象;<br>
	 *                      m.where("id=1").setInc("num");
	 */
	public Integer setInc(String field) throws SQLException {
		Integer ret = 0;
		if (this.connection != null) {
			this.db.setInc(field);
			String sql = this.db.getSql();
			Log.javatodo_sql_log(connection, "开始执行sql：\n" + sql);
			PreparedStatement ptmt = connection.prepareStatement(sql);
			ret = ptmt.executeUpdate();
			Log.javatodo_sql_log(connection, "sql执行结束");
			ptmt.close();
			this.db.clear();
			this.lastSql = sql;
			this.sqlParams = "";
			if (!this.isTransaction) {
				this.close();
			}
		}
		return ret;
	}

	/**
	 * 对于统计字段（通常指的是数字类型）的更新（减少）
	 * 
	 * @param field 字段名
	 * @param value 减去的值
	 * @throws SQLException 示例<br>
	 *                      <br>
	 *                      M m=new M("web");//实例化M对象;<br>
	 *                      m.where("id=1").setDec("num", 1);
	 */
	public Integer setDec(String field, Integer value) throws SQLException {
		Integer ret = 0;
		if (this.connection != null) {
			this.db.setDec(field, value);
			String sql = this.db.getSql();
			Log.javatodo_sql_log(connection, "开始执行sql：\n" + sql);
			PreparedStatement ptmt = connection.prepareStatement(sql);
			ret = ptmt.executeUpdate();
			Log.javatodo_sql_log(connection, "sql执行结束");
			ptmt.close();
			this.db.clear();
			this.lastSql = sql;
			this.sqlParams = "";
			if (!this.isTransaction) {
				this.close();
			}
		}
		return ret;
	}

	/**
	 * 对于统计字段（通常指的是数字类型）减1
	 * 
	 * @param field 字段名
	 * @throws SQLException 示例<br>
	 *                      <br>
	 *                      M m=new M("web");//实例化M对象;<br>
	 *                      m.where("id=1").setDec("num");
	 */
	public Integer setDec(String field) throws SQLException {
		Integer ret = 0;
		if (this.connection != null) {
			this.db.setDec(field);
			String sql = this.db.getSql();
			Log.javatodo_sql_log(connection, "开始执行sql：\n" + sql);
			PreparedStatement ptmt = connection.prepareStatement(sql);
			ret = ptmt.executeUpdate();
			Log.javatodo_sql_log(connection, "sql执行结束");
			ptmt.close();
			this.db.clear();
			this.lastSql = sql;
			this.sqlParams = "";
			if (!this.isTransaction) {
				this.close();
			}
		}
		return ret;
	}

	/**
	 * 删除数据库中的某条记录
	 * 
	 * @throws SQLException 示例：<br>
	 *                      <br>
	 *                      M m=new M("web");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 *                      m.where("id=15").delete();
	 */
	public Integer delete() throws SQLException {
		Integer ret = 0;
		if (this.connection != null) {
			this.db.delete();
			String sql = this.db.getSql();
			List<Object> where_data_list = this.db.getWhereData();
			Log.javatodo_sql_log(connection,
					"开始执行sql：\n" + sql + "\n---------------\n where参数：" + where_data_list.toString());
			PreparedStatement ptmt = connection.prepareStatement(sql);
			for (Integer i = 0; i < where_data_list.size(); i = i + 1) {
				ptmt.setObject(i + 1, where_data_list.get(i));
			}
			ret = ptmt.executeUpdate();
			ptmt.close();
			Log.javatodo_sql_log(connection, "sql执行结束");
			this.db.clear();
			this.lastSql = sql;
			this.sqlParams = where_data_list;
			if (!this.isTransaction) {
				this.close();
			}
		}
		return ret;
	}

	/**
	 * 获取数据表中的多行记录
	 * 
	 * @return List<Map<String 字段名, Object 值>> 一个list对象，其中每一条记录是一个以map形式的数据。
	 * @throws SQLException
	 */
	public List<Map<String, Object>> select() throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (this.connection != null) {
			this.db.select();
			String sql = this.db.getSql();
			List<Object> where_data_list = this.db.getWhereData();
			Log.javatodo_sql_log(connection, "开始执行sql：\n" + sql + "\n--------\n where参数：" + where_data_list.toString());
			PreparedStatement ptmt = connection.prepareStatement(sql);
			for (Integer i = 0; i < where_data_list.size(); i = i + 1) {
				ptmt.setObject(i + 1, where_data_list.get(i));
			}
			ResultSet resultSet = ptmt.executeQuery();
			list = this.resultSetToMap(resultSet);
			ptmt.close();
			resultSet.close();
			Log.javatodo_sql_log(connection, "sql执行结束");
			this.db.clear();
			this.lastSql = sql;
			this.sqlParams = where_data_list;
			if (!this.isTransaction) {
				this.close();
			}
			return list;
		} else {
			return list;
		}
	}

	/**
	 * 获取数据表中的一行记录
	 * 
	 * @return Map<String 字段名, Object 值> 一个以map形式的数据记录
	 * @throws SQLException
	 */
	public Map<String, Object> find() throws SQLException {
		Map<String, Object> map = null;
		if (this.connection != null) {
			this.db.find();
			String sql = this.db.getSql();
			List<Object> where_data_list = this.db.getWhereData();
			Log.javatodo_sql_log(connection,
					"开始执行sql：\n" + sql + "\n-----------\n where参数：" + where_data_list.toString());
			PreparedStatement ptmt = connection.prepareStatement(sql);
			for (Integer i = 0; i < where_data_list.size(); i = i + 1) {
				ptmt.setObject(i + 1, where_data_list.get(i));
			}
			ResultSet resultSet = ptmt.executeQuery();
			List<Map<String, Object>> list = this.resultSetToMap(resultSet);
			if (list.size() > 0) {
				map = list.get(0);
			}
			ptmt.close();
			resultSet.close();
			Log.javatodo_sql_log(connection, "sql执行结束");
			this.db.clear();
			this.lastSql = sql;
			this.sqlParams = where_data_list;
			if (!this.isTransaction) {
				this.close();
			}
			return map;
		} else {
			return map;
		}
	}

	/**
	 * 读取字段值,就是获取数据表中的单个数据
	 * 
	 * @param field_name
	 * @return Object 当前记录中field_name字段对应的值
	 * @throws SQLException
	 */
	public Object getField(String field_name) throws SQLException {
		Object object = null;
		if (this.connection != null) {
			object = new Object();
			this.db.field(field_name);
			Map<String, Object> map = this.find();
			if (map == null) {
				return null;
			}
			if (map.containsKey(field_name)) {
				object = map.get(field_name);
			} else {
				object = null;
			}
			if (!this.isTransaction) {
				this.close();
			}
			return object;
		} else {
			return object;
		}
	}

	/**
	 * 用于执行SQL查询操作,主要用于“查询”操作，“增、删、改”操作请用execute()
	 * 
	 * @param sql String sql语句
	 * @return List<Map<String 字段名, Object 值>> 一个list对象，其中每一条记录是一个以map形式的数据。
	 * @throws SQLException
	 */
	public List<Map<String, Object>> query(String sql) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (this.connection != null) {
			Log.javatodo_sql_log(connection, "开始执行sql：\n" + sql);
			PreparedStatement ptmt = connection.prepareStatement(sql);
			ResultSet resultSet = ptmt.executeQuery();
			list = this.resultSetToMap(resultSet);
			Log.javatodo_sql_log(connection, "sql执行结束");
			ptmt.close();
			resultSet.close();
			this.lastSql = sql;
			this.sqlParams = "";
			if (!this.isTransaction) {
				this.close();
			}
			return list;
		} else {
			return list;
		}
	}

	/**
	 * 用于执行SQL查询操作,主要用于“查询”操作，“增、删、改”操作请用execute()
	 * 
	 * @param sql    String sql语句
	 * @param params 参数值
	 * @return List<Map<String 字段名, Object 值>> 一个list对象，其中每一条记录是一个以map形式的数据。
	 * @throws SQLException
	 */
	public List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (this.connection != null) {
			Log.javatodo_sql_log(connection, "开始执行sql：" + sql + "\n------------\n 参数：" + params.toString());
			PreparedStatement ptmt = connection.prepareStatement(sql);
			for (Integer i = 0; i < params.length; i = i + 1) {
				ptmt.setObject(i + 1, params[i]);
			}
			ResultSet resultSet = ptmt.executeQuery();
			list = this.resultSetToMap(resultSet);
			ptmt.close();
			resultSet.close();
			Log.javatodo_sql_log(connection, "sql执行结束");
			this.lastSql = sql;
			this.sqlParams = params;
			if (!this.isTransaction) {
				this.close();
			}
			return list;
		} else {
			return list;
		}
	}

	/**
	 * 获取记录的总条数
	 * 
	 * @return Integer 记录的总条数
	 * @throws SQLException
	 */
	public long count() throws SQLException {
		if (this.connection != null) {
			Object count = this.getField("count(*)");
			if (!this.isTransaction) {
				this.close();
			}
			if (count == null) {
				return 0;
			} else {
				return Long.valueOf(count.toString());
			}
		} else {
			return 0;
		}
	}

	/**
	 * 用于更新和写入数据的sql操作,主要用于“增、删、改”的操作，查询操作请用query()
	 * 
	 * @param sql String sql语句
	 * @throws SQLException
	 */
	public void execute(String sql) throws SQLException {
		if (this.connection != null) {
			Log.javatodo_sql_log(connection, "开始执行sql：" + sql);
			PreparedStatement ptmt = connection.prepareStatement(sql);
			ptmt.execute();
			ptmt.close();
			Log.javatodo_sql_log(connection, "sql执行结束");
			this.lastSql = sql;
			this.sqlParams = "";
			if (!this.isTransaction) {
				this.close();
			}
		}
	}

	/**
	 * 用于更新和写入数据的sql操作,主要用于“增、删、改”的操作，查询操作请用query()
	 * 
	 * @param sql    String sql语句
	 * @param params 参数值
	 * @throws SQLException
	 */
	public void execute(String sql, Object... params) throws SQLException {
		if (this.connection != null) {
			Log.javatodo_sql_log(connection, "开始执行sql：" + sql + "----------参数：" + params.toString());
			PreparedStatement ptmt = connection.prepareStatement(sql);
			for (Integer i = 0; i < params.length; i = i + 1) {
				ptmt.setObject(i + 1, params[i]);
			}
			ptmt.executeUpdate();
			ptmt.close();
			Log.javatodo_sql_log(connection, "sql执行结束");
			this.lastSql = sql;
			this.sqlParams = params;
			if (!this.isTransaction) {
				this.close();
			}
		}
	}

	public void clear() {
		if (this.db != null) {
			this.db.clear();
		}
	}

	/**
	 * 输出上次执行的sql语句
	 * 
	 * @return String 上次执行的sql语句
	 */
	public void getLastSql() {
		System.out.println(this.lastSql);
		System.out.println(this.sqlParams);
	}

	/**
	 * 关闭数据库连接
	 */
	private void close() {
		if (this.connection != null) {
			try {
				this.connection.close();
				Log.javatodo_sql_log(connection, "connect关闭了");
				this.connection = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.javatodo_error_log(e);
			}
		}
	}

	private List<Map<String, Object>> resultSetToMap(ResultSet resultSet) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (resultSet.next()) {
			ResultSetMetaData rsm = resultSet.getMetaData();
			// ResultSetMetaData 接口创建一个对象，可使用该对象找出 ResultSet 中的各列的类型和属性。
			int size = rsm.getColumnCount(); // 每行列数
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			for (int j = 1; j <= size; j++) {
				map.put(rsm.getColumnLabel(j), resultSet.getObject(j));
			}
			list.add(map);
		}
		return list;
	}
}
