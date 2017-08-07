package com.dhb.anyz.entity;

public class Constants {
	/**
     * 签名方法
     */
    public static final String anyz_signMethod = "MD5";
    /**
     * 消息版本号
     */
    public static final String anyz_version = "1.0.0";
    /**
     * 交易类型_11代收
     */
    public static final String anyz_txnType_11 = "11";
    public static final String anyz_txnType_12 = "12";
    public static final String anyz_txnType_00 = "00";
    /**
     * 交易子类型_01消费
     */
    public static final String anyz_txnSubType = "01";
    /**
     * 产品类型_000501代收
     */
    public static final String anyz_bizType_cut = "000501";
    public static final String anyz_bizType_pay = "000401";
    
    /**
     * 接入类型_0商户直连接入,1收单机构接入,2平台商户接入
     */
    public static final String anyz_accessType_0 = "0";
    public static final String anyz_accessType_1 = "1";
    public static final String anyz_accessType_2 = "2";
    /**
     * 接入方式_01web
     */
    public static final String anyz_accessMode = "01";
    /**
     * 账户类型_01借记卡,02贷记卡,03存折,04公司账号
     */
    public static final String anyz_accType_01 = "01";
    public static final String anyz_accType_02 = "02";
    public static final String anyz_accType_03 = "03";
    public static final String anyz_accType_04 = "04";
    /**
     * 交易币种
     */
    public static final String anyz_currency_CNY = "CNY";
    /**
     * 支付方式_0401代付
     */
    public static final String anyz_payType_pay = "0401";
    /**
     * 支付方式_0501代扣
     */
    public static final String anyz_payType = "0501";
    /**
     * 商品标题_暂不传
     */
    public static final String anyz_subject = "";
    /**
     * 商品描述_暂不传
     */
    public static final String anyz_body = "";
    /**
     * 对公对私标志_00对公01对私
     */
    public static final String anyz_ppFlag_00 = "00";
    public static final String anyz_ppFlag_01 = "01";
    /**
     * 用途_暂不传
     */
    public static final String anyz_purpose = "";
    /**
     * 请求保留域_暂不传
     */
    public static final String anyz_merResv1 = "";
    /**
     * 业务类型  0为钱包业务，1为POSP业务   2为公众号业务
     */
    public static final String business_type_0 = "0";	
    public static final String business_type_1 = "1";
//    public static final String business_type_2 = "2";
    /**
     * 渠道号
     */
    public static final String anyz_channelId = "11";
    public static final String ysb_channelId = "3";
    
}
