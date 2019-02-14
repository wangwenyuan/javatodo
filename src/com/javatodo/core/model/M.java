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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

public class M {
	private Connection connection = null;
	private QueryRunner queryRunner = new QueryRunner();
	private Driver db = null;
	
	private String lastSql="";
	private Object sql_params;
	private boolean is_transaction=false;
	
	/**
	 * 实例化M对象
	 */
	public M(){
		this.connection = MC.get_connection();
		if (this.connection == null) {
			System.out.println("找不到数据源");
		}else{
			if ("mysql".equals(MC.db_type)) {
				this.db=new MysqlDriver();
			}
			if("postgresql".equals(MC.db_type)){
				this.db=new PgsqlDriver();
			}
		}
	}
	
	/**
	 * 实例化M对象
	 * @param table_name String 表名
	 * @throws Exception
	 */
	public M(String table_name){
		this.connection = MC.get_connection();
		if (this.connection == null) {
			System.out.println("找不到数据源");
		}else{
			if ("mysql".equals(MC.db_type)) {
				this.db = new MysqlDriver(table_name);
			}
			if("postgresql".equals(MC.db_type)){
				this.db=new PgsqlDriver(table_name);
			}
		}
	}
	
	/**
	 * table方法属于模型类的连贯操作方法之一，主要用于指定操作的数据表。
	 * @param table_name String 数据表名
	 * @return
	 */
	public M table(String table_name){
		this.db.table(table_name);
		return this;
	}
	
	/**
	 * 开启事务（注意：mysql中的MyISAM引擎不支持事务，建议使用InnoDB）
	 * @throws SQLException
	 */
	public void transaction() throws SQLException{
		this.is_transaction=true;
		if(this.connection!=null){
			this.connection.setAutoCommit(false);
		}else{
			this.connection=MC.get_connection();
			if(this.connection!=null){
				this.connection.setAutoCommit(false);
			}
		}
	}
	
	/**
	 * 提交（确保已开启事务）
	 * @throws SQLException
	 */
	public void commit() throws SQLException{
		this.is_transaction=false;
		if(this.connection!=null){
			this.connection.commit();
			this.close();
		}
	}
	
