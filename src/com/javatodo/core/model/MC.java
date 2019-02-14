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

import com.javatodo.config.C;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MC {
	
	public static ComboPooledDataSource dataSource = null;
	public static String db_type = C.db_type;// 数据库类型
	public static String table_pre = C.table_pre;// 数据表前缀
	public static String db_host = C.db_host;// 数据库地址
	public static String db_port = C.db_port;// 数据库端口
	public static String db_name = C.db_name;// 数据库名称
	public static String db_username = C.db_username;// 数据库用户名
	public static String db_password = C.db_password;// 数据库密码
	
	static {
		if(MC.dataSource==null && !MC.db_host.trim().equals("")){
			try {
				MC.dataSource=new ComboPooledDataSource();
				if(MC.db_type.equals("mysql")){
					dataSource.setDriverClass("com.mysql.jdbc.Driver");
				}
				if(MC.db_type.equals("postgresql")){
					dataSource.setDriverClass("org.postgresql.Driver");
				}
				dataSource.setMaxPoolSize(C.MaxPoolSize);
				dataSource.setMinPoolSize(C.MinPoolSize);
				dataSource.setInitialPoolSize(C.InitialPoolSize);
				dataSource.setMaxIdleTime(C.MaxIdleTime);
				dataSource.setAcquireIncrement(C.AcquireIncrement);
				dataSource.setJdbcUrl("jdbc:" + MC.db_type + "://" + MC.db_host + ":" + MC.db_port + "/" + MC.db_name+"??useUnicode=true&characterEncoding="+C.default_encoding+"&zeroDateTimeBehavior=convertToNull");
				dataSource.setUser(MC.db_username);
				dataSource.setPassword(MC.db_password);
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Connection get_connection() {
		Connection connection = null;
		if (MC.dataSource == null) {
			System.out.println("数据源不存在");
		} else {
			try {
				connection = MC.dataSource.getConnection();
			} catch (SQLException e) {
			}
		}
		return connection;
	}
}
