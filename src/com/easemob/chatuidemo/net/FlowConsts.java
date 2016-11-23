package com.easemob.chatuidemo.net;
public class FlowConsts {

//	public static final String SERVICE_IP = "http://falago.cn/doornew/dl";
//	public static final String SERVICE_IP = "http://192.168.10.171:8080/door/dl";
//	public static final String SERVICE_IP = "http://falago.cn/door/dl";
//	public static final String SERVICE_IP = "http://wms.fala.cn/doornew/dl";
	public static final String SERVICE_IP = "https://www.ideling.com/door/dl";
	//key
	public static final String KEY_XML="zxncasdasdaweqws";
	//状态 'Y'
	public static final String STATU_Y="Y";
	// 生产
	// 自己主机
//	 public static final String SERVICE_IP =
//	 "http://10.0.0.2:8080/ykdoctor/ykys";
	// 测试
//	 public static final String SERVICE_IP =
//	 "http://10.18.104.59:9998/ykdoctor/ykys";

	// 返回公共参数returnCode
	// 标记，1成功；2tonken失效；非1，2失败
	public static final String STATUE_1 = "1";
	public static final String STATUE_2 = "2";
	public static final String STATUE_3 = "-1";
	public static final String STATUE_4 = "-2";
	public static final String STATUE_5 = "-3";

	public static final int INT_1 = 1;
	public static final int INT_7 = 7;

	public static final int INT_00 = 0;
	public static final int INT_08 = 1;
	public static final int INT_05 = 5;
	
	public static final int INT_10 = 10;
	public static final int INT_20 = 20;
	public static final int INT_24 = 24;
	public static final int INT_30 = 30;
	public static final int INT_50 = 50;
	public static final int INT_300 = 300;
	public static final int INT_1000 = 1000;
	public static final int INT_3600 = 3600;
	public static final String MONEY = "0.00";
	/**
	 * 患者id前缀,用于区分患者和医生
	 */
	public static final String P = "p";

	/**
	 * 男
	 */
	public static final String MALE = "1";

	/**
	 * 女
	 */
	public static final String FEMALE = "0";
	/**
	 * 0
	 */
	public static final String ZERO = "0";
	/**
	 * 1
	 */
	public static final String ONE = "1";
	/**
	 * 46000
	 */
	public static final String CHINA_MOBILE_46000 = "46000";
	/**
	 * "46002"
	 */
	public static final String CHINA_MOBILE_46002 = "46002";
	/**
	 * 中国移动
	 */
	public static final String CHINA_MOBILE = "中国移动";
	/**
	 * 46001
	 */
	public static final String CHINA_UNICOM_46001 = "46001";
	/**
	 * "中国联通"
	 */
	public static final String CHINA_UNICOM = "中国联通";
	/**
	 * "46003"
	 */
	public static final String CHINA_TELECOM_46003 = "46003";
	/**
	 * 中国电信
	 */
	public static final String CHINA_TELECOM = "中国电信";
	/**
	 * phone
	 */
	public static final String PHONE = "phone";
	/**
	 * Disconnected
	 */
	public static final String DISCONNECTED = "Disconnected";
	/**
	 * unknown_app_id
	 */
	public static final String UNKNOWN_APP_ID = "unknown_app_id";
	/**
	 * unknown_client_key
	 */
	public static final String UNKNOWN_CLIENT_KEY = "unknown_client_key";

	/**
	 * SELECT
	 */
	public static final String SELECT = "2";
	/**
	 * chat
	 */
	public static final String CHAT = "chat";

	public static final String Y = "1";
	public static final String N = "2";

	public static final String IMG_PATH = "/cache";
	public static final String IMG_FILE = "/avater.png";
	public static final String NO_SDCARD = "No SDCard";
	public static final String LOADING = "loading";
	public static final String LODER = "416";

	public static final int ADDPAIBAN = 1234;// 添加排班
	public static final int RESULT = 5465;// 删除排班RESULT
	public static final int TX = 123;// 提现requestCode
	public static final int REQUESTCODE = 123;// 删除
	public static final int ADDYHK = 2;// 添加银行卡
	public static final int CKTP = 2345;// 查看图片
	public static final int FBDT = 14;// 发表动态
	public static final int ZLFW = 12345;// 诊疗服务
	public static final int RESULT_OK = -1;// RESULT_OK

	/**
	 * 1.审核中；2.审核通过;3.审核不通过
	 */

	/**
	 * AUDITING
	 */
	public static final String AUDITING = "1";
	/**
	 * AUDITED
	 */
	public static final String AUDITED = "2";
	/**
	 * NO_AUDIT
	 */
	public static final String NO_AUDIT = "3";

