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
package com.javatodo.core.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.javatodo.config.C;

public class Upload {
	public class FileInfo {
		public String key = null;// 附件上传的表单名称
		public String savePath = null;// 上传文件的保存路径
		public String name = null;// 上传文件的原始名称
		public String savename = null;// 上传文件的保存名称
		public long size = 0;// 上传文件的大小
		public String type = null;// 上传文件的MIME类型
		public String ext = null;// 上传文件的后缀类型
		public String local_file_path = null;// 文件上传后本地的存储路径

		public String toString() {
			String string = "";
			string = string + "key:" + this.key + "\n";
			string = string + "savePath:" + this.savePath + "\n";
			string = string + "name:" + this.name + "\n";
			string = string + "savename:" + this.savename + "\n";
			string = string + "size:" + this.size + "\n";
			string = string + "type:" + this.type + "\n";
			string = string + "ext:" + this.ext + "\n";
			return string;
		}
	}

	public String err_msg = "";
	public long maxSize = C.UploadMaxSize;
	public List<String> extList = new ArrayList<>();
	public String savePath = "uploads";
	public String cachePath = "uploadFiles";

	private HttpServlet servlet;
	private HttpServletRequest request;

	/**
	 * 实例化上传类
	 * 
	 * @param servlet
	 * @param request
	 */
	public Upload(HttpServlet servlet, HttpServletRequest request) {
		this.servlet = servlet;
		this.request = request;
	}

	/**
	 * 上传单个文件
	 * 
	 * @param fieldName
	 *            表单中的上传字段名
	 * @return FileInfo对象，其中存储着上传以后的信息
	 * @throws Exception
	 */
	public FileInfo uploadOne(String fieldName) throws Exception {
		FileInfo fileInfo = null;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(4096);
		factory.setRepository(new File(servlet.getServletContext().getRealPath("/") + cachePath));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(this.maxSize);
		List<FileItem> file_list = upload.parseRequest(request);
		if (file_list != null) {
			Iterator<FileItem> iterator = file_list.iterator();
			while (iterator.hasNext()) {
				FileItem file_item = null;
				String path = null;
				long size = 0;
				file_item = (FileItem) iterator.next();
				if (file_item == null || file_item.isFormField()) {
					continue;
				}
				if (!(file_item.getFieldName().equals(fieldName))) {
					continue;
				}
				path = file_item.getName();
				size = file_item.getSize();
				if ("".equals(path) || size == 0) {
					this.err_msg = "上传的文件为空";
				} else {
					String t_name = path.substring(path.lastIndexOf("\\") + 1);
					String t_ext = t_name.substring(t_name.lastIndexOf(".") + 1);
					int allowFlag = 0;
					int allowExtCount = extList.size();
					for (; allowFlag < allowExtCount; allowFlag++) {
						if (extList.get(allowFlag).equals(t_ext)) {
							break;
						}
					}
					if (allowFlag == allowExtCount) {
						this.err_msg = "该类型文件不允许上传";
					} else {
						long now = System.currentTimeMillis();
						String prefix = String.valueOf(now);
						String file_name = prefix + "." + t_ext;
						String saveDir = servlet.getServletContext().getRealPath("/") + savePath + "/" + T.now("yyyy/MM/dd") + "/";
						File dir = new File(saveDir);
						if (!dir.exists()) {
							dir.mkdirs();
						}
						file_item.write(new File(saveDir + file_name));
						// 设置文件信息
						fileInfo = new FileInfo();
						fileInfo.key = file_item.getFieldName();
						fileInfo.ext = t_ext;
						fileInfo.name = file_item.getName();
						fileInfo.savename = file_name;
						fileInfo.savePath = savePath + "/" + T.now("yyyy/MM/dd") + "/";
						fileInfo.size = file_item.getSize();
						fileInfo.type = file_item.getContentType();
						fileInfo.local_file_path = saveDir + file_name;
					}
				}
			}
		}
		return fileInfo;
	}

	/**
	 * 上传多个文件
	 * 
	 * @return Map<String 表单中的上传字段名, FileInfo FileInfo对象，其中存储着上传以后的信息>
	 * @throws Exception
	 */
	public Map<String, FileInfo> upload() throws Exception {
		Map<String, FileInfo> fileInfoMap = null;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(4096);
		factory.setRepository(new File(servlet.getServletContext().getRealPath("/") + cachePath));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(this.maxSize);
		List<FileItem> file_list = upload.parseRequest(request);
		if (file_list != null) {
			Iterator<FileItem> iterator = file_list.iterator();
			while (iterator.hasNext()) {
				FileItem file_item = null;
				String path = null;
				long size = 0;
				file_item = (FileItem) iterator.next();
				if (file_item == null || file_item.isFormField()) {
					continue;
				}
				path = file_item.getName();
				size = file_item.getSize();
				if ("".equals(path) || size == 0) {
					continue;
				}
				String t_name = path.substring(path.lastIndexOf("\\") + 1);
				String t_ext = t_name.substring(t_name.lastIndexOf(".") + 1);
				int allowFlag = 0;
				int allowExtCount = extList.size();
				for (; allowFlag < allowExtCount; allowFlag++) {
					if (extList.get(allowFlag).equals(t_ext)) {
						break;
					}
				}
				if (allowFlag == allowExtCount) {
					continue;
				} else {
					long now = System.currentTimeMillis();
					String prefix = String.valueOf(now);
					String file_name = prefix + "." + t_ext;
					String saveDir = servlet.getServletContext().getRealPath("/") + savePath + "/" + T.now("yyyy/MM/dd") + "/";
					File dir = new File(saveDir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					file_item.write(new File(saveDir + file_name));
					if (fileInfoMap == null) {
						fileInfoMap = new HashMap<>();
					}
					FileInfo fileInfo = new FileInfo();
					fileInfo.key = file_item.getFieldName();
					fileInfo.ext = t_ext;
					fileInfo.name = file_item.getName();
					fileInfo.savename = file_name;
					fileInfo.savePath = savePath + "/" + T.now("yyyy/MM/dd") + "/";
					fileInfo.size = file_item.getSize();
					fileInfo.type = file_item.getContentType();
					fileInfo.local_file_path = saveDir + file_name;
					fileInfoMap.put(fileInfo.key, fileInfo);
				}
			}
		}
		return fileInfoMap;
	}
}
