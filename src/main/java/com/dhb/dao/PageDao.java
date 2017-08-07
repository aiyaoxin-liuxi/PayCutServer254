package com.dhb.dao;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import com.dhb.entity.page.PageModel;



public class PageDao extends NamedParameterJdbcDaoSupport{
	public <T> List<T> getPage(String sql,Object[] param,PageModel<T> page){
		int totalNum=getTotalPages(sql,param);
		page.setTotal(totalNum);
		int pageNo = page.getPageNo();
		int pageSize = page.getPageSize();
		int startIndex= (int) ((pageNo -1)*pageSize);
		int lastIndex =  getLastIndex(page);
		StringBuffer paginationSQL = new StringBuffer(" SELECT * FROM ( ");
		paginationSQL.append(" SELECT temp.* ,ROWNUM num FROM ( ");
		paginationSQL.append(sql);
		paginationSQL.append("　) temp where ROWNUM <= " +lastIndex );
		paginationSQL.append(" ) WHERE　num > "+ startIndex);
	
		BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<T>(page.getGenericType());
		List<T> datas=getJdbcTemplate().query(paginationSQL.toString(),rowMapper,param);
		page.setDatas(datas);
		return datas;
	}
	public int getTotalPages(String sql,Object[] param){
		StringBuffer totalSQL = new StringBuffer(" SELECT count(*) FROM ( ");
		totalSQL.append(sql);
		totalSQL.append(" ) totalTable ");
		return getJdbcTemplate().queryForObject(totalSQL.toString(),param,Integer.class);
	}
	
	private <T> int getLastIndex(PageModel<T> page){
		int lastIndex =0;
		int currentPage = page.getPageNo();
		int numPerPage = page.getPageSize();
		int totalRows = page.getTotal();
		int totalPages =page.getTotalPages();
		 if (totalRows < numPerPage) {
			    lastIndex = totalRows;
			   } else if ((totalRows % numPerPage == 0)
			     || (totalRows % numPerPage != 0 && currentPage < totalPages)) {
			    lastIndex = currentPage * numPerPage;
			   } else if (totalRows % numPerPage != 0 && currentPage == totalPages) {//最后一页
			    lastIndex = totalRows;
			   }
		return lastIndex;
	}
	
}
