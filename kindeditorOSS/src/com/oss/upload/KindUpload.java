package com.oss.upload;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.oss.service.OSSClientFactory;
import com.oss.util.OSSProperties;

/**
 * KindEditor用上传文件
 *
 */
public class KindUpload {
	public HttpServletRequest request;
	public HttpServletResponse response;
	
	public KindUpload(){}
	
	public KindUpload(HttpServletRequest request,HttpServletResponse response){
		this.request=request;
		this.response=response;
	}
	
	/**
	 * 默认前缀无
	 * @return
	 */
	public String uploadFile(){
		return uploadFile("");
	}
	
	/**
	 * 设置前缀
	 * @param prefix 例 "file/"
	 * @return
	 */
	public String uploadFile(String prefix){
		if(prefix==null){
			prefix="";
		}
		if(OSSProperties.ossStatus){
			return uploadOSSFile(prefix);
		}else{
			return localUpload(prefix);
		}
	}
	
	/**
	 * 文件保存到oss
	 * @param prefix 前缀
	 * @return
	 */
	public String uploadOSSFile(String prefix){

		//定义允许上传的文件扩展名
		HashMap<String, String> extMap = new HashMap<String, String>();
		extMap.put("image", "gif,jpg,jpeg,png,bmp");
		extMap.put("flash", "swf,flv");
		extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
		extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");

		//最大文件大小 10M
		long maxSize =10*(1024*1024);

		if(!ServletFileUpload.isMultipartContent(request)){
			return getError("请选择文件。");
		}

		String dirName = request.getParameter("dir");
		if (dirName == null) {
			dirName = "image";
		}


		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		
		List items=null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e1) {
			e1.printStackTrace();
		}
		
		Iterator itr = items.iterator();
		
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			String fileName = item.getName();
			
			//判断不是普通表单元素
			if (!item.isFormField()) {
				//检查文件大小
				if(item.getSize() > maxSize){
					return getError("上传文件大小超过限制。");
				}
				
				//检查扩展名
				String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
				if(!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
					return getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。");
				}

				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
				String urlAddr="";
				try{
					urlAddr=this.ossUpload(newFileName, item.getSize() ,item.getInputStream() , prefix);
				}catch(Exception e){
					return getError("上传文件失败。");
				}finally{
					item.delete();
				}

				JSONObject obj = new JSONObject();
				obj.put("error", 0);
				obj.put("url", urlAddr);
				return obj.toJSONString();
			}
		}
		return getError("未知错误");
	}
	
	/**
	 * 保存到oss具体方法
	 * @param fileName 文件名
	 * @param contentLength 文件长度
	 * @param input 文件流
	 * @param prefix 前缀
	 * @return
	 */
	public String ossUpload(String fileName, long contentLength , InputStream input,String prefix) {
		String upUrl=null;
		//仓库地址
		String bucketName=OSSProperties.bucketName;
		
		//设置oss客户端
		OSSClient client=OSSClientFactory.createOSSClient();
		//设置元数据
		ObjectMetadata om=new ObjectMetadata();
		//设置长度
		om.setContentLength(contentLength);
		//提交到oss
		PutObjectResult result=client.putObject(bucketName, prefix+fileName, input , om);
		
		if(OSSProperties.ossStatus){
			upUrl=OSSProperties.kindEditorPrefix+prefix+fileName;
		}else{
			upUrl=prefix+fileName;
		}
		
		return upUrl;
	}
	
	/**
	 * 保存文件到本地
	 * @param prefix
	 * @return
	 */
	public String localUpload(String prefix){

		//文件保存目录路径
		String savePath = request.getSession().getServletContext().getRealPath("/") + prefix;

		//文件保存目录URL
		String saveUrl  = request.getContextPath() + "/"+prefix;

		//定义允许上传的文件扩展名
		HashMap<String, String> extMap = new HashMap<String, String>();
		extMap.put("image", "gif,jpg,jpeg,png,bmp");
		extMap.put("flash", "swf,flv");
		extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
		extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");

		//最大文件大小
		long maxSize = 1000000;

		response.setContentType("text/html; charset=UTF-8");

		if(!ServletFileUpload.isMultipartContent(request)){
			return getError("请选择文件。");
		}
		//检查目录
		File uploadDir = new File(savePath);
		if(!uploadDir.isDirectory()){
			//out.println(getError("上传目录不存在。"));
			//return;
			//创建目录
			uploadDir.mkdirs();
		}
		//检查目录写权限
		if(!uploadDir.canWrite()){
			return getError("上传目录没有写权限。");
		}

		//如果没有前缀就用image做文件夹名
		String dirName = request.getParameter("dir");
		if (dirName == null) {
			dirName = "image";
		}
		if(!extMap.containsKey(dirName)){
			return getError("目录名不正确。");
		}
		
		//创建以dirName为文件名的文件夹
		savePath += dirName + "/";
		saveUrl += dirName + "/";
		File saveDirFile = new File(savePath);
		if (!saveDirFile.exists()) {
			saveDirFile.mkdirs();
		}
		
		//以当前日期作为二级目录名
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String ymd = sdf.format(new Date());
		savePath += ymd + "/";
		saveUrl += ymd + "/";
		File dirFile = new File(savePath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		
		List items=null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e1) {
		}
		Iterator itr = items.iterator();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			String fileName = item.getName();
			long fileSize = item.getSize();
			if (!item.isFormField()) {
				//检查文件大小
				if(item.getSize() > maxSize){
					return getError("上传文件大小超过限制。");
				}
				//检查扩展名
				String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
				if(!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
					return getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。");
				}

				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
				try{
					File uploadedFile = new File(savePath, newFileName);
					item.write(uploadedFile);
				}catch(Exception e){
					return getError("上传文件失败。");
				}

				JSONObject obj = new JSONObject();
				obj.put("error", 0);
				obj.put("url", saveUrl + newFileName);
				return obj.toJSONString();
			}
		}
		return getError("未知错误");
	}
	
	/**
	 * 设置错误信息
	 * @param message
	 * @return
	 */
	private String getError(String message) {
		JSONObject obj = new JSONObject();
		obj.put("error", 1);
		obj.put("message", message);
		return obj.toJSONString();
	}

}
