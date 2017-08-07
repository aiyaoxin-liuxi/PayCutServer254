package com.dhb.entity;

import java.util.List;

public class LeftTree {

	private Long id;
	
	private String url;
	
	private String name;
	
	private int type =0;
	
	private Integer order;
	
	private Integer level;
	
	private List<LeftTree> children;
	
	private LeftTree parent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public List<LeftTree> getChildren() {
		return children;
	}

	public void setChildren(List<LeftTree> children) {
		this.children = children;
	}

	public LeftTree getParent() {
		return parent;
	}

	public void setParent(LeftTree parent) {
		this.parent = parent;
	}
	
	
}
