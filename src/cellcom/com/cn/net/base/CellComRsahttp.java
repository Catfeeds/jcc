package cellcom.com.cn.net.base;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.HttpHandler;

import org.afinal.simplecache.ConfigCacheUtil;
import org.apache.http.Header;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.widget.Toast;
import cellcom.com.cn.bean.DESBeanBase;
import cellcom.com.cn.bean.Info;
import cellcom.com.cn.net.CellComAjaxResult;
import cellcom.com.cn.util.CellcomUtil;
import cellcom.com.cn.util.Consts;
import cellcom.com.cn.util.Des3;
import cellcom.com.cn.util.LogMgr;
import cellcom.com.cn.util.RSAHelper;
import cellcom.com.cn.util.SharepreferenceUtil;
import cellcom.com.cn.util.Tracking;

public class CellComRsahttp implements CellComHttpInterface {
  private Context context;
  private FinalHttp finalHttp;
  private ConfigCacheUtil configCacheUtil;

  public CellComRsahttp(Context context, ConfigCacheUtil configCacheUtil) {
    this.finalHttp = new FinalHttp();
    this.context = context;
    this.configCacheUtil = configCacheUtil;
  }

  public void get3Des(String url, final NetCallBack<String> callBack) throws Exception {
    String publicKey = RSAHelper.getPublicKey();
    AjaxParams ajaxParams = new AjaxParams();
    Info info = Tracking.getEventInfo(context);
    ajaxParams.put("publickey", publicKey);
    ajaxParams.put("deviceid", info.getDeviceid());// deviceid
    LogMgr.showLog("json:" + ajaxParams.toJson());
    ajaxParams.put("json", ajaxParams.toJson());

    finalHttp.post(url, ajaxParams, new AjaxCallBack<Object>() {
      @Override
      public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
        callBack.onLoading(count, current);
      }

      @Override
      public void onSuccess(Object t) {
        super.onSuccess(t);
        String result = (t == null ? "" : t).toString().trim();
        LogMgr.showLog("result==>" + result);
        Serializer serializer = new Persister();
        DESBeanBase a = null;
        try {
          a = serializer.read(DESBeanBase.class, result);
        } catch (Exception e) {
          e.printStackTrace();
        }
        if (Consts.STATE_Y.equals(a.getState())) {
          String desKey = a.getParambuf().size() > 0 ? a.getParambuf().get(0).getKey() : "";
          SharepreferenceUtil.saveData(context, new String[][] {{"deskey", desKey}});
          callBack.onSuccess(new String("success"));
        } else {
          callBack.onSuccess(new String("fail"));
        }
      }

      @Override
      public void onStart() {
        // 开始http请求的时候回调
        callBack.onStart();
      }

      @Override
      public void onFailure(Throwable t, int errorNo, String strMsg) {
        super.onFailure(t, errorNo, strMsg);
        callBack.onFailure(t, strMsg);
//        Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void configTimeout(int timeout) {
    finalHttp.configTimeout(timeout);
  }

  @Override
  public void configSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
    finalHttp.configSSLSocketFactory(sslSocketFactory);
  }

  @Override
  public void configRequestExecutionRetryCount(int count) {
    finalHttp.configRequestExecutionRetryCount(count);
  }

  @Override
  public void addHeader(String header, String value) {
    finalHttp.addHeader(header, value);
  }

  private void dealPostResult(final String url, final String interfacename, final String cacheKey,
      final NetCallBack<CellComAjaxResult> callBack, AjaxParams params) {
    LogMgr.showLog("url==>"+url+"?"+params.toString());
    finalHttp.post(url, params, new AjaxCallBack<Object>() {
      @Override
      public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
        callBack.onLoading(count, current);
      }

      @Override
      public void onSuccess(Object t) {
        super.onSuccess(t);
        String result = (t == null ? "" : t).toString().trim();
        LogMgr.showLog("result==>" + result);
        String desRsaKey = SharepreferenceUtil.getDate(context, "deskey");
        // CellComAjaxResult cellComAjaxResult=new CellComAjaxResult(result);
        String sysParamFlow = Tracking.getSysParamFlow(context);
        if (sysParamFlow.equalsIgnoreCase(interfacename)) {
          CellComAjaxResult cellComAjaxResult = new CellComAjaxResult(result);
          if (cellComAjaxResult != null) {
            configCacheUtil.setUrlCache(result, cacheKey);
            callBack.onSuccess(cellComAjaxResult);
          }
        } else {
          CellComAjaxResult cellComAjaxResult = null;
          try {
//            String json = Des3.decode(result, desRsaKey);
//            LogMgr.showLog("result = " + json);
//            configCacheUtil.setUrlCache(json, cacheKey);
//            cellComAjaxResult = new CellComAjaxResult(json);
            cellComAjaxResult = new CellComAjaxResult(result);
          } catch (Exception e) {
            e.printStackTrace();
          }
          if (cellComAjaxResult != null) {
            callBack.onSuccess(cellComAjaxResult);
          }
        }
      }

      @Override
      public void onStart() {
        // 开始http请求的时候回调
        callBack.onStart();
      }

      @Override
      public void onFailure(Throwable t, int errorNo, String strMsg) {
        super.onFailure(t, errorNo, strMsg);
        callBack.onFailure(t, strMsg);
//        Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void post(String url, final NetCallBack<CellComAjaxResult> callBack) {
    StringBuffer urlSb = new StringBuffer(url);
    AjaxParams params = getPublicParams(context, url, null);
    String result = configCacheUtil.getUrlCache(urlSb.toString());
    String interfacename = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".flow"));// 接口名称
    String desRsaKey = SharepreferenceUtil.getDate(context, "deskey");
    String orginJson = params.toJson();
    String sysParamFlow = Tracking.getSysParamFlow(context);
    LogMgr.showLog("url==>"+url+"?"+params.toString());
    // 上传的参数是否加密
    if (desRsaKey.equalsIgnoreCase("")||sysParamFlow.equalsIgnoreCase(interfacename)) {
      params.put("json", orginJson);
    } else {
      try {
        String json = Des3.encode(orginJson, desRsaKey);
        params.clear();
        if (params.isExitsFile()) {
          url += "?json=" + URLEncoder.encode(json, "UTF-8");
        } else {
          params.put("json", json);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    List<CharSequence> lCharSequences = Tracking.getFlowSet(context);
    if (result != null && !lCharSequences.contains(interfacename)) {
      CellComAjaxResult cellComAjaxResult = new CellComAjaxResult(result);
      if (cellComAjaxResult != null) {
        callBack.onSuccess(cellComAjaxResult);
      }
    } else {
      dealPostResult(url, interfacename, urlSb.toString(), callBack, params);
    }
  }

  @Override
  public void post(String url, AjaxParams params, final NetCallBack<CellComAjaxResult> callBack) {
    StringBuffer urlSb = new StringBuffer(url);
    urlSb.append(params.toString());
    params = getPublicParams(context, url, params);
    String result = configCacheUtil.getUrlCache(urlSb.toString());
    String interfacename = url.substring(url.lastIndexOf("/") + 1, url.length());// 接口名称
    String desRsaKey = SharepreferenceUtil.getDate(context, "deskey");
    String orginJson = params.toJson();
    String sysParamFlow = Tracking.getSysParamFlow(context);
    // 上传的参数是否加密
//    if (desRsaKey.equalsIgnoreCase("")||sysParamFlow.equalsIgnoreCase(interfacename)) {
    if(true){
      params.put("json", orginJson);
    } else {
      try {
        String json = Des3.encode(orginJson, desRsaKey);
        LogMgr.showLog("orginJson==>"+orginJson);
        LogMgr.showLog("json==>"+json);
        
        params.clear();
        if (params.isExitsFile()) {
          url += "?json=" + URLEncoder.encode(json, "UTF-8");
        } else {
          params.put("json", json);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    List<CharSequence> lCharSequences = Tracking.getFlowSet(context);
    if (result != null && !lCharSequences.contains(interfacename)) {
      CellComAjaxResult cellComAjaxResult = new CellComAjaxResult(result);
      if (cellComAjaxResult != null) {
        callBack.onSuccess(cellComAjaxResult);
      }
    } else {
      dealPostResult(url, interfacename, urlSb.toString(), callBack, params);
    }
  }

  private void dealPostResultWithHead(final String url, final String interfacename,
      final String urlSb, Header[] headers, AjaxParams params, String contentType,
      final NetCallBack<CellComAjaxResult> callBack) {
    finalHttp.post(url, headers, params, contentType, new AjaxCallBack<Object>() {
      @Override
      public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
        callBack.onLoading(count, current);
      }

      @Override
      public void onSuccess(Object t) {
        super.onSuccess(t);
        String result = (t == null ? "" : t).toString().trim();
        LogMgr.showLog("result==>" + result);
        String desRsaKey = SharepreferenceUtil.getDate(context, "deskey");
        // CellComAjaxResult cellComAjaxResult=new CellComAjaxResult(result);
        String sysParamFlow = Tracking.getSysParamFlow(context);
        if (sysParamFlow.equalsIgnoreCase(interfacename)) {
          CellComAjaxResult cellComAjaxResult = new CellComAjaxResult(result);
          if (cellComAjaxResult != null) {
            configCacheUtil.setUrlCache(result, urlSb);
            callBack.onSuccess(cellComAjaxResult);
          }
        } else {
          CellComAjaxResult cellComAjaxResult = null;
          try {
            String json = Des3.decode(result, desRsaKey);
            LogMgr.showLog("json = " + json);
            configCacheUtil.setUrlCache(json, urlSb);
            cellComAjaxResult = new CellComAjaxResult(json);
          } catch (Exception e) {
            e.printStackTrace();
          }
          if (cellComAjaxResult != null) {
            callBack.onSuccess(cellComAjaxResult);
          }
        }
      }

      @Override
      public void onStart() {
        // 开始http请求的时候回调
        callBack.onStart();
      }

      @Override
      public void onFailure(Throwable t, int errorNo, String strMsg) {
        super.onFailure(t, errorNo, strMsg);
        callBack.onFailure(t, strMsg);
//        Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void post(String url, Header[] headers, AjaxParams params, String contentType,
      final NetCallBack<CellComAjaxResult> callBack) {
    StringBuffer urlSb = new StringBuffer(url);
    urlSb.append(params.toString());
    params = getPublicParams(context, url, params);
    String result = configCacheUtil.getUrlCache(urlSb.toString());
    String interfacename = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".flow"));// 接口名称
    String desRsaKey = SharepreferenceUtil.getDate(context, "deskey");
    String orginJson = params.toJson();
    String sysParamFlow = Tracking.getSysParamFlow(context);
    // 上传的参数是否加密
    if (desRsaKey.equalsIgnoreCase("")||sysParamFlow.equalsIgnoreCase(interfacename)) {
      params.put("json", orginJson);
    } else {
      try {
        String json = Des3.encode(orginJson, desRsaKey);
        params.clear();
        if (params.isExitsFile()) {
          url += "?json=" + URLEncoder.encode(json, "UTF-8");
        } else {
          params.put("json", json);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    List<CharSequence> lCharSequences = Tracking.getFlowSet(context);
    if (result != null && !lCharSequences.contains(interfacename)) {
      CellComAjaxResult cellComAjaxResult = new CellComAjaxResult(result);
      if (cellComAjaxResult != null) {
        callBack.onSuccess(cellComAjaxResult);
      }
    } else {
      dealPostResultWithHead(url, interfacename, urlSb.toString(), headers, params, contentType,
          callBack);
    }
  }

  @Override
  public Object postSync(String url) {
    // StringBuffer stringBuffer = new StringBuffer(url);
    AjaxParams params = getPublicParams(context, url, null);
    String desRsaKey = SharepreferenceUtil.getDate(context, "deskey");
    String orginJson = params.toJson();
    String sysParamFlow = Tracking.getSysParamFlow(context);
    String interfacename = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".flow"));// 接口名称
    // 上传的参数是否加密
    if (desRsaKey.equalsIgnoreCase("")||sysParamFlow.equalsIgnoreCase(interfacename)) {
      params.put("json", orginJson);
    } else {
      try {
        String json = Des3.encode(orginJson, desRsaKey);
        params.clear();
        if (params.isExitsFile()) {
          url += "?json=" + URLEncoder.encode(json, "UTF-8");
        } else {
          params.put("json", json);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return finalHttp.postSync(url, params);
  }

  @Override
  public Object postSync(String url, AjaxParams params) {
    // StringBuffer stringBuffer = new StringBuffer(url);
    params = getPublicParams(context, url, params);
    String desRsaKey = SharepreferenceUtil.getDate(context, "deskey");
    String orginJson = params.toJson();
    String sysParamFlow = Tracking.getSysParamFlow(context);
    String interfacename = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".flow"));// 接口名称
    // 上传的参数是否加密
    if (desRsaKey.equalsIgnoreCase("")||sysParamFlow.equalsIgnoreCase(interfacename)) {
      params.put("json", orginJson);
    } else {
      try {
        String json = Des3.encode(orginJson, desRsaKey);
        params.clear();
        if (params.isExitsFile()) {
          url += "?json=" + URLEncoder.encode(json, "UTF-8");
        } else {
          params.put("json", json);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return finalHttp.postSync(url, params);
  }

  @Override
  public Object postSync(String url, Header[] headers, AjaxParams params, String contentType) {
    // StringBuffer stringBuffer = new StringBuffer(url);
    params = getPublicParams(context, url, params);
    String desRsaKey = SharepreferenceUtil.getDate(context, "deskey");
    String orginJson = params.toJson();
    String sysParamFlow = Tracking.getSysParamFlow(context);
    String interfacename = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".flow"));// 接口名称
    // 上传的参数是否加密
    if (desRsaKey.equalsIgnoreCase("")||sysParamFlow.equalsIgnoreCase(interfacename)) {
      params.put("json", orginJson);
    } else {
      try {
        String json = Des3.encode(orginJson, desRsaKey);
        params.clear();
        if (params.isExitsFile()) {
          url += "?json=" + URLEncoder.encode(json, "UTF-8");
        } else {
          params.put("json", json);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return finalHttp.postSync(url, headers, params, contentType);
  }


  @Override
  public HttpHandler<File> download(String url, String target, final NetCallBack<File> callback) {
    AjaxParams params = getDownloadPublicParams(context, url, null);
    return finalHttp.download(url, params, target, new AjaxCallBack<File>() {
      @Override
      public void onFailure(Throwable t, int errorNo, String strMsg) {
        super.onFailure(t, errorNo, strMsg);
        callback.onFailure(t, strMsg);
        LogMgr.showLog("onFailure==>" + strMsg);
//        Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStart() {
        super.onStart();
        callback.onStart();
      }

      @Override
      public void onSuccess(File t) {
        super.onSuccess(t);
        Toast.makeText(context, t == null ? "null" : t.getAbsoluteFile().toString(),
            Toast.LENGTH_SHORT).show();
        callback.onSuccess(null);
      }

      @Override
      public void onLoading(long count, long current) {
        callback.onLoading(count, current);
        super.onLoading(count, current);
      }
    });
  }

  @Override
  public HttpHandler<File> download(String url, String target, boolean isResume,
      final NetCallBack<File> callback) {
    AjaxParams params = getDownloadPublicParams(context, url, null);
    return finalHttp.download(url, params, target, isResume, new AjaxCallBack<File>() {
      @Override
      public void onFailure(Throwable t, int errorNo, String strMsg) {
        super.onFailure(t, errorNo, strMsg);
        callback.onFailure(t, strMsg);
        // Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStart() {
        super.onStart();
        callback.onStart();
      }

      @Override
      public void onSuccess(File t) {
        super.onSuccess(t);
        callback.onSuccess(null);
      }

      @Override
      public void onLoading(long count, long current) {
        callback.onLoading(count, current);
        super.onLoading(count, current);
      }
    });
  }

  @Override
  public HttpHandler<File> download(String url, AjaxParams params, String target,
      final NetCallBack<File> callback) {
    params = getDownloadPublicParams(context, url, params);
    return finalHttp.download(url, params, target, new AjaxCallBack<File>() {
      @Override
      public void onFailure(Throwable t, int errorNo, String strMsg) {
        super.onFailure(t, errorNo, strMsg);
        callback.onFailure(t, strMsg);
        LogMgr.showLog("onFailure==>" + strMsg);
//        Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStart() {
        super.onStart();
        callback.onStart();
      }

      @Override
      public void onSuccess(File t) {
        super.onSuccess(t);
        Toast.makeText(context, t == null ? "null" : t.getAbsoluteFile().toString(),
            Toast.LENGTH_SHORT).show();
        callback.onSuccess(null);
      }

      @Override
      public void onLoading(long count, long current) {
        callback.onLoading(count, current);
        super.onLoading(count, current);
      }
    });
  }

  @Override
  public HttpHandler<File> download(String url, AjaxParams params, String target, boolean isResume,
      final NetCallBack<File> callback) {
    params = getDownloadPublicParams(context, url, params);
    return finalHttp.download(url, params, target, isResume, new AjaxCallBack<File>() {
      @Override
      public void onFailure(Throwable t, int errorNo, String strMsg) {
        super.onFailure(t, errorNo, strMsg);
        callback.onFailure(t, strMsg);
        LogMgr.showLog("onFailure==>" + strMsg);
//        Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStart() {
        super.onStart();
        callback.onStart();
      }

      @Override
      public void onSuccess(File t) {
        super.onSuccess(t);
        Toast.makeText(context, t == null ? "null" : t.getAbsoluteFile().toString(),
            Toast.LENGTH_SHORT).show();
        callback.onSuccess(null);
      }

      @Override
      public void onLoading(long count, long current) {
        callback.onLoading(count, current);
        super.onLoading(count, current);
      }
    });
  }

  private AjaxParams getDownloadPublicParams(Context context, String url, AjaxParams params) {
    Info info = Tracking.getEventInfo(context);
    params = (params == null ? new AjaxParams() : params);
    String token = cellcom.com.cn.util.SharepreferenceUtil.getDate(context, "token");
    params.put("token", token);// token
    params.put("service", info.getService());// 服务
    params.put("os", info.getOs());// 手机操作系统
    params.put("version", info.getApp_version());// 客户端版本号
    params.put("timestamp", info.getTimestamp() + "");// 时间戳
    params.put("imsi", info.getImsi());// imsi
    params.put("deviceid", info.getDeviceid());// deviceid
    LogMgr.showLog("http.url = " + (url + "?" + params.toString()));
    return params;
  }

  public static String getDate(long milliseconds) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(milliseconds);
    String month = "";
    if (cal.get(Calendar.MONTH) + 1 < 10) {
      month = "0" + cal.get(Calendar.MONTH);
    } else {
      month = cal.get(Calendar.MONTH) + "";
    }
    String day = "";
    if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
      month = "0" + cal.get(Calendar.DAY_OF_MONTH);
    } else {
      month = cal.get(Calendar.DAY_OF_MONTH) + "";
    }
    return cal.get(Calendar.YEAR) + month + day;
  }

  public static String getTime(long milliseconds) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(milliseconds);
    String month = "";
    if (cal.get(Calendar.MONTH) + 1 < 10) {
      month = "0" + cal.get(Calendar.MONTH);
    } else {
      month = cal.get(Calendar.MONTH) + "";
    }
    String day = "";
    if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
      month = "0" + cal.get(Calendar.DAY_OF_MONTH);
    } else {
      month = cal.get(Calendar.DAY_OF_MONTH) + "";
    }
    @SuppressWarnings("unused")
	String hour = "";
    if ((cal.get(Calendar.HOUR_OF_DAY) > 9)) {
      hour = cal.get(Calendar.HOUR_OF_DAY) + "";
    } else {
      hour = "0" + cal.get(Calendar.HOUR_OF_DAY);
    }
    @SuppressWarnings("unused")
	String minute =
        (cal.get(Calendar.MINUTE) > 9 ? cal.get(Calendar.MINUTE) + "" : ("0" + cal
            .get(Calendar.MINUTE)));
    @SuppressWarnings("unused")
	String second =
        (cal.get(Calendar.SECOND) > 9 ? cal.get(Calendar.SECOND) + "" : ("0" + cal
            .get(Calendar.SECOND)));
    return cal.get(Calendar.YEAR) + month + day;
  }

  public static String getAccurateTime(long milliseconds) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(milliseconds);
    String month = "";
    if (cal.get(Calendar.MONTH) + 1 < 10) {
      month = "0" + cal.get(Calendar.MONTH);
    } else {
      month = cal.get(Calendar.MONTH) + "";
    }
    String day = "";
    if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
      month = "0" + cal.get(Calendar.DAY_OF_MONTH);
    } else {
      month = cal.get(Calendar.DAY_OF_MONTH) + "";
    }
    String hour = "";
    if ((cal.get(Calendar.HOUR_OF_DAY) > 9)) {
      hour = cal.get(Calendar.HOUR_OF_DAY) + "";
    } else {
      hour = "0" + cal.get(Calendar.HOUR_OF_DAY);
    }
    String minute =
        (cal.get(Calendar.MINUTE) > 9 ? cal.get(Calendar.MINUTE) + "" : ("0" + cal
            .get(Calendar.MINUTE)));
    String second =
        (cal.get(Calendar.SECOND) > 9 ? cal.get(Calendar.SECOND) + "" : ("0" + cal
            .get(Calendar.SECOND)));
    return cal.get(Calendar.YEAR) + month + day + hour + minute + second;
  }
  /**
   * 公共参数
   * @param context
   * @param url
   * @param params
   * @return
   */
  private AjaxParams getPublicParams(Context context, String url, AjaxParams params) {
    Info info = Tracking.getEventInfo(context);
    String interfacename = url.substring(url.lastIndexOf("/") + 1, url.length());// 接口名称
    String sysParamFlow = Tracking.getSysParamFlow(context);
    String authstring = "";
    if (sysParamFlow.equalsIgnoreCase(interfacename)) {
      authstring = CellcomUtil.encodeMD5(info.getService() + info.getTimestamp() + "cellcom");
    } else {
      String key = cellcom.com.cn.util.SharepreferenceUtil.getDate(context, "authstringkey");
      authstring = CellcomUtil.encodeMD5(info.getService() + info.getTimestamp() + key);
    }

    String celltype;
    if ((android.os.Build.MODEL).contains("HUA")) {
      celltype = "HUAWEI";
    } else {
      celltype = android.os.Build.MODEL;
    }
    try {
      celltype = URLEncoder.encode(celltype, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    params = (params == null ? new AjaxParams() : params);
    params.put("celltype", celltype);
    params.put("service", info.getService());// 服务
    params.put("os", info.getOs());// 手机操作系统
    // params.put("devicename", info.getDevicename());// 手机操作系统
    params.put("version", info.getApp_version());// 客户端版本号
    params.put("osversion", info.getOs_version());// 手机系统版本号
    params.put("timestamp", info.getTimestamp() + "");// 时间戳
    params.put("authstring", authstring);// md5加密串
    params.put("imsi", info.getImsi());// imsi
    params.put("deviceid", info.getDeviceid());// deviceid
    String token = cellcom.com.cn.util.SharepreferenceUtil.getDate(context, "token");
    String userid = cellcom.com.cn.util.SharepreferenceUtil.getDate(context, "userid");
    params.put("token", token);// token
    params.put("userid", userid);// userid
    @SuppressWarnings("unused")
	String desRsaKey = SharepreferenceUtil.getDate(context, "deskey");
    LogMgr.showLog("deviceid==>" + info.getDeviceid());
    return params;
  }

  @Override
  public void get(String url, NetCallBack<CellComAjaxResult> callBack) {

  }

  @Override
  public void get(String url, AjaxParams params, NetCallBack<CellComAjaxResult> callBack) {

  }

  @Override
  public void get(String url, Header[] headers, AjaxParams params,
      NetCallBack<CellComAjaxResult> callBack) {

  }

  @Override
  public Object getSync(String url) {
    return null;
  }

  @Override
  public Object getSync(String url, AjaxParams params) {
    return null;
  }

  @Override
  public Object getSync(String url, Header[] headers, AjaxParams params) {
    return null;
  }
}
