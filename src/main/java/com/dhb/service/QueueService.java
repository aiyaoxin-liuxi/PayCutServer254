package com.dhb.service;

import java.util.LinkedList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Lists;


public class QueueService {

	private static QueueService queueService = new QueueService();
	public static QueueService getInstance(){
		return queueService;
	}
	private LinkedList<String> queue = Lists.newLinkedList();
	private final ReentrantLock lock = new ReentrantLock();
	public String getLast(){
		return queue.getLast();
	}
	
	public  void add(String data){
		lock.lock();
		if(queue.size()>0){
			queue.removeFirst();
		}
		queue.addLast(data);
		lock.unlock();
	}
	public int size(){
		return queue.size();
	}
	 
	public static void main(String[] args) {
		SynchronousQueue<String> queue = new SynchronousQueue<String>();
		String token ="33333";
		String data=queue.poll();
		System.out.println("data:"+data);

			queue.add(token);
		
		System.out.println("size:"+queue.size());
	}
}
