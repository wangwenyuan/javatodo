package com.javatodo.core.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.javatodo.core.tools.Http;
import com.javatodo.core.tools.T;

public class ElasticSearch {
	private String ip = "";
	private Integer port = 9200;

	private String log = "";

	public ElasticSearch(String ip) {
		this.ip = ip;
	}

	public ElasticSearch(String ip, Integer port) {
		this.ip = ip;
		this.port = port;
	}

	public String getLog() {
		return log;
	}

	/**
	 * 新增或修改数据
	 * 
	 * @param _index     String
	 *                   ElasticSearch中的索引相当于数据表（版本6之前相当于数据库，版本6之后相当于数据表）版本6之后一个索引只能有一个类型，因此去除类型参数，默认类型与索引名称相同
	 * @param _id        String 标识某个数据库、某个数据表中的唯一数据ID
	 * @param jsonString String 提交的json数据
	 */
	public Boolean save(String _index, String _id, String jsonString) {
		try {
			URL httpurl = new URL(ip + ":" + port + "/" + _index + "/" + _index + "/" + _id);
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			String data = jsonString;
			conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			Integer code = conn.getResponseCode();
			if (code == 201 || code == 200) {
				InputStream inputStream = conn.getInputStream();
				this.log = this.stremToString(inputStream, "UTF-8");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 新增或修改数据
	 * 
	 * @param _index String
	 *               ElasticSearch中的索引相当于数据表（版本6之前相当于数据库，版本6之后相当于数据表）版本6之后一个索引只能有一个类型，因此去除类型参数，默认类型与索引名称相同
	 * @param _id    String 标识某个数据库、某个数据表中的唯一数据ID
	 * @param map    Map<String, Object> 提交的数据
	 */
	public Boolean save(String _index, String _id, Map<String, Object> map) {
		String jsonString = JSON.toJSONString(map);
		return save(_index, _id, jsonString);
	}

	/**
	 * 删除数据
	 * 
	 * @param _index String
	 *               ElasticSearch中的索引相当于数据表（版本6之前相当于数据库，版本6之后相当于数据表）版本6之后一个索引只能有一个类型，因此去除类型参数，默认类型与索引名称相同
	 * @param _id    String 标识某个数据库、某个数据表中的唯一数据ID
	 */
	public Boolean delete(String _index, String _id) {
		try {
			URL httpurl = new URL(ip + ":" + port + "/" + _index + "/" + _index + "/" + _id);
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("DELETE");
			conn.setReadTimeout(5000);
			conn.setDoOutput(true);
			conn.getOutputStream();
			Integer code = conn.getResponseCode();
			if (code == 201 || code == 200) {
				InputStream inputStream = conn.getInputStream();
				this.log = this.stremToString(inputStream, "UTF-8");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 删除索引
	 * 
	 * @param _index String
	 *               ElasticSearch中的索引相当于数据表（版本6之前相当于数据库，版本6之后相当于数据表）版本6之后一个索引只能有一个类型，因此去除类型参数，默认类型与索引名称相同
	 * @param _id    String 标识某个数据库、某个数据表中的唯一数据ID
	 */
	public Boolean deleteALL(String _index) {
		try {
			URL httpurl = new URL(ip + ":" + port + "/" + _index);
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("DELETE");
			conn.setReadTimeout(5000);
			conn.setDoOutput(true);
			conn.getOutputStream();
			Integer code = conn.getResponseCode();
			if (code == 201 || code == 200) {
				InputStream inputStream = conn.getInputStream();
				this.log = this.stremToString(inputStream, "UTF-8");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public JSONObject sqlQuery(String sql) {
		try {
			URL httpurl = new URL(ip + ":" + port + "/_xpack/sql?format=json");
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			JSONObject object = new JSONObject();
			object.put("query", sql);
			String data = object.toJSONString();
			conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			Integer code = conn.getResponseCode();
			if (code == 200) {
				InputStream inputStream = conn.getInputStream();
				String jsonString = this.stremToString(inputStream, "UTF-8");
				return JSONObject.parseObject(jsonString);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String query(String _index, String jsonString) {
		try {
			URL httpurl = new URL(ip + ":" + port + "/" + _index + "/" + _index + "/_search");
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			String data = jsonString;
			conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			Integer code = conn.getResponseCode();
			if (code == 200) {
				InputStream inputStream = conn.getInputStream();
				return this.stremToString(inputStream, "UTF-8");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONObject find(String _index, String _id) {
		Http http = new Http();
		String ret = http.get(ip + ":" + port + "/" + _index + "/" + _index + "/" + _id);
		if (ret == null) {
			return null;
		} else {
			return JSONObject.parseObject(ret);
		}
	}

	public Object getField(String field) {
		JSONObject object = this.field(field).find();
		if (object == null) {
			return null;
		}
		return object.get(field);
	}

	public long count() {
		JSONObject object = this.field("count(*)").find();
		if (object.getLong("count(*)") > 1000) {
			return 1000;// ElasticSearch 通过sql查询获取到的数据最大1000条
		} else {
			return object.getLong("count(*)");
		}
	}

	private String stremToString(InputStream is, String encoding) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString(encoding);
	}

	// sql链式表查询
	private String sql = "";
	private String table_name = "";
	private String where_str = "";
	private String order_str = "";
	private String field_str = " * ";
	private String limit_str = "";
	private String group_str = "";

	private int elasticsearch_size = 0;
	private int elasticsearch_row_num = 0;

	public ElasticSearch table(String table_name) {
		this.table_name = table_name;
		return this;
	}

	// where方法
	public ElasticSearch where(Map<String, W> where) {
		for (String key : where.keySet()) {
			String temp_where_string = this.where_str;
			if (this.where_str.trim().equals("")) {
				this.where_str = " where ";
			} else {
				this.where_str = this.where_str + " and ";
			}
			String string1 = "";
			String string2 = "";
			switch (where.get(key).get_relation().toLowerCase().trim()) {
			case "eq":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " = '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " = " + where.get(key).get_value();
				}
				break;
			case "=":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " = '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " = " + where.get(key).get_value();
				}
				break;
			case "!=":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " != '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " != " + where.get(key).get_value();
				}
				break;
			case "<>":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " != '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " != " + where.get(key).get_value();
				}
				break;
			case "neq":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " != '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " != " + where.get(key).get_value();
				}
				break;
			case ">":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " > '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " > " + where.get(key).get_value();
				}
				break;
			case "gt":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " > '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " > " + where.get(key).get_value();
				}
				break;
			case ">=":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " >= '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " >= " + where.get(key).get_value();
				}
				break;
			case "egt":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " >= '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " >= " + where.get(key).get_value();
				}
				break;
			case "lt":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " < '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " < " + where.get(key).get_value();
				}
				break;
			case "elt":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " <= '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " <= " + where.get(key).get_value();
				}
				break;
			case "<":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " < '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " < " + where.get(key).get_value();
				}
				break;
			case "<=":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " <= '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " <= " + where.get(key).get_value();
				}
				break;
			case "like":
				if (where.get(key).get_value() instanceof String) {
					this.where_str = this.where_str + key + " like '" + where.get(key).get_value() + "'";
				} else {
					this.where_str = this.where_str + key + " like " + where.get(key).get_value();
				}
				break;
			case "between":
				if (where.get(key).get_value_list().get(0) instanceof String) {
					string1 = "'" + where.get(key).get_value_list().get(0) + "'";
				} else {
					string1 = where.get(key).get_value_list().get(0) + "";
				}
				if (where.get(key).get_value_list().get(1) instanceof String) {
					string2 = "'" + where.get(key).get_value_list().get(1) + "'";
				} else {
					string2 = where.get(key).get_value_list().get(1) + "";
				}
				this.where_str = this.where_str + key + " between " + string1 + " and " + string2;
				break;
			case "not between":
				if (where.get(key).get_value_list().get(0) instanceof String) {
					string1 = "'" + where.get(key).get_value_list().get(0) + "'";
				} else {
					string1 = where.get(key).get_value_list().get(0) + "";
				}
				if (where.get(key).get_value_list().get(1) instanceof String) {
					string2 = "'" + where.get(key).get_value_list().get(1) + "'";
				} else {
					string2 = where.get(key).get_value_list().get(1) + "";
				}
				this.where_str = this.where_str + key + " not between " + string1 + " and " + string2;
				break;
			case "in":
				if (where.get(key).get_value_list().size() > 1) {
					String wenhao_str = "";
					if (where.get(key).get_value_list().get(0) instanceof String) {
						wenhao_str = "'" + where.get(key).get_value_list().get(0) + "'";
					} else {
						wenhao_str = "" + where.get(key).get_value_list().get(0);
					}
					for (Integer integer = 1; integer < where.get(key).get_value_list().size(); integer = integer + 1) {
						if (where.get(key).get_value_list().get(integer) instanceof String) {
							wenhao_str = wenhao_str + ",'" + where.get(key).get_value_list().get(integer) + "'";
						} else {
							wenhao_str = wenhao_str + "," + where.get(key).get_value_list().get(integer);
						}
					}
					this.where_str = this.where_str + key + " in (" + wenhao_str + ") ";
				} else if (where.get(key).get_value_list().size() == 1) {
					if (where.get(key).get_value_list().get(0) instanceof String) {
						this.where_str = this.where_str + key + "= '" + where.get(key).get_value_list().get(0) + "'";
					} else {
						this.where_str = this.where_str + key + "= " + where.get(key).get_value_list().get(0);
					}
				}
				break;
			case "not in":
				if (where.get(key).get_value_list().size() > 1) {
					String wenhao_str = "";
					if (where.get(key).get_value_list().get(0) instanceof String) {
						wenhao_str = "'" + where.get(key).get_value_list().get(0) + "'";
					} else {
						wenhao_str = "" + where.get(key).get_value_list().get(0);
					}
					for (Integer integer = 1; integer < where.get(key).get_value_list().size(); integer = integer + 1) {
						if (where.get(key).get_value_list().get(integer) instanceof String) {
							wenhao_str = wenhao_str + ",'" + where.get(key).get_value_list().get(integer) + "'";
						} else {
							wenhao_str = wenhao_str + "," + where.get(key).get_value_list().get(integer);
						}
					}
					this.where_str = this.where_str + key + " not in (" + wenhao_str + ") ";
				} else if (where.get(key).get_value_list().size() == 1) {
					if (where.get(key).get_value_list().get(0) instanceof String) {
						this.where_str = this.where_str + key + "!= '" + where.get(key).get_value_list().get(0) + "'";
					} else {
						this.where_str = this.where_str + key + "!= " + where.get(key).get_value_list().get(0);
					}
				}
				break;
			default:
				this.where_str = temp_where_string;
				break;
			}
		}
		return this;
	}

	// where方法
	public ElasticSearch where(String where_str) {
		if (this.where_str.equals("")) {
			this.where_str = " where " + where_str;
		} else {
			this.where_str = this.where_str + " and " + where_str + " ";
		}
		return this;
	}

	// order方法
	public ElasticSearch order(String order_str) {
		this.order_str = " order by " + order_str;
		return this;
	}

	// limit方法
	public ElasticSearch limit(String limit_str) {
		limit_str = limit_str.trim();
		String[] arr = limit_str.split(",");
		if (arr.length == 0) {
			this.limit_str = "";
			return this;
		} else if (arr.length == 1) {
			this.limit_str = " limit " + limit_str + " ";
			this.elasticsearch_size = T.toInt(limit_str.trim());
			this.elasticsearch_row_num = this.elasticsearch_size;
		} else if (arr.length == 2) {
			this.elasticsearch_size = T.toInt(arr[0].trim()) + T.toInt(arr[1].trim());
			this.elasticsearch_row_num = T.toInt(arr[1].trim());
			this.limit_str = " limit " + this.elasticsearch_size + " ";
		}
		return this;
	}

	// filed方法
	public ElasticSearch field(String field_str) {
		this.field_str = " " + field_str + " ";
		return this;
	}

	// select方法
	public JSONArray select() {
		this.sql = "select " + this.field_str + " from " + this.table_name + this.where_str + this.group_str + this.order_str + this.limit_str;
		String _sql = this.sql;
		JSONObject ret = this.sqlQuery(_sql);
		JSONArray columns = ret.getJSONArray("columns");
		JSONArray rows = ret.getJSONArray("rows");
		Integer rows_count = rows.size();
		JSONArray list = new JSONArray();
		if (this.limit_str.trim().equals("")) {
			for (Integer i = 0; i < rows_count; i = i + 1) {
				JSONObject object = new JSONObject();
				for (Integer n = 0; n < columns.size(); n = n + 1) {
					object.put(columns.getJSONObject(n).getString("name"), rows.getJSONArray(i).getString(n));
				}
				list.add(object);
			}
		} else {
			if (this.elasticsearch_size - this.elasticsearch_row_num > -1) {
				for (Integer i = this.elasticsearch_size - this.elasticsearch_row_num; i < this.elasticsearch_size; i = i + 1) {
					if (i < rows_count) {
						JSONObject object = new JSONObject();
						for (Integer n = 0; n < columns.size(); n = n + 1) {
							object.put(columns.getJSONObject(n).getString("name"), rows.getJSONArray(i).getString(n));
						}
						list.add(object);
					} else {
						break;
					}
				}
			}
		}

		this.clear();
		return list;
	}

	// find方法
	public JSONObject find() {
		this.sql = "select " + this.field_str + " from " + this.table_name + this.where_str + this.group_str + this.order_str + " limit 1";
		String _sql = this.sql;
		this.clear();
		JSONObject ret = this.sqlQuery(_sql);
		JSONArray columns = ret.getJSONArray("columns");
		JSONArray rows = ret.getJSONArray("rows");
		if (rows.size() == 0) {
			return null;
		}
		JSONArray row = rows.getJSONArray(0);
		JSONObject object = new JSONObject();
		for (Integer i = 0; i < columns.size(); i = i + 1) {
			object.put(columns.getJSONObject(i).getString("name"), row.get(i));
		}
		return object;
	}

	// 清理数据
	public void clear() {
		this.sql = "";
		this.table_name = "";
		this.where_str = "";
		this.order_str = "";
		this.field_str = " * ";
		this.limit_str = "";
		this.group_str = "";
		this.elasticsearch_size = 0;
		this.elasticsearch_row_num = 0;
	}
}
