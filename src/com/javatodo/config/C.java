
package com.javatodo.config;

import com.javatodo.core.router.RC;

public class C {
	// 数据库配置项
	public static String db_type = "mysql";// 数据库类型
	// 可以同时连接多个数据库。
	public static String[] db_host = {};// 数据库地址
	public static String[] db_port = {};// 数据库端口
	public static String[] db_name = {};// 数据库名称
	public static String[] db_username = {};// 数据库用户名
	public static String[] db_password = {};// 数据库密码
	public static String[] table_pre = {};// 数据表前缀

	// 是否调试模式
	public static boolean is_debug = true;
	// 日志文件夹
	public static String log_file_path = "";
	//缓存文件夹
	public static String cache_path = "";

	// 设置使用的模版引擎：velocity、jsp、freemaker
	public static String template_engines = "jsp";
	// 设置编码
	public static String default_encoding = "utf-8";
	// 默认路由配置项
	public static String default_module = "index";
	public static String default_controller = "Index";
	public static String default_action = "index";

	// 路由
	public static void set_router() {
		new RC("com.javatodo.app.index", "index");
	}

	/** 以下配置项如无特殊需要请不要更改 **/

	// 默认模版文件目录
	public static String default_template_path = "WEB-INF/Template";
	public static String default_template_public = "Public";
	// 数据库连接池C3P0配置信息
	public static int MaxPoolSize = 100;
	public static int MinPoolSize = 10;
	public static int InitialPoolSize = 10;
	public static int MaxIdleTime = 20;
	public static int AcquireIncrement = 2;
	// 验证码图片配置信息
	public static int CaptchaWidth = 280;// 验证码默认图片宽度
	public static int CaptchaHeight = 90;// 验证码默认图片高度
	public static int CaptchaCodeNum = 5;// 验证码默认字符数
	public static int CaptchaLineNum = 250;// 验证码默认干扰线条数
	public static String CaptchaFont = "宋体";// 验证码默认字体
	// 文件上传配置
	public static long UploadMaxSize = 1024 * 1024 * 5;// 默认文件上传最大限制为5M
}
