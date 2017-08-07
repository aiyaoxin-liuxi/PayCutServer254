package com.dhb.umpay.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FTP文件上传
 * @author Liangwei
 * @version 2015-04-01
 */
public class FtpFileUtils{
	
	private FTPClient ftpClient = new FTPClient();

	private static Logger log = LoggerFactory.getLogger(FtpFileUtils.class);
	// 文件服务器信息
	public static String FILE_SERVER_IP="221.179.195.189";			// 文件服务器IP地址
	public static String FILE_SERVER_PORT="21"; 			// 文件服务器端口号
	public static String FILE_SERVER_USERNAME="24026";		// 文件服务器用户名 
	public static String FILE_SERVER_PASSWORD="Ad/Fu2!g"; 		// 文件服务器密码
	public static String FILE_FTP_PATH="/data/ftp/uecp/24026";
	  
    /** 
     * 连接到FTP服务器 （连接文件服务器时使用）
     *  
     */  
	int count = 0;
    public boolean ftpConnect() {  
    	boolean flag = false;
        count++;
        try { 
            ftpClient.connect(FILE_SERVER_IP, Integer.parseInt(FILE_SERVER_PORT));  
            ftpClient.setControlEncoding("GBK");  
//          ftpClient.setDataTimeout(120000);
            int reply = ftpClient.getReplyCode();
            if(FTPReply.isPositiveCompletion(reply)) {  
            	if(count<4){
	            	if(ftpClient.login(FILE_SERVER_USERNAME, FILE_SERVER_PASSWORD)) {
	                	flag = true;  
	                }else{ // 若连接失败，则重新连接
	                    ftpDisConnect(); // 关闭当前连接
		                ftpConnect(); // 重新连接
	                } 
            	}
            }  
        } catch (Exception e) { 
        	log.debug("1Failure connection!");
        	//System.out.println("2Failure connection!");  
            e.printStackTrace();  
        }  
        return flag;  
    } 
    