	/**
	 * SHOWING
	 */
	public static final String SHOWING = "4";
	/**
	 * SEND
	 */
	public static final String SEND = "send";

	/**
	 * PROVINCE
	 */
	public static final String PROVINCE = "1";
	/**
	 * CITY
	 */
	public static final String CITY = "2";
	/**
	 * area
	 */
	public static final String AREA = "3";

	/**
	 * hospital
	 */
	public static final String HOSPITAL = "4";

	/**
	 * department
	 */
	public static final String DEPARTMENT = "5";

	/**
	 * PAGE
	 */
	public static final String PAGE = "1";

	/**
	 * SRTING_TX
	 */
	public static final String SRTING_TX = "tx";

	/**
	 * patient
	 */
	public static final String PATIENT = "2";
	/**
	 * doctor
	 */
	public static final String DOCTOR = "1";
	/**
	 * all
	 */
	public static final String ALL = "3";

	/**
	 * REGISTER
	 */
	public static final String REGISTER = "1";
	/**
	 * REFUSED
	 */
	public static final String REFUSED = "2";
	/**
	 * accept
	 */
	public static final String ACCEPT = "3";

	/**
	 * Today for diagnosis
	 */
	public static final String TODAY_FOR_DIAGNOSIS = "6";
	/**
	 * Has been completed
	 */
	public static final String HAS_BEEN_COMPLETED = "5";
	/**
	 * Have to make an appointment
	 */
	public static final String HAVE_TO_MAKE_AN_APPOINTMENT = "4";

	/**
	 * Clinical follow-up after
	 */
	public static final String CLINICAL_FOLLOW_UP_AFTER = "3";

	public static final String REFLESH = "reflesh";// EVENTBUS
	public static final String FIRST = "first";// EVENTBUS
	public static final String NOT_FRIEND = "暂时不是好友,请先去添加好友吧!";// 
	public static final String RECONTENT = "重连成功";// 
	/**
	 * 名医诊疗
	 */
	public static final String ZSZL = "1";
	/**
	 * 电话预约
	 */
	public static final String DHYY = "2";
	/**
	 * 电话咨询
	 */
	public static final String DHZX = "3";
	/**
	 * 图文咨询
	 */
	public static final String TWZX = "4";

	/**
	 * general
	 */
	public static final String GENERAL = "1";
	/**
	 * MORE
	 */
	public static final String MORE = "2";
	/**
	 * ALL_TEXT
	 */
	public static final String ALL_TEXT = "3";
	/**
	 * PACK_UP
	 */
	public static final String PACK_UP = "4";

	/**
	 * WechatMoments
	 */
	public static final String WECHATMOMENTS = "WechatMoments";

	/**
	 * Wechat
	 */
	public static final String WECHAT = "Wechat";
	/**
	 * ShortMessage
	 */
	public static final String SHORTMESSAGE = "ShortMessage";

	/**
	 * FLAG
	 */
	public static final String FLAG = "1";

	/**
	 * START_TIME
	 */
	public static final String START_TIME = "10:00";
	/**
	 * STOP_TIME
	 */
	public static final String STOP_TIME = "14:00";

	/**
	 * Ordinary registered
	 */
	public static final String ORDINARY_REGISTERED = "1";
	/**
	 * Medical diagnosis
	 */
	public static final String MEDICAL_DIAGNOSIS = "2";
	/**
	 * Telephone counseling
	 */
	public static final String TELEPHONE_COUNSELING = "3";
	/**
	 * By consulting
	 */
	public static final String BY_CONSULTING = "4";

	/**
	 * Yuntou article
	 */
	public static final String YUNTOU_ARTICLE = "5";
	/**
	 * FAMOUS_DOCTOR_DYNAMIC
	 */
	public static final String FAMOUS_DOCTOR_DYNAMIC = "6";
	/**
	 * comments
	 */
	public static final String COMMENTS = "7";
	/**
	 * PATIENTS_REPORT
	 */
	public static final String PATIENTS_REPORT = "8";

	/**
	 * 通知推送
	 */
	public static final String TZTS = "5";

	/**
	 * 网络使用提醒
	 */
	public static final String WLSYTX = "6";

	/**
	 * 手势密码
	 */
	public static final String SSMM = "7";

	/**
	 * 开启
	 */
	public static final String OPEN = "1";

	/**
	 * 关闭
	 */
	public static final String CLOSE = "2";

