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

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.javatodo.config.C;
import com.javatodo.core.tools.T;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MC {

	public static List<ComboPooledDataSource> dataSource = null;
	public static String db_type = C.db_type;// 数据库类型
	public static List<String> table_pre = new ArrayList<String>(Arrays.asList(C.table_pre));// 数据表前缀
	public static List<String> db_host = new ArrayList<String>(Arrays.asList(C.db_host));// 数据库地址
	public static List<String> db_port = new ArrayList<String>(Arrays.asList(C.db_port));// 数据库端口
	public static List<String> db_name = new ArrayList<String>(Arrays.asList(C.db_name));// 数据库名称
	public static List<String> db_username = new ArrayList<String>(Arrays.asList(C.db_username));// 数据库用户名
	public static List<String> db_password = new ArrayList<String>(Arrays.asList(C.db_password));// 数据库密码

	static {
		if (MC.dataSource == null && MC.db_host.size() > 0) {
			try {
				MC.dataSource = new ArrayList<ComboPooledDataSource>();
				for (Integer i = 0; i < MC.db_host.size(); i = i + 1) {
					ComboPooledDataSource source = new ComboPooledDataSource();
					if (MC.db_type.equals("mysql")) {
						source.setDriverClass("com.mysql.jdbc.Driver");
					}
					if (MC.db_type.equals("postgresql")) {
						source.setDriverClass("org.postgresql.Driver");
					}
					source.setMaxPoolSize(C.MaxPoolSize);
					source.setMinPoolSize(C.MinPoolSize);
					source.setInitialPoolSize(C.InitialPoolSize);
					source.setMaxIdleTime(C.MaxIdleTime);
					source.setAcquireIncrement(C.AcquireIncrement);
					String port = "";
					if (MC.db_port.size() - 1 < i) {
						port = "3306";
					} else {
						port = MC.db_port.get(i);
					}
					source.setJdbcUrl("jdbc:" + MC.db_type + "://" + MC.db_host.get(i) + ":" + port + "/" + MC.db_name.get(i) + "??useUnicode=true&characterEncoding=" + C.default_encoding + "&zeroDateTimeBehavior=convertToNull");
					source.setUser(MC.db_username.get(i));
					source.setPassword(MC.db_password.get(i));
					MC.dataSource.add(source);
				}

			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				T.javatodo_error_log(e);
			}
		}
	}

	public static Connection get_connection() {
		Connection connection = null;
		if (MC.dataSource == null) {
			System.out.println("数据源不存在");
		} else {
			try {
				connection = MC.dataSource.get(0).getConnection();
				T.javatodo_sql_log(connection, "connect创建");
			} catch (SQLException e) {
				T.javatodo_error_log(e);
			}
		}
		return connection;
	}

	public static Connection get_connection(Integer dbIndex) {
		Connection connection = null;
		if (MC.dataSource == null) {
			System.out.println("数据源不存在");
		} else {
			try {
				if (MC.dataSource.size() < dbIndex) {
					System.out.println("不存在该数据库链接");
				} else {
					connection = MC.dataSource.get(dbIndex).getConnection();
					T.javatodo_sql_log(connection, "connect创建");
				}
			} catch (SQLException e) {
				T.javatodo_error_log(e);
			}
		}
		return connection;
	}
}
