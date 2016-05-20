package com.oss.service;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.oss.util.OSSProperties;

/**
 * OSSClient是OSS服务的Java客户端，它为调用者提供了一系列的方法，用于和OSS服务进行交互<br>
 * 
 */
public class OSSClientFactory {

	private static OSSClient client = null;
	
	/**
	 * 新建OSSClient 
	 * 
	 * @return
	 */
	public static OSSClient createOSSClient(){
		if ( null == client){
			ClientConfiguration config=new ClientConfiguration();
			//打开连接传输数据的超时时间（单位：毫秒）。默认为50000毫秒
			if(OSSProperties.socketTimeout>0)
				config.setSocketTimeout(OSSProperties.socketTimeout);
			//建立连接的超时时间（单位：毫秒）。默认为50000毫秒
			if(OSSProperties.connectionTimeout>0)
				config.setConnectionTimeout(OSSProperties.connectionTimeout);
			//可重试的请求失败后最大的重试次数。默认为3次
			if(OSSProperties.maxErrorRetry>0)
				config.setMaxErrorRetry(OSSProperties.maxErrorRetry);
			
			client = new OSSClient(OSSProperties.endpoint,OSSProperties.accessKeyId, OSSProperties.accessKeySecret,config);
		}
		return client;
	}
	
}
