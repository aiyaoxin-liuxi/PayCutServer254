package com.dhb.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.dhb.entity.BizType;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.SingleResp;
@Service
public class ProxySinglePayCutThreadService {

	private Executor executor = Executors.newCachedThreadPool();
	
	
	
	public void toSinglePayCut(OutRequestInfo info,PayCutInterface payCutService,CountDownLatch threadsSignal,SingleResp resp){
		PayThread pt = new PayThread(info,payCutService,threadsSignal,resp);
		executor.execute(pt);
	}

	class PayThread implements Runnable{
		private OutRequestInfo info;
		private CountDownLatch threadsSignal;
		private PayCutInterface payCutService;
		private SingleResp resp;
		public PayThread(OutRequestInfo info,PayCutInterface payCutService,CountDownLatch threadsSignal,SingleResp resp){
			this.info = info;
			this.payCutService = payCutService;
			this.setThreadsSignal(threadsSignal);
			this.setResp(resp);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if(info!=null){
					
					BizType currentType =BizType.findByCode(info.getBizType());
					if(BizType.Cut.equals(currentType)){
						SingleResp singleResp =payCutService.singleCut(info);
						setResp(singleResp);
					}
					if(BizType.Pay.equals(currentType)){
						SingleResp singleResp =payCutService.singlePay(info);
						setResp(singleResp);
					}
					threadsSignal.countDown();
					/*if(BizType.SendSalary.equals(currentType)){
						payCutService.sendSalary(tranReq);
					}*/
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public PayCutInterface getPayCutService() {
			return payCutService;
		}
		public void setPayCutService(PayCutInterface payCutService) {
			this.payCutService = payCutService;
		}
		public CountDownLatch getThreadsSignal() {
			return threadsSignal;
		}
		public void setThreadsSignal(CountDownLatch threadsSignal) {
			this.threadsSignal = threadsSignal;
		}
		public SingleResp getResp() {
			return resp;
		}
		public void setResp(SingleResp resp) {
			this.resp = resp;
		}
		
	}
}
