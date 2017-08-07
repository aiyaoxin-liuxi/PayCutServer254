package com.dhb.mobile.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;   

/**  
 * 响应对象  
 */  
public class HttpResponse {   
    
    String urlString;   
    
    int defaultPort;   
    
    String file;   
    
    String host;   
    
    String path;   
    
    int port;   
    
    String protocol;   
    
    String query;   
    
    String ref;   
    
    String userInfo;   
    
    String contentEncoding;   
    
    String content;   
    
    String contentType;   
    
    int code;   
    
    String message;   
    
    String method;   
    
    int connectTimeout;   
    
    int readTimeout;   
    
    Vector<String> contentCollection;   
    
    public String getContent() {   
        return content;   
    }   
    
    public String getContentType() {   
        return contentType;   
    }   
    
    public int getCode() {   
        return code;   
    }   
    
    public String getMessage() {   
        return message;   
    }   
    
    public Vector<String> getContentCollection() {   
        return contentCollection;   
    }   
    
    public String getContentEncoding() {   
        return contentEncoding;   
    }   
    
    public String getMethod() {   
        return method;   
    }   
    
    public int getConnectTimeout() {   
        return connectTimeout;   
    }   
    
    public int getReadTimeout() {   
        return readTimeout;   
    }   
    
    public String getUrlString() {   
        return urlString;   
    }   
    
    public int getDefaultPort() {   
        return defaultPort;   
    }   
    
    public String getFile() {   
        return file;   
    }   
    
    public String getHost() {   
        return host;   
    }   
    
    public String getPath() {   
        return path;   
    }   
    
    public int getPort() {   
        return port;   
    }   
    
    public String getProtocol() {   
        return protocol;   
    }   
    
    public String getQuery() {   
        return query;   
    }   
    
    public String getRef() {   
        return ref;   
    }   
    
    public String getUserInfo() {   
        return userInfo;   
    }   
    
    //
    private int responseCode;
	private String body;

	public int getResponseCode() {
		return this.responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuffer sb = new StringBuffer();
		Field[] fields = this.getClass().getDeclaredFields();
		sb.append(this.getClass().getName() + "{");
		for (int i = 0; i < fields.length; i++) {
			try {
				if (fields[i].get(this) instanceof Date) {
					if (fields[i].get(this) != null) {
						sb.append(fields[i].getName() + ":").append(
								sdf.format(fields[i].get(this))).append(";");
						continue;
					}
				}
				sb.append(fields[i].getName()).append(":").append(
						fields[i].get(this)).append(";");
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		}
		sb.append("}");
		return sb.toString();
	}
}