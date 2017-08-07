package com.dhb.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BizType;
@Service
public class ProxyBatchPayThreadService {

	private Executor executor = Executors.newCachedThreadPool();
	
	
	
	public void toBatchPay(BatchTranReq tranReq,PayCutInterface payCutService){
		PayThread pt = new PayThread(tranReq,payCutService);
		executor.execute(pt);
	}

	class PayThread implements Runnable{
		private BatchTranReq tranReq;
		private PayCutInterface payCutService;
		public PayThread(BatchTranReq tranReq,PayCutInterface payCutService){
			this.tranReq = tranReq;
			this.payCutService = payCutService;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if(tranReq!=null){
					
					BizType currentType =BizType.findByCode(tranReq.getBizType());
					if(BizType.Cut.equals(currentType)){
						payCutService.batchCut(tranReq);
					}
					if(BizType.Pay.equals(currentType)){
						payCutService.batchPay(tranReq);
					}
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
		
	}
}
