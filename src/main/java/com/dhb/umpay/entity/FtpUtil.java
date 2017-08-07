package com.dhb.umpay.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;


public class FtpUtil {
	
	/**
	 * 生成TXT文本
	 * @param content
	 * @param fileName
	 * @return
	 */
	public static boolean createTxt(String content,String fileName,String filePath){
		System.out.println("===========开始生成TXT文件============");
	    byte[] sourceByte = content.getBytes();  
	    if(null != sourceByte){  
	        try {  
	            File file = new File(filePath);     //文件路径（路径+文件名）  
	            if (!file.exists()) {   //文件不存在则创建文件，先创建目录  
	                File dir = new File(file.getParent());  
	                dir.mkdirs();  
	                file.createNewFile();  
	            }  
	            FileOutputStream outStream = new FileOutputStream(file);    //文件输出流用于将数据写入文件  
	            outStream.write(sourceByte);  
	            outStream.close();  //关闭文件输出流  
	            System.out.println("===========TXT文件生成成功！============");
	            return true;
	        } catch (Exception e) {  
	            e.printStackTrace();  
	            return false;
	        }  
	    }
	    return false;
	}
	public static boolean upLoadFromProduction(String filename,String orginfilename) {  
		FtpFileUtils ftp = new FtpFileUtils();
		boolean flag = false;
		if(ftp.ftpConnect()){
			flag = ftp.ftpUpload(orginfilename,filename);
		}
		if(ftp.ftpIsConnected()){
			ftp.ftpDisConnect();
		}
		return flag;
	}
	/**
	 * 将本地文件上传到FTP服务器上
	 * @param filename 上传到FTP服务器上的文件名
	 * @param orginfilename 文件路径
	 */
//    public static boolean upLoadFromProduction(String filename,String orginfilename) {  
//        try {  
//        	System.out.println("【umpay ftp file】filename="+filename+",filePath="+orginfilename);
//        	String url = "221.179.195.189";//ResourceUtil.getString("umpay", "ftpUrl");//FTP服务器hostname  
//        	String port = "21";// ResourceUtil.getString("umpay", "ftpPort");// FTP服务器端口  
//        	String username = "24026";//ResourceUtil.getString("umpay", "ftpUserName");// FTP登录账号  
//        	String password = "Ad/Fu2!g";//ResourceUtil.getString("umpay", "ftpPwd");// FTP登录密码  
//        	String path = "/data/ftp/uecp/24026";//ResourceUtil.getString("umpay", "ftpPath");// FTP服务器保存目录  
//            FileInputStream in = new FileInputStream(new File(orginfilename));  
//            boolean flag = uploadFile(url,Integer.parseInt(port), username, password, path,filename, in);  
////            boolean flag = uploadFile("218.240.148.250",Integer.parseInt(port), "beifen", "zhao.3821", "/home/beifen",filename, in);  
//            System.out.println("【umpay ftp file】filename="+filename+"上传结果："+flag);
//            return flag;
//        } catch (Exception e) {  
//            e.printStackTrace();  
//            return false;
//        }  
//    }  
    private static byte[] getBytes(String filePath){  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    }
    /** 
     * Description: 向FTP服务器上传文件 
     * @Version      1.0 
     * @param url FTP服务器hostname 
     * @param port  FTP服务器端口 
     * @param username FTP登录账号 
     * @param password  FTP登录密码 
     * @param path  FTP服务器保存目录 
     * @param filename  上传到FTP服务器上的文件名 
     * @param input   输入流 
     * @return 成功返回true，否则返回false * 
     */  
    private static boolean uploadFile(String url,// FTP服务器hostname  
            int port,// FTP服务器端口  
            String username, // FTP登录账号  
            String password, // FTP登录密码  
            String path, // FTP服务器保存目录  
            String filename, // 上传到FTP服务器上的文件名  
            InputStream input // 输入流  
    ){  
        boolean success = false;  
        FTPClient ftp = new FTPClient();  
        ftp.setControlEncoding("UTF-8");  
        try {  
            int reply;  
            ftp.connect(url,port);// 连接FTP服务器  
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器  
            ftp.login(username, password);// 登录  
            reply = ftp.getReplyCode();  
            ftp.setDataTimeout(120000); 
            System.out.println("【umpay ftp file】reply："+reply);
            if (!FTPReply.isPositiveCompletion(reply)) {  
                ftp.disconnect();  
                System.err.println("FTP server refused connection."); 
                return success;  
            }  
            ftp.makeDirectory(path);  
            ftp.changeWorkingDirectory(path); 
            ftp.enterLocalPassiveMode();
            ftp.storeFile(filename, input);  
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);  
            input.close();  
            ftp.logout(); 
            success = true;
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (ftp.isConnected()) {  
                try {  
                    ftp.disconnect();  
                } catch (IOException ioe) {  
                	ioe.printStackTrace();
                }  
            }  
        }  
        return success;  
    }  
}
