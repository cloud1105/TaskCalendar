
package com.light.ancalendar.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.text.format.Time;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;


/**
 * 工具類
 * @author KrisLight
 *
 */
public class Utils 
{
	/**
	 * 透明度動畫變化持續時間
	 */
	public static int ANIM_ALPHA_DURATION = 100;
	
	/**
	 * 平移動畫持續時間
	 */
	public static int ANIM_TRANSLATE_DURATION = 30;	
	
	/** 一周的第一天為星期一  對應數字2**/
	public static int iFirstDayOfWeek = Calendar.MONDAY;
	
	
	/**
	 *  包括年,月,日的日期格式   yyyy-MM-dd
	 */
	private static SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Sql中的日期格式   yyyy-MM-dd kk:mm.ss
	 */
	private static SimpleDateFormat dateFormatSql = new SimpleDateFormat("yyyy-MM-dd kk:mm.ss");
	

	//UTILS
	private Utils(){
	}
	

	
	/**
	 * 得到 yyyy-MM-dd 格式字符串 
	 */
	public static String getShortDate(Calendar date)
	{
		return dateFormatShort.format(date.getTime());
	}
	
	/**
	 * 
	 * 將日期轉換成SQL中需要的日期格式 
	 * 形如yyyy-MM-dd kk:mm.ss
	 *
	 */
	public static String dateToSqlStr(Calendar date)
	{
		return dateFormatSql.format(date.getTime());
	}
	
	
	
	
	/**
	 *  得到時和分 
	 */
	public static String getLongTime(Calendar date, boolean b24HourMode)
	{
		String s = "";
		if (b24HourMode)
		{
			//k: 24 小时制的小时     M: 小时中的分钟
			s = String.format("%tk:%tM", date, date);
		} else {			
			//l: 12 小时制的小时     M: 小时中的分钟
			if (date.get(Calendar.AM_PM) == 0) //AM		
				s = String.format("%tl:%tM am", date, date, date.get(Calendar.AM_PM));
			if (date.get(Calendar.AM_PM) == 1) //PM						
				s = String.format("%tl:%tM pm", date, date, date.get(Calendar.AM_PM));
		}
		return s; 
	}
	
	/**
	 *  將小時和分鐘轉換成秒
	 */
	public static int getTimeAsSeconds(Calendar date)
	{
		return (date.get(Calendar.HOUR_OF_DAY) * 3600) +
			date.get(Calendar.MINUTE) * 60;
	}

	/**
	 *  清除日期
	 */
	public static void clearCalendarTime(Calendar cal)
	{
		cal.clear(Calendar.MILLISECOND);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 *  判斷兩個日期是否相等
	 */
	public static boolean yearDaysEqual(Calendar calDate, Calendar calDateTo)
	{
		if (calDate.get(Calendar.YEAR) == calDateTo.get(Calendar.YEAR))
			if (calDate.get(Calendar.MONTH) == calDateTo.get(Calendar.MONTH))
				if (calDate.get(Calendar.DAY_OF_MONTH) == calDateTo.get(Calendar.DAY_OF_MONTH))
					return true;
		return false;
	}

	/**
	 *  判斷前一個日期是否大於后一個
	 */
	public static boolean yearDaysGreater(Calendar calDate, Calendar calDateTo)
	{
		if (calDate.get(Calendar.YEAR) >= calDateTo.get(Calendar.YEAR))
			if (calDate.get(Calendar.MONTH) >= calDateTo.get(Calendar.MONTH))
				if (calDate.get(Calendar.DAY_OF_MONTH) >= calDateTo.get(Calendar.DAY_OF_MONTH))
					return true;
		return false;
	}
	

	
	/**
	 * @Function: com.light.mycal.util.Utils.join
	 * @Description: 集合中的元素以指定分隔符隔開
	 * @param list   集合List
	 * @param delim  分隔符
	 * @return  用分隔符隔開的集合元素字符串
	 *
	 * @version: v1.0
	 * @author: KrisLight
	 * @date: 2013/9/5 上午10:20:51
	 *
	 * Modification History:
	 * Date         Author      Version     Description
	 * -----------------------------------------------------------------
	 * 2013/9/5    KrisLight      v1.0.0         create
	 */
	public static String join(List<String> list, String delim) {
	    StringBuilder buf = new StringBuilder();
	    int num = list.size();
	    for (int i = 0; i < num; i++) {
	        if (i != 0){
	            buf.append(delim);
	        }
	        buf.append((String) list.get(i));
	    }
	    return buf.toString();
	}
	
	/**
	 *  開始alpha動畫
	 */
  public static void startAlphaAnimIn(View view)
  {
		AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
		anim.setDuration(ANIM_ALPHA_DURATION);
		anim.startNow();
		view.startAnimation(anim);
  }
  
	/**
	 *  開始translate動畫
	 */
  public static void startTranslateAnimIn(View view)
  {
		TranslateAnimation anim = new TranslateAnimation(0, 0, - view.getHeight(), 0);
		anim.setDuration(ANIM_TRANSLATE_DURATION);
		anim.startNow();
		view.startAnimation(anim);
  }  

	
	/**
	 * 判斷日期是否是今天
	 * @author: KrisLight
	 * @param date
	 * @return
	 */
	public static boolean isToday(Date date){
		return isSameDate(new Date(), date);
	}
	
	/**
	 * 判斷2個日期是否相等
	 * @author: KrisLight
	 * @param baseDate
	 * @param thenDate
	 * @return
	 */
	public static boolean isSameDate(Date baseDate, Date thenDate){
		Time time = new Time();
        time.set(thenDate.getTime());

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(baseDate.getTime());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
	}
	
	
	/**
	 * 設定月份格式兩位顯示 如 01,02,·····10
	 */
	public static String leftPad2Zero(int str) {
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		return format.format(str);

	}
	
}
