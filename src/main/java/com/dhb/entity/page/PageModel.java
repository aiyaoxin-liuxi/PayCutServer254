package com.dhb.entity.page;

import java.util.List;

import com.google.common.collect.Lists;


public class PageModel<T> implements java.io.Serializable{
	private Class<T> genericType;
	
	public PageModel(Class<T> c ){
		this.genericType =c;
	}
	public Class<T> getGenericType()
    {
         return genericType;
    }
	private int total;

	private List<T> datas =Lists.newArrayList();

	private int pageSize = 10;

	private int pageNo = 1;

	@SuppressWarnings("unused")
	private int totalPages;
	
	private String orders;
	private String sorts;
	
	public PageModel(int pageNo, int pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}
	//求总页数
	public int getTotalPages() {
		if(total % pageSize == 0){
			   this.totalPages = total / pageSize;
			   }else{
			   this.totalPages= (total / pageSize) + 1;
			   }
	/*	return (int) ((this.total + pageSize - 1) / pageSize);*/
		return totalPages;
	}
	public int getPageSize() {
		if(this.pageSize > 200)
			throw new RuntimeException("每页记录数"+pageSize+"设置过大,请设置一个小于200的数值");
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = (List<T>) datas;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	
	public String getOrders() {
		return orders;
	}
	public void setOrders(String orders) {
		this.orders = orders;
	}
	public String getSorts() {
		return sorts;
	}
	public void setSorts(String sorts) {
		this.sorts = sorts;
	}
	public PageModel(List<T> datas,int total,int pageSize,int pageNo){
		this.datas = datas;
		this.total = total;
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		
	}
	
}