	/**
	 * FRIEND
	 */
	public static final String FRIEND = "4";
	/**
	 * INVITATION
	 */
	public static final String INVITATION = "2";
	/**
	 * ADD
	 */
	public static final String ADD = "1";
	/**
	 * STRING_Y
	 */
	public static final String STRING_Y = "Y";

	public static final int GALLERY_REQUEST_CODE = 1;

	public static final int CAMERA_REQUEST_CODE = 2;

	// public static final String STATUE_Y = "Y";
	public static String XMLNAME = "ykys";

	public static String WEBVIEW_PATH = "javascript:window.html_obj.showSource('<head>'+"
			+ "document.getElementsByTagName('html')[0].innerHTML+'</head>');";

	// 系统参数接口
	public static String GETSYSTEMPARAM = SERVICE_IP + "/dl_getdldata";
	
	// 验证码接口
	public static String SENDIDENTIFYINGCODE = SERVICE_IP + "/dl_sendverifysms";
	
	// 登陆
	public static String LOGIN = SERVICE_IP + "/dl_login";
	
	// 注册
	public static String DL_REGISTER = SERVICE_IP + "/dl_register";
	
	// 修改密码
	public static String UPDATEPWD = SERVICE_IP + "/dl_updatepwd";
	
	// 查询钥匙
	public static String KEYINFO = SERVICE_IP + "/dl_keyinfo";
	
	// 查询可授权房间
	public static String CHECKUSERROOM = SERVICE_IP + "/dl_checkuserroom";
	
	// 获取省市区最新数据
	public static String GETPROCITY = SERVICE_IP + "/dl_get_province_city_region";
	
	// 获取小区
	public static String GETAREA = SERVICE_IP + "/dl_getarea";
	
	// 获取最近的小区(经纬度)
	public static String GETDEPARTMENT = SERVICE_IP + "/dl_getdepartment";
	
	// 获取二维码门栋
	public static String GETDEPARTMENTFROMKEY = SERVICE_IP + "/dl_getdepartmentfromkey";

	// 查询门栋
	public static String GETGATE = SERVICE_IP + "/dl_getgate";
	
	//查询房号
	public static String GETDOOR = SERVICE_IP + "/dl_getdoor";

	// 申请钥匙
	public static String APPLYKEY = SERVICE_IP + "/dl_applykey";
	
	// 查询申请记录
	public static String CHECKAPPLYKEY = SERVICE_IP + "/dl_checkapplykey";
	
	// 查询申请记录详情
	public static String CHECKAPPLYINFO = SERVICE_IP + "/dl_checkapplyinfo";

	// 查询小区通知信息
	public static String GETNOTICE = SERVICE_IP + "/dl_getnotice";
	
	//获取查看过的小区的信息的id
	public static String GETNOTICELOG = SERVICE_IP + "/dl_getnoticelog";
	
	//更新看过的小区的信息的id
	public static String UPDATENOTICELOG = SERVICE_IP + "/dl_updatenoticelog";
	
	// 查询授权
	public static String GETGRANT = SERVICE_IP + "/dl_getgrant";
	
	// 添加授权
	public static String ADDGRANT = SERVICE_IP + "/dl_addgrant";

	// 取消授权
	public static String DELGRANT = SERVICE_IP + "/dl_delgrant";
	
	// 同意授权
	public static String AGREEGRANT = SERVICE_IP + "/dl_agreegrant";
	
	// 开门通知
	public static String DOOROPENNOTIFY = SERVICE_IP + "/dl_dooropennotify";
	
	// 开门通知((批量上传)
	public static String DOOROPENNOTIFYBATCH = SERVICE_IP + "/dl_dooropenlist";
	
	// 开门记录查询
	public static String GETOPENDOOR = SERVICE_IP + "/dl_getopendoor";

	// 收藏优惠卷
	public static String GETDISCOUNT  = SERVICE_IP + "/dl_getdiscount";

	// 查询我的优惠卷
	public static String CHECKDISCOUNT = SERVICE_IP + "/dl_checkdiscount";
	
	// 上传图片
	public static String UPLOADIMG = SERVICE_IP + "/dl_uploadimg";
	
	// 更换头像
	public static String HEADIMG = SERVICE_IP + "/dl_headimg";
	
	// 意见反馈
	public static String SUGGEST = SERVICE_IP + "/dl_suggest";
	
	// 钥匙禁用和启用
	public static String KEYSET = SERVICE_IP + "/dl_keyset";

	// 查询广告
	public static String CHECKAD = SERVICE_IP + "/dl_checkad";
	
	
	/**
	 * PWD
	 */
	public static final String PWD = "547892";
}