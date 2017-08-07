package com.dhb.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;


public class CommonObjectDao extends NamedParameterJdbcDaoSupport{
	private static final Log logger = LogFactory.getLog(CommonObjectDao.class);

	public  void saveOrUpdate(String sql ,Object commonObject){
		logger.debug("sql:("+sql+")");
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(commonObject);
		this.getNamedParameterJdbcTemplate().update(sql, paramSource);
	}
	
	public <T> List<T> findList(String sql,Class<T> Class,Object[] param){
		logger.debug("sql:("+sql+") :param:"+printParm(param));
		BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<T>(Class);
		List<T> list=this.getJdbcTemplate().query(sql, rowMapper,param);
		return list;
	}
	public <T> T findOneObject(String sql,Class<T> Class,Object[] param){
		logger.debug("sql:("+sql+") :param:"+printParm(param));
		BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<T>(Class);
		List<T> list=this.getJdbcTemplate().query(sql, rowMapper,param);
		if(list.size()==1){
			return list.get(0);
		}
		return null;
	}
	public String findSingleVal(String sql,Object[] param){
		logger.debug("sql:("+sql+") :param:"+printParm(param));
		
		List<String> list=this.getJdbcTemplate().queryForList(sql, String.class,param);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	private String  printParm(Object[] param){
		StringBuilder sb = new StringBuilder();
		for(Object o:param){
			sb.append(o).append(",");
		}
		return sb.toString();
	}
}
