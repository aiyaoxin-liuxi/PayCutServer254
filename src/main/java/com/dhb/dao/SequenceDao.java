package com.dhb.dao;

import org.springframework.jdbc.core.support.JdbcDaoSupport;


public class SequenceDao extends JdbcDaoSupport{

	
	
	public long getNextVal(final String seq_name){	
		String sql = "select "+seq_name+".nextval from dual";
		return this.getJdbcTemplate().queryForObject(sql, Integer.class);
	}
	
	public long getCurrentVal(final String seq_name){
		String sql = "select "+seq_name+".currVal from dual";
		return this.getJdbcTemplate().queryForObject(sql, Integer.class);
	}
}
