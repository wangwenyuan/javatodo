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
	public static String[] dbType = C.dbType;// 数据库类型
	public static List<String> tablePrefix = new ArrayList<String>(Arrays.asList(C.tablePrefix));// 数据表前缀
	public static List<String> dbHost = new ArrayList<String>(Arrays.asList(C.dbHost));// 数据库地址
	public static List<String> dbPort = new ArrayList<String>(Arrays.asList(C.dbPort));// 数据库端口
	public static List<String> dbName = new ArrayList<String>(Arrays.asList(C.dbName));// 数据库名称
	public static List<String> dbUsername = new ArrayList<String>(Arrays.asList(C.dbUserName));// 数据库用户名
	public static List<String> dbPassword = new ArrayList<String>(Arrays.asList(C.dbPassword));// 数据库密码

	static {
		if (MC.dataSource == null && MC.dbHost.size() > 0) {
			try {
				MC.dataSource = new ArrayList<ComboPooledDataSource>();
				for (Integer i = 0; i < MC.dbHost.size(); i = i + 1) {
					ComboPooledDataSource source = new ComboPooledDataSource();
					if (MC.dbType[i].equals("mysql")) {
						source.setDriverClass("com.mysql.jdbc.Driver");
					}
					if (MC.dbType[i].equals("postgresql")) {
						source.setDriverClass("org.postgresql.Driver");
					}
					source.setMaxPoolSize(C.MaxPoolSize);
					source.setMinPoolSize(C.MinPoolSize);
					source.setInitialPoolSize(C.InitialPoolSize);
					source.setMaxIdleTime(C.MaxIdleTime);
					source.setAcquireIncrement(C.AcquireIncrement);
					String port = "";
					if (MC.dbPort.size() - 1 < i) {
						port = "3306";
					} else {
						port = MC.dbPort.get(i);
					}
					source.setJdbcUrl("jdbc:" + MC.dbType + "://" + MC.dbHost.get(i) + ":" + port + "/"
							+ MC.dbName.get(i) + "?zeroDateTimeBehavior=convertToNull");
					source.setUser(MC.dbUsername.get(i));
					source.setPassword(MC.dbPassword.get(i));
					MC.dataSource.add(source);
				}

			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				T.javatodo_error_log(e);
			}
		}
	}

	public static Connection getConnection() {
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

	public static Connection getConnection(Integer dbIndex) {
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
