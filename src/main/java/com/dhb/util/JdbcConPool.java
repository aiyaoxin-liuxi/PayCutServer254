package com.dhb.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class JdbcConPool {

	private static final Logger logger = Logger.getLogger(JdbcConPool.class);

	public static synchronized Connection getConnection() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource) envContext.lookup("jdbc/Oracle");

			return ds.getConnection();
		} catch (NamingException e) {
			logger.error("获取数据库连接失败！", e);
			throw new RuntimeException(e);
		} catch (SQLException e) {
			logger.error("获取数据库连接失败！", e);
			throw new RuntimeException(e);
		}
	}

	public static void close(ResultSet rs, Statement stmt, Connection con) {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				logger.error("ResultSet关闭失败！", e);
			}
		}

		if (stmt != null) {
			try {
				stmt.close();
				stmt = null;
			} catch (SQLException e) {
				logger.error("Statement关闭失败！", e);
			}
		}

		if (con != null) {
			try {
				con.close();
				con = null;
			} catch (SQLException e) {
				logger.error("数据库连接关闭失败！", e);
			}
		}
	}
}
