package com.dhb.nfc.entity;

public class Constants {
	/**
	 * 通讯服务商
	 */
	public static final String wechat_nfc_merch = "wechat";
	public static final String alipay_nfc_merch = "alipay";
	public static final String wechat_public_number_nfc_merch = "wechat_public_number";
	public static final String oneCodePay_nfc_merch = "oneCodePay";
	/**
	 * 商户通道编码
	 */
	public static final String nfc_merch_channel_yacol = "yacol";//雅酷
	public static final String nfc_merch_channel_hrt = "hrt";//和融通
	public static final String nfc_merch_channel_qj = "qj";//全晶
	public static final String nfc_merch_channel_ccb = "ccb";//中信
	public static final String nfc_merch_channel_rf = "rf";//融服
	/**
	 * 版本号
	 */
	public static final String wechat_version_1 = "1.0";
	public static final String wechat_version_2 = "2.0";
	/**
	 * 字符集
	 */
	public static final String wechat_charset_00 = "GBK";
	public static final String wechat_charset_01 = "GB2312";
	public static final String wechat_charset_02 = "UTF-8";
	/**
	 * 签名方式 
	 */
	public static final String wechat_sing_type = "RSA";
	/**
	 * 业务子类型
	 * 11:微信公众号
	 * 12:微信扫码支付
	 * 13:微信小额支付
	 * 21：支付宝被扫支付
	 * 22：支付宝扫码支付
	 */
	public static final String wechat_sub_type_11 = "11";
	public static final String wechat_sub_type_12 = "12";
	public static final String wechat_sub_type_13 = "13";
	public static final String wechat_sub_type_21 = "21";
	public static final String wechat_sub_type_22 = "22";
	/**
	 * 业务类型
	 * 下单
	 * 查询
	 * 退款
	 */
	public static final String wechat_service_newGatePayment = "newGatePayment";
	public static final String wechat_service_OrderSearch = "OrderSearch";
	public static final String wechat_service_OrderRefund = "OrderRefund";
	/**
	 * 订单有效单位
	 * 分
	 * 小时
	 * 日
	 * 月
	 */
	public static final String wechat_validUnit_00 = "00";
	public static final String wechat_validUnit_01 = "01";
	public static final String wechat_validUnit_02 = "02";
	public static final String wechat_validUnit_03 = "03";
	/**
	 * 订单有效数字
	 */
	public static final String wechat_validNum = "10";
	
	
	/**
	 * 通讯类型
	 * 被扫
	 * 主扫
	 * 冲正
	 * 退货
	 * 查询订单（内部区分支付和退货）
	 * 通道支付
	 */
	public static final String nfc_passive = "nfc_passive";
	public static final String nfc_active = "nfc_active";
	public static final String nfc_reversal = "nfc_reversal";
	public static final String nfc_refund = "nfc_refund";
	public static final String nfc_query = "nfc_query";
	public static final String nfc_channelPay = "nfc_channelPay";
	
	/**
	 * 业务状态
	 * 被扫业务先生成二维码，支付后变成成功或失败 
	 */
	public static final String nfc_pay_status_0 = "0";
	public static final String nfc_pay_status_0_context = "交易中";
	public static final String nfc_pay_status_1 = "1";
	public static final String nfc_pay_status_1_context = "交易完成";
	public static final String nfc_pay_status_1_context_refund_RP = "交易完成，部分退款";
	public static final String nfc_pay_status_1_context_refund_RF = "交易完成，全部退款";
	public static final String nfc_pay_status_1_context_refund_RE = "交易完成，订单退款";
	public static final String nfc_pay_status_1_context_refund = "交易完成，转入退款";
	public static final String nfc_pay_status_1_context_close = "交易完成，已关闭";
	public static final String nfc_pay_status_1_context_reversal = "交易完成，已冲正";
	public static final String nfc_pay_status_2 = "2";
	public static final String nfc_pay_status_2_context = "退货中";
	public static final String nfc_pay_status_3 = "3";
	public static final String nfc_pay_status_3_context = "未支付";
	public static final String nfc_pay_status_4 = "4";
	public static final String nfc_pay_status_4_context = "冲正申请提交成功";
	public static final String nfc_pay_status_5 = "5";
	public static final String nfc_pay_status_5_context = "退货订单转入代发";
	public static final String nfc_pay_status_9 = "9";
	public static final String nfc_pay_status_9_context = "交易失败";
	public static final String nfc_pay_status_19 = "19";
	public static final String nfc_pay_status_19_context = "超时失效";
	public static final String nfc_pay_status_19_context_ZHL = "ZHL_超时失效";
	public static final String nfc_pay_status_29 = "29";
	public static final String nfc_pay_status_29_context = "退货未确定";
    /**
     * 交易类型_11代收
     */
//    public static final String anyz_txnType_11 = "11";
//    public static final String anyz_txnType_12 = "12";
//    public static final String anyz_txnType_00 = "00";

    
}