	/**
	 * 回滚（确保已开启事务）
	 */
	public void rollback(){
		this.is_transaction=false;
		if(this.connection!=null){
			try {
				this.connection.rollback();
				this.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 模型类的连贯操作方法之一，使用查询表达式可以支持更多的SQL查询语法
	 * @param where Map<String 数据库字段名,W 查询条件>
	 * @return 模型类<br>
	 * <br>
	 * 示例：<br>
	 * M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 * Map&lt;String, W&gt; w=new HashMap&lt;String, W&gt;();//实例化查询条件<br>
	 * w.put("id",new W("=",1));//设置查询条件<br>
	 * w.put("is_del",new W("=",0));//设置查询条件<br>
	 * List&lt;Map&lt;String, Object&gt;&gt; list=m.where(w).select();//将查询条件传入查询方法<br>
	 * 所生成的sql语句是：select * from javatodo where `id`=1 and `is_del`=0<br>
	 */
	public M where(Map<String, W> where) {
		this.db.where(where);
		return this;
	}

	/**
	 * 模型类的连贯操作方法之一，主要用于字符串条件直接查询和操作
	 * @param where_str 查询条件语句
	 * @return 模型类<br>
	 * <br>
	 * 示例：<br>
	 * M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 * List&lt;Map&lt;String, Object&gt;&gt; list=m.where("id=1 and is_del=0").select();//将查询条件传入查询方法<br>
	 * 所生成的sql语句是：select * from javatodo where `id`=1 and `is_del`=0<br>
	 */
	public M where(String where_str) {
		this.db.where(where_str);
		return this;
	}
	
	/**
	 * 模型类的连贯操作方法之一，主要用于字符串条件直接查询和操作
	 * @param where_str 查询条件语句
	 * @param params 相关参数
	 * @return 模型类<br>
	 * <br>
	 * 示例：<br>
	 * M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 * List&lt;Map&lt;String, Object&gt;&gt; list=m.where("id=? and is_del=?",1,0).select();//将查询条件传入查询方法<br>
	 */
	public M where(String where_str, Object... params) {
		this.db.where(where_str, params);
		return this;
	}

	/**
	 * 用于对操作结果进行排序
	 * @param order_str 排序方式
	 * @return 模型类<br>
	 * <br>
	 * 示例：<br>
	 * M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 * List&lt;Map&lt;String, Object&gt;&gt; list=m.where("is_del=0").order("id desc").select();//将查询条件传入查询方法<br>
	 * 所生成的sql语句是：select * from javatodo where `is_del`=0 order by id desc<br>
	 */
	public M order(String order_str) {
		this.db.order(order_str);
		return this;
	}
	
	/**
	 * 用于对结果条数进行限制
	 * @param limit_str 限制条数
	 * @return 模型类
	 */
	public M limit(String limit_str) {
		this.db.limit(limit_str);
		return this;
	}

	/**
	 * 设置要操作的数据对象的值，主要用于对数据库增加一条记录
	 * @param data
	 * @return 模型类<br>
	 * <br>
	 * 示例<br>
	 * M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 * Map&lt;String,Object&gt;d=new HashMap&lt;String,Object&gt;();<br>
	 * d.put("name","javatodo");<br>
	 * d.put("author","wangwenyuan");<br>
	 * m.data(d).add();<br>
	 */
	public M data(Map<String, Object> data) {
		this.db.data(data);
		return this;
	}

	/**
	 * 设置当前数据表的别名。
	 * @param as_str String 当前数据表的别名
	 * @return 模型类
	 */
	public M alias(String as_str) {
		this.db.alias(as_str);
		return this;
	}

	/**
	 * 根据两个或多个表中的列之间的关系，从这些表中查询数据(默认为inner连表查询)
	 * @param table_name 所要连表的表名
	 * @param on_sql 连表查询的条件
	 * @return 模型类<br>
	 * <br>
	 * 示例：<br>
	 * M m=new M("javatodo");<br>
	 * List&lt;Map&lt;String,Object&gt;&gt; list= m.alias("j").join("user","as u on j.uid=u.id").where("u.id=1").select();<br>
	 * 生成的sql:select * from javatodo as j inner join user as u on j.uid=u.id
	 */
	public M join(String table_name, String on_sql) {
		this.db.join(table_name, on_sql);
		return this;
	}

	/**
	 * 根据两个或多个表中的列之间的关系，从这些表中查询数据
	 * @param table_name String 所要连表的表名
	 * @param on_sql 连表查询的条件
	 * @param type 连表查询的方式
	 * @return 模型类<br>
	 * <br>
	 * 示例：<br>
	 * M m=new M("javatodo");<br>
	 * List&lt;Map&lt;String,Object&gt;&gt; list= m.alias("j").join("user","as u on j.uid=u.id","left").where("u.id=1").select();<br>
	 * 生成的sql:select * from javatodo as j left join user as u on j.uid=u.id
	 */
	public M join(String table_name, String on_sql, String type) {
		this.db.join(table_name, on_sql, type);
		return this;
	}

	/**
	 * 标识要返回或者操作的字段
	 * @param field_str 字段名
	 * @return 模型类
	 */
	public M field(String field_str) {
		this.db.field(field_str);
		return this;
	}
	
	/**
	 * 用于结合合计函数，根据一个或多个列对结果集进行分组
	 * @param field_str 需要分组的字段名
	 * @return 模型类
	 */
	public M group(String field_str){
		this.db.group(field_str);
		return this;
	}

	/**
	 * 将数据写入数据库
	 * @throws SQLException<br>
	 * <br>
	 * 示例：<br>
	 * M m=new M("javatodo");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 * Map&lt;String,Object&gt;d=new HashMap&lt;String,Object&gt;();<br>
	 * d.put("name","javatodo");<br>
	 * d.put("author","wangwenyuan");<br>
	 * m.data(d).add();<br>
	 */
	public void add() throws SQLException {
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			this.db.add();
			String sql = this.db.get_sql();
			List<Object> add_data_list = this.db.get_add_data();
			Object[] params = new Object[add_data_list.size()];
			for (Integer integer = 0; integer < add_data_list.size(); integer = integer + 1) {
				params[integer] = add_data_list.get(integer);
			}
			this.queryRunner.update(this.connection, sql, params);
			this.db.clear();
			this.lastSql=sql;
			this.sql_params=params;
			if(!this.is_transaction){
				this.close();
			}
		}
	}

	/**
	 * 获取数据表中的最后一个id
	 * @param table_name 表名称
	 * @return 最后一条数据的主键
	 * @throws SQLException
	 */
	public static Object getLastId(String table_name) throws SQLException{
		return new M(table_name).order("id desc").getField("id");
	}

	/**
	 * 修改数据库中的某条记录
	 * @param data Map<String, Object> 数据参数
	 * @throws SQLException<br>
	 * 示例<br>
	 * <br>
	 * M m=new M("web");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 * Map&lt;String,Object&gt;d=new HashMap&lt;String,Object&gt;();<br>
	 * d.put("name","javatodo");<br>
	 * d.put("url","javatodo.com");<br>
	 * m.where("id=1").save(d);
	 */
	public void save(Map<String, Object> data) throws SQLException {
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			this.db.save(data);
			String sql = this.db.get_sql();
			List<Object> update_data_list = this.db.get_update_data();
			List<Object> where_data_list = this.db.get_where_data();
			Integer all_total = update_data_list.size() + where_data_list.size();
			Object[] params = new Object[all_total];
			for (Integer integer = 0; integer < update_data_list.size(); integer = integer + 1) {
				params[integer] = update_data_list.get(integer);
			}
			for (Integer integer = update_data_list.size(); integer < all_total; integer = integer + 1) {
				params[integer] = where_data_list.get(integer - update_data_list.size());
			}
			this.queryRunner.update(this.connection, sql, params);
			this.db.clear();
			this.lastSql=sql;
			this.sql_params=params;
			if(!this.is_transaction){
				this.close();
			}
		}
	}

	/**
	 * 删除数据库中的某条记录
	 * @throws SQLException<br>
	 * 示例：<br>
	 * <br>
	 * M m=new M("web");//实例化M对象，并制定所要操作的数据表为javatodo;<br>
	 * m.where("id=15").delete();
	 */
	public void delete() throws SQLException {
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			this.db.delete();
			String sql = this.db.get_sql();
			List<Object> where_data_list = this.db.get_where_data();
			Object[] params = new Object[where_data_list.size()];
			for (Integer integer = 0; integer < where_data_list.size(); integer = integer + 1) {
				params[integer] = where_data_list.get(integer);
			}
			this.queryRunner.update(this.connection, sql, params);
			this.db.clear();
			this.lastSql=sql;
			this.sql_params=params;
			if(!this.is_transaction){
				this.close();
			}
		}
	}

