package com.oss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 加载配置
 *
 */
public class OSSProperties {
	public static Properties ossProperties=new Properties();
	public static boolean ossStatus;//是否使用oss
	public static String accessKeyId;//ossKey
	public static String accessKeySecret;//ossSecret
	public static String endpoint;//存放路径
	public static String bucketName;//仓库名
	
	//配置
	public static int socketTimeout;//打开连接传输数据的超时时间
	public static int connectionTimeout;//建立连接的超时时间
	public static int maxErrorRetry;//可重试的请求失败后最大的重试次数
	
	public static String kindEditorPrefix;
	
	
	
	static{
		String ossPath=SystemUtil.getProjectClassesPath()+ "ossProperties.properties";
		
		InputStream input=null;
		try {
			input = new FileInputStream(new File(ossPath));//网络发布用
			//input=Class.class.getResourceAsStream("/ossProperties.properties");//本地测试用
			ossProperties.load(input);
			ossStatus="true".equalsIgnoreCase((String)ossProperties.getProperty("ossStatus"))?true:false;
			accessKeyId=(String)ossProperties.getProperty("accessKeyId");
			accessKeySecret=(String)ossProperties.getProperty("accessKeySecret");
			endpoint=(String)ossProperties.getProperty("endpoint");
			bucketName=(String)ossProperties.getProperty("bucketName");
			
			socketTimeout=Integer.parseInt((String)ossProperties.getProperty("socketTimeout"));
			connectionTimeout=Integer.parseInt((String)ossProperties.getProperty("connectionTimeout"));
			maxErrorRetry=Integer.parseInt((String)ossProperties.getProperty("maxErrorRetry"));
			
			kindEditorPrefix=(String)ossProperties.getProperty("kindEditorPrefix");
		} catch (FileNotFoundException e) {
			System.out.println("找不到配置文件");
		} catch (IOException e) {
			System.out.println("IO异常");
		}catch (Exception e) {
			System.out.println("其他错误");
		}finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