    /** 
     * 连接到FTP服务器 （连接影城服务器时使用）
     *  
     * @param host  FTP地址 
     * @param port  端口号 
     * @param username  用户名  
     * @param password  密码 
     */  
    public boolean ftpConnect(String host, int port, String username, String password) {  
    	boolean flag = false;
        try { 
            ftpClient.connect(host, port);  
            ftpClient.setControlEncoding("GBK");
            
            int reply = ftpClient.getReplyCode();  
            if(FTPReply.isPositiveCompletion(reply)) {  
                if(ftpClient.login(username, password)) {  
                	flag = true;  
                }else{ // 若连接失败，则重新连接
                    ftpDisConnect(); // 关闭当前连接
                    ftpConnect(); // 重新连接
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return flag;  
    }
    
    //从本地上传单个文件，localFile：本地文件路径包含文件名，ftpFolder：所要放于ftp的文件夹，newName：重命名  
    /**
     * 
     * @param localFile 服务器上文件路径
     * @param fileName  文件名称
     */
    public boolean ftpUpload(String localFile, String fileName) {  
        File srcFile = new File(localFile);  
        FileInputStream fis = null;  
        try { 
            fis = new FileInputStream(srcFile);  
            
            if (!ftpClient.changeWorkingDirectory(FILE_FTP_PATH)) {// 如果不能进入ftpFolder下，说明此目录不存在！  
                if (!makeDirectory(FILE_FTP_PATH)) {  
                	log.debug("1创建文件目录【"+FILE_FTP_PATH+"】 失败！");
                    System.out.println("2创建文件目录【"+FILE_FTP_PATH+"】 失败！");  
                }  
            }
            
            //改变工作目录到所需要的路径下  
            ftpClient.changeWorkingDirectory(FILE_FTP_PATH);    
            ftpClient.setBufferSize(1024);  
            ftpClient.setControlEncoding("UTF-8");  
//          FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);  
//          conf.setServerLanguageCode("zh");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            ftpClient.storeFile(fileName, fis);  
        } catch (Exception e) {  
        	log.debug("Failed to upload!");
            //System.out.println("Failed to upload!");  
            e.printStackTrace(); 
            return false;
        } finally {  
            try {  
                fis.close();  // 关闭输入流
//                ftpDisConnect();  
            } catch (IOException e) {  
                e.printStackTrace();  
                return false;
            }  
        }  
        return true;
    }
    
    //从本地上传单个文件，file：本地文件（图片或视频），ftpFolder：所要放于ftp的文件夹，newName：重命名
    public void ftpUploadFile(InputStream file, String ftpFolder, String newFileName) { 
        try {  
            if (!ftpClient.changeWorkingDirectory(ftpFolder)) {// 如果不能进入ftpFolder下，说明此目录不存在！  
                if(!makeDirectory(ftpFolder)) {  
                	log.debug("创建文件目录【"+ftpFolder+"】 失败！");
                   // System.out.println("创建文件目录【"+ftpFolder+"】 失败！");  
                }  
            }
            //改变工作目录到所需要的路径下  
            ftpClient.changeWorkingDirectory(ftpFolder);    
            ftpClient.setBufferSize(1024);  
            ftpClient.setControlEncoding("UTF-8");  
//          FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);  
//          conf.setServerLanguageCode("zh");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            ftpClient.storeFile(newFileName, file);  
          // System.out.println("Successed to upload!"); 
           log.debug("1 Successed to upload!"+FILE_SERVER_IP);
        } catch (Exception e) {  
        	log.debug("Failed to upload!");
            //System.out.println();  
            e.printStackTrace();  
        } finally {  
            try {  
            	file.close();  // 关闭输入流
//                ftpDisConnect();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    
    //从本地上传一个文件夹，localPath：本地文件夹，ftpPath：上传的资源所要存放的文件夹  
    public void ftpUploadFolder(String localPath, String ftpPath) {  
        File uploadFile = new File(localPath);  
        File[] fileList = uploadFile.listFiles();  
        FileInputStream fis = null;  
        if (fileList == null) {  
            return;  
        }  
        for (int i = 0; i < fileList.length; i++) {  
            try {  
                fis = new FileInputStream(fileList[i]);  
                String ftpFileName = fileList[i].getName();  
                ftpClient.changeWorkingDirectory(ftpPath);  
                ftpClient.setBufferSize(1024);  
                ftpClient.setControlEncoding("GBK");  
//              FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);  
//              conf.setServerLanguageCode("zh");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
                ftpClient.storeFile(ftpFileName, fis);  
  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        try {  
            fis.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        ftpDisConnect();  
  
    } 
    
    //从FTP下载单个文件  
    public void ftpDownload(String ftpFile, String localName) {  
        File outfile = new File(localName + "/" + ftpFile);  
        OutputStream fos = null;  
        try {  
            fos = new FileOutputStream(outfile);  
            ftpClient.setBufferSize(1024);  
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            ftpClient.retrieveFile(ftpFile, fos);  
        } catch (Exception e) {  
        	log.debug("Failed to upload!");
            //System.out.println("Failed to upload!");  
            e.printStackTrace();  
        } finally {  
            try {  
                fos.close();  
                ftpClient.disconnect();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
            ftpDisConnect();  
        }  
    } 

    //从FTP下载一个文件夹  
    public void ftpDownloadFolder(String ftpPath, String localPath) {  
        OutputStream fos = null;  
        File localFile = null;  
        try {  
            ftpClient.changeWorkingDirectory(ftpPath);  
            FTPFile[] fileList = ftpClient.listFiles();  
            for (int i = 0; i < fileList.length; i++) {  
                String localname = fileList[i].getName();  
                localFile = new File(localPath + "/" + localname);  
                fos = new FileOutputStream(localFile);  
                ftpClient.setBufferSize(1024);  
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        try {  
            fos.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        ftpDisConnect();  
  
    }  
    
    /** 
     * 在服务器上创建一个文件夹 
     * 
     * @param dir 
     *            文件夹名称，不能含有特殊字符，如 \ 、/ 、: 、* 、?、 "、 <、>... 
     */  
    public boolean makeDirectory(String dir) {
        boolean flag = true;  
        try {
            flag = ftpClient.makeDirectory(dir);  
            if (flag) {  
                System.out.println("make Directory " +dir +" succeed"); 
                //log.debug("make Directory " +dir +" succeed");
            } else {  
            	log.debug("make Directory " +dir+ " error!!");
            	// System.out.println("make Directory " +dir+ " false");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return flag;  
    }  
    
    //是否连接FTP服务器
    public boolean ftpIsConnected(){
    	boolean flag = true;
    	if(!ftpClient.isConnected()){
    		flag = false;
    	}
    	return flag;
    }
  
    // 断开FTP服务器
    public void ftpDisConnect() {  
        try {  
            if (ftpClient.isConnected()) { 
//            	ftpClient.logout();
                ftpClient.disconnect();  
            }  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }
    
}