	/**
	 * 获取数据表中的多行记录
	 * @return List<Map<String 字段名, Object 值>> 一个list对象，其中每一条记录是一个以map形式的数据。
	 * @throws SQLException
	 */
	public List<Map<String, Object>> select() throws SQLException {
		List<Map<String, Object>> list = new ArrayList<>();
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			this.db.select();
			String sql = this.db.get_sql();
			List<Object> where_data_list = this.db.get_where_data();
			Object[] params = new Object[where_data_list.size()];
			for (Integer integer = 0; integer < where_data_list.size(); integer = integer + 1) {
				params[integer] = where_data_list.get(integer);
			}
			
			list = queryRunner.query(this.connection, sql, new MapListHandler(), params);
			this.db.clear();
			this.lastSql=sql;
			this.sql_params=params;
			if(!this.is_transaction){
				this.close();
			}
			return list;
		}else{
			return list;
		}
	}

	/**
	 * 获取数据表中的一行记录
	 * @return Map<String 字段名, Object 值> 一个以map形式的数据记录
	 * @throws SQLException
	 */
	public Map<String, Object> find() throws SQLException {
		Map<String, Object> map = null;
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			map=new HashMap<>();
			this.db.find();
			String sql = this.db.get_sql();
			List<Object> where_data_list = this.db.get_where_data();
			Object[] params = new Object[where_data_list.size()];
			for (Integer integer = 0; integer < where_data_list.size(); integer = integer + 1) {
				params[integer] = where_data_list.get(integer);
			}
			map = this.queryRunner.query(this.connection, sql, new MapHandler(), params);
			this.db.clear();
			this.lastSql=sql;
			this.sql_params=params;
			if(!this.is_transaction){
				this.close();
			}
			return map;
		}else{
			return map;
		}
	}
	
	/**
	 * 读取字段值,就是获取数据表中的单个数据
	 * @param field_name
	 * @return Object 当前记录中field_name字段对应的值
	 * @throws SQLException
	 */
	public Object getField(String field_name) throws SQLException{
		Object object=null;
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			object=new Object();
			this.db.field(field_name);
			Map<String, Object>map=this.find();
			if(map == null) {
				return null;
			}
			if(map.containsKey(field_name)){
				object=map.get(field_name);
			}else{
				object=null;
			}
			if(!this.is_transaction){
				this.close();
			}
			return object;
		}else{
			return object;
		}
	}
	
	/**
	 * 用于执行SQL查询操作,主要用于“查询”操作，“增、删、改”操作请用execute()
	 * @param sql String sql语句
	 * @return List<Map<String 字段名, Object 值>> 一个list对象，其中每一条记录是一个以map形式的数据。
	 * @throws SQLException
	 */
	public List<Map<String, Object>>query(String sql) throws SQLException{
		List<Map<String, Object>> list = new ArrayList<>();
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			list = queryRunner.query(this.connection, sql, new MapListHandler());
			this.lastSql=sql;
			this.sql_params="";
			if(!this.is_transaction){
				this.close();
			}
			return list;
		}else{
			return list;
		}
	}
	
	/**
	 * 用于执行SQL查询操作,主要用于“查询”操作，“增、删、改”操作请用execute()
	 * @param sql String sql语句
	 * @param params 参数值
	 * @return List<Map<String 字段名, Object 值>> 一个list对象，其中每一条记录是一个以map形式的数据。
	 * @throws SQLException
	 */
	public List<Map<String, Object>>query(String sql,Object... params) throws SQLException{
		List<Map<String, Object>> list = new ArrayList<>();
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			list = queryRunner.query(this.connection, sql, new MapListHandler(),params);
			this.lastSql=sql;
			this.sql_params=params;
			if(!this.is_transaction){
				this.close();
			}
			return list;
		}else{
			return list;
		}
	}

	/**
	 * 获取记录的总条数
	 * @return Integer 记录的总条数
	 * @throws SQLException
	 */
	public Integer count() throws SQLException{
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			Object count=this.getField("count(*)");
			if(!this.is_transaction){
				this.close();
			}
			if(count==null){
				return null;
			}else{
				return Integer.parseInt(count.toString());
			}
		}else{
			return null;
		}
	}
	
	/**
	 * 用于更新和写入数据的sql操作,主要用于“增、删、改”的操作，查询操作请用query()
	 * @param sql String sql语句
	 * @throws SQLException
	 */
	public void execute(String sql) throws SQLException {
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			this.queryRunner.update(this.connection, sql);
			this.lastSql=sql;
			this.sql_params="";
			if(!this.is_transaction){
				this.close();
			}
		}
	}
	
	/**
	 * 用于更新和写入数据的sql操作,主要用于“增、删、改”的操作，查询操作请用query()
	 * @param sql String sql语句
	 * @param params 参数值
	 * @throws SQLException
	 */
	public void execute(String sql,Object... params) throws SQLException {
		if(this.connection==null){
			this.connection=MC.get_connection();
		}
		if(this.connection!=null){
			this.queryRunner.update(this.connection, sql,params);
			this.lastSql=sql;
			this.sql_params=params;
			if(!this.is_transaction){
				this.close();
			}
		}
	}
	
	/**
	 * 输出上次执行的sql语句
	 * @return String 上次执行的sql语句
	 */
	public void getLastSql(){
		System.out.println(this.lastSql);
		if(this.sql_params.getClass().getName().contains("String")){
			System.out.println(this.sql_params);
		}else{
			Object[] objects=(Object[])this.sql_params;
			List<String>list=new ArrayList<>();
			for(Integer i=0; i<objects.length; i=i+1){
				list.add(objects[i].toString());
			}
			System.out.println(list);
		}
	}
	
	/**
	 * 关闭数据库连接
	 */
	private void close(){
		if(this.connection!=null){
			try {
				this.connection.close();
				this.connection=null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
