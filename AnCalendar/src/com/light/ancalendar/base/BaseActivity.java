package com.light.ancalendar.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.light.ancalendar.activity.R;

/**
 * Activity 基類
 * @author KrisLight
 *
 */
public abstract class BaseActivity extends Activity {
	
	private static final String TAG = BaseActivity.class.getSimpleName(); 
	/**
	 * 消息類型默認Default
	 */
	public static int MSGTYPE_DEFAULT = 0;
	/**
	 * 消息類型為Info
	 */
	public static int MSGTYPE_INFO = 1;
	/**
	 * 消息類型為Warning
	 */
	public static int MSGTYPE_WARNING = 2;
	/**
	 * 消息類型為Error
	 */
	public static int MSGTYPE_ERROR = 3;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**初始化**/
	protected abstract void init();
	
	/** 初始化監聽器**/
	protected abstract void initListener();
	
	/**  得到字符串資源 **/
	public String getResStr(int id)
	{
		return this.getResources().getString(id);
	}

	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** Debug输出Log日志 **/
	protected void showLogDebug(String tag, String msg) {
		Log.d(tag, msg);
	}

	/** Error输出Log日志 **/
	protected void showLogError(String tag, String msg) {
		Log.e(tag, msg);
	}

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		if(intent.resolveActivity(getPackageManager()) != null){
	     	startActivity(intent);
		}else{
			showLogError(TAG, "there is no activity can handle this intent: "+intent.getAction().toString());
		}
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/** 通过Action跳转界面 **/
	protected void startActivity(String action) {
		Intent intent = new Intent();
		intent.setAction(action);
		if(intent.resolveActivity(getPackageManager()) != null){
	     	startActivity(intent);
		}else{
			showLogError(TAG, "there is no activity can handle this intent: "+intent.getAction().toString());
		}
	}
	
	/**含有Date通过Action跳转界面**/
	protected void startActivity(String action,Uri data) {
		Intent intent = new Intent();
		intent.setAction(action);
		intent.setData(data);
		if(intent.resolveActivity(getPackageManager()) != null){
	     	startActivity(intent);
		}else{
			showLogError(TAG, "there is no activity can handle this intent: "+intent.getAction().toString());
		}
	}

	/** 含有Bundle通过Action跳转界面 **/
	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		if(intent.resolveActivity(getPackageManager()) != null){
	     	startActivity(intent);
		}else{
			showLogError(TAG, "there is no activity can handle this intent: "+intent.getAction().toString());
		}
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}


	/** 含有标题、内容、两个按钮的对话框 **/
	protected void showAlertDialog(String title, String message,
			String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		    AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener).create();
		    alertDialog.show();
	}

	/** 含有标题、内容、图标、两个按钮的对话框 **/
	protected void showAlertDialog(String title, String message,
			int icon, String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).setIcon(icon)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener).create();
		alertDialog.show();
	}
	
	
	/**
	 * 
	 * @Function: com.light.mycal.base.BaseActivity
	 * @Description:根據不同的信息類型彈出不同等級的對話框
	 *
	 * @param i  資源id
	 * @param iMsgType 信息類型
	 *
	 * @version:v1.0
	 * @author:KrisLight
	 * @date:2013/9/4 下午4:56:39
	 *
	 * Modification History:
	 * Date         Author      Version     Description
	 * -----------------------------------------------------------------
	 * 2013/9/4    KrisLight      v1.0.0         create
	 */
	public void ShowMsgResStr(int i, int iMsgType)
	{
		String sTitle = getResStr(R.string.app_name);
		int iconId = 0;
		if (iMsgType == MSGTYPE_INFO)
		{
			sTitle = getResStr(R.string.msgTypeInfo);
			iconId = R.drawable.msgicon_info;
		}
		if (iMsgType == MSGTYPE_WARNING)
		{
			sTitle = getResStr(R.string.msgTypeWarning);
			iconId = R.drawable.msgicon_warning;
		}
		if (iMsgType == MSGTYPE_ERROR)
		{
			sTitle = getResStr(R.string.msgTypeError);
			iconId = R.drawable.msgicon_error;
		}					
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);		
		dlg.setMessage(getResStr(i));
		dlg.setPositiveButton(getResStr(R.string.msgBoxButtonOk), null);		
		dlg.setTitle(sTitle);		
		dlg.setIcon(iconId);		
		dlg.create();
		dlg.show();
	}

	/** 带有右进右出动画的退出 **/
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	/** 默认退出 **/
	protected void defaultFinish() {
		super.finish();
	}
}
