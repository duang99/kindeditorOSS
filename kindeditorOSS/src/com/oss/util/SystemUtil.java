package com.oss.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 设置系统路径
 *
 */
public class SystemUtil {

	// 系统相关路径
	private static String rootPath = null;
	private static String classesPath = null;
	private static String projectName = null;

	/**
	 * 获取系统编译文件的路径
	 * 例:-> F:/My%20Project/ali-oss/WebRoot/WEB-INF/classes/
	 * @return
	 */
	public static String getProjectClassesPath() {
		if (classesPath == null) {
			classesPath = SystemUtil.class.getClassLoader().getResource("").getPath().trim();
			if (!isLinux()) {
				classesPath = classesPath.replaceFirst("/", "");
			}
			try {
				classesPath=java.net.URLDecoder.decode(classesPath, "utf-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return classesPath;
	}

	// 系统类型
	private static String osName = null;

	/**
	 * GET Project Root Path
	 * 例:-> F:/My%20Project/ali-oss/WebRoot/
	 * @return /var/opt/tomcat/../project_name/
	 */
	public static String getProjectRootPath() {
		if (rootPath == null) {
			String classesPath = getProjectClassesPath();
			// java
			if (classesPath.endsWith("build/classes/")) {
				rootPath = classesPath.replace("build/classes/", "");
			} else if (classesPath.endsWith("WEB-INF/classes/")) {
				// java web
				rootPath = classesPath.replace("WEB-INF/classes/", "");
			}
		}
		return rootPath;
	}

	/**
	 * 获取项目名称
	 * 例:-> WebRoot
	 * @return project_name
	 */
	public static String getProjectName() {
		if (projectName == null) {
			String classesPath = getProjectClassesPath();
			// java
			String rootPath = "";
			if (classesPath.endsWith("build/classes/")) {
				rootPath = classesPath.replace("build/classes/", "");
			} else if (classesPath.endsWith("WEB-INF/classes/")) {
				// java web
				rootPath = classesPath.replace("WEB-INF/classes/", "");
			}
			rootPath += "__";
			rootPath = rootPath.replace("/__", "");
			rootPath = rootPath.replaceAll("/", "/__");
			int index = rootPath.lastIndexOf("/__");
			if (index == -1) {
				return "";
			}
			projectName = rootPath.substring(index + 3);
		}
		return projectName;
	}

	/**
	 * 获取系统的类型
	 * 
	 * @return
	 */
	public static String getOsName() {
		if (osName == null) {
			Properties prop = System.getProperties();
			osName = prop.getProperty("os.name");
		}
		return osName;
	}

	/**
	 * 判断系统是否为Linux
	 * 
	 * @return true：linux false: win
	 */
	public static boolean isLinux() {
		if (getOsName().startsWith("win") || getOsName().startsWith("Win")) {
			return false;
		}
		return true;
	}

	 public static void main(String[] args) {
		 System.out.println("isLinux:"+isLinux());
		 System.out.println("getOsName:"+getOsName());
		 System.out.println("getProjectName:"+getProjectName());
		 System.out.println("getProjectClassesPath:"+getProjectClassesPath());
		 System.out.println("getProjectRootPath:"+getProjectRootPath());
		 
	 }
}