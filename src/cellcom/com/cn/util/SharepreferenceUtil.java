package cellcom.com.cn.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
/**
 * 
 * @author Administrator mw
 *
 */
public class SharepreferenceUtil {

	/**
	 * 从本地文件中读取数据
	 * 
	 * @param cxt 上下文对象
	 * @param name 获取数据的key
	 * @return value
	 */
	@SuppressWarnings("static-access")
	public static String getDate(Context cxt, String name) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(cxt);
		sp = cxt.getSharedPreferences(Consts.cellcomnetXmlName, cxt.MODE_PRIVATE);
		String value = sp.getString(name, "");
		return value;
	}
	
	/**
	 *  保存数据到本地文件
	 *  
	 * @param cxt 上下文对象
	 * @param str_key_values 保存数据的键值对
	 */
	public static void saveData(Context cxt, String[][] str_key_values) {
		@SuppressWarnings("static-access")
		SharedPreferences sp = cxt.getSharedPreferences(Consts.cellcomnetXmlName, cxt.MODE_PRIVATE);
		if (str_key_values != null && str_key_values.length > 0) {
			if (str_key_values[0].length != 2)
				throw new IllegalArgumentException("参数格式错误,key-value");
			Editor editor = sp.edit();
			for (String[] keyValue : str_key_values) {
				LogMgr.showLog("savedate param=" + keyValue[0] + " value:"
						+ keyValue[1]);
				editor.putString(keyValue[0], keyValue[1]);
			}
			editor.commit();
		}
	}
	public static void write(Context context, String fileName, String k, int v) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putInt(k, v);
        editor.commit();
    }
	/**
	 * 获取数据
	 * @param cxt
	 * @param name
	 * @param xmlname
	 * @return
	 */
	public static String getDate(Context cxt, String name, String xmlname) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(cxt);
		if (xmlname != null || !"".equalsIgnoreCase(xmlname)) {
			sp = cxt.getSharedPreferences(xmlname, cxt.MODE_PRIVATE);
		} else {
			sp = PreferenceManager.getDefaultSharedPreferences(cxt);
		}
		String value = sp.getString(name, "");
		return value;
	}
    public static void write(Context context, String fileName, String k,
            boolean v) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putBoolean(k, v);
        editor.commit();
    }

    public static void write(Context context, String fileName, String k,
            String v) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putString(k, v);
        editor.commit();
    }
    public static void write(Context context, String fileName, String k,
            long v) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putLong(k, v);
        editor.commit();
    }
    public static int readInt(Context context, String fileName, String k) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return preference.getInt(k, 0);
    }

    public static int readInt(Context context, String fileName, String k,
            int defv) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return preference.getInt(k, defv);
    }
    
    public static long readLong(Context context, String fileName, String k,
            int defv) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return preference.getLong(k, defv);
    }

    public static boolean readBoolean(Context context, String fileName, String k) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return preference.getBoolean(k, false);
    }

    public static boolean readBoolean(Context context, String fileName,
            String k, boolean defBool) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return preference.getBoolean(k, defBool);
    }

    public static String readString(Context context, String fileName, String k) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return preference.getString(k, null);
    }

    public static String readString(Context context, String fileName, String k,
            String defV) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return preference.getString(k, defV);
    }

    public static void remove(Context context, String fileName, String k) {
        SharedPreferences preference = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.remove(k);
        editor.commit();
    }

    public static void clean(Context cxt, String fileName) {
        SharedPreferences preference = cxt.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.clear();
        editor.commit();
    }
}
