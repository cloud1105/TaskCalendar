package com.light.ancalendar.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.light.ancalendar.activity.R;
import com.light.ancalendar.biz.DbService;
import com.light.ancalendar.util.Utils;


public class CalendarAdapter extends BaseAdapter {
	
	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
	private Calendar calSelected = Calendar.getInstance(); // 选择的日历
	private Calendar calToday = Calendar.getInstance(); // 今日
	private int iMonthViewCurrentMonth = 0; // 当前视图月份
	private DbService service;
	private Context mContext;
	Resources resources;
	//當前月份顯示視圖的所有日期集合
	ArrayList<java.util.Date> titles;
	
	public ArrayList<java.util.Date> getTitles() {
		return titles;
	}

	public void setSelectedDate(Calendar cal)
	{
		calSelected=cal;
	}
	

    /** 計算當前月份視圖的第一天 **/
	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月
		// 星期一是2 星期天是1  填充剩余天数
		int iDay = 0;
		//一周的第一天 默認設置是週一
		int iStartDay = Utils.iFirstDayOfWeek;
		//一周的第一天為週一
		if (iStartDay == Calendar.MONDAY) {
			//與週一求差值
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			//小於0表示相差一個星期 減6天
			if (iDay < 0)
				iDay = 6;
		}
		//一周的第一天為週日
		if (iStartDay == Calendar.SUNDAY) {
			//與週日求差值
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			//小於0表示相差一個星期 減6天
			if (iDay < 0)
				iDay = 6;
		}
		//換成第一周週一對應的日期 
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
		// 默認週一,減一后周日對應的日期變為月的第一周的第一天
		calStartDate.add(Calendar.DAY_OF_MONTH, -1);
	}
	
	
	/**
	 *  存放當前顯示的月份的日期
	 */
	private ArrayList<java.util.Date> getDates() {
		//計算當前月份第一天
		UpdateStartDateForMonth();
		ArrayList<java.util.Date> alArrayList = new ArrayList<java.util.Date>();
		// 從第一天開始往後的一共42個日期  
		// add(Calendar.DAY_OF_MONTH, 1) 備註:這個方法超出這個月的部份會自動跳至下個月
		for (int i = 1; i <= 42; i++) {
			alArrayList.add(calStartDate.getTime());
			calStartDate.add(Calendar.DAY_OF_MONTH, 1);
		}
		return alArrayList;
	}
	

	// construct
	public CalendarAdapter(Context context,Calendar cal,List<Long> list) {
		this.calStartDate=cal;
		this.mContext = context;
		this.resources = mContext.getResources();
		//計算本月第一天并得到需要顯示的所有日期集合放入titles
		this.titles = getDates();
	}
	
	
	@Override
	public int getCount() {
		return titles.size();
	}

	@Override
	public Object getItem(int position) {
		return titles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Date myDate = (Date) getItem(position);
		
		//存儲position位置對應日期的臨時變量
		Calendar calCalendar = Calendar.getInstance();
		calCalendar.setTime(myDate);

		//對應月份
		final int iMonth = calCalendar.get(Calendar.MONTH);
		//對應星期
		final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);

		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.calendar_item, null);
		}
		
		FrameLayout lv = (FrameLayout) convertView.findViewById(R.id.itemLayout);
		TextView tvToDay = (TextView) convertView.findViewById(R.id.tvToDay);
		TextView tvDay = (TextView) convertView.findViewById(R.id.tvDay);
		ImageView ivMark = (ImageView) convertView.findViewById(R.id.taskMark);
		
		// 判断週末 設置顏色
		if (iDay == 7) {
			// 周六
			lv.setBackgroundColor(resources.getColor(R.color.text_6));
		}else if (iDay == 1) {
			// 周日
			lv.setBackgroundColor(resources.getColor(R.color.text_7));
		}else{
			//工作日
			lv.setBackgroundColor(resources.getColor(R.color.white));
		}
		
		if (Utils.isSameDate(calToday.getTime(), myDate)) {
			// 若当前日期是今天 設置標示Today
			lv.setBackgroundColor(resources.getColor(R.color.event_center));
			tvToDay.setText("TODAY!");
		}

		// 设置背景颜色
		if (Utils.isSameDate(calSelected.getTime(), myDate)) {
			// 选择的日期
			lv.setBackgroundColor(resources.getColor(R.color.selection));
		} else {
			if (Utils.isSameDate(calToday.getTime(), myDate)) {
				// 選擇的是当前日期
				lv.setBackgroundColor(resources.getColor(R.color.calendar_zhe_day));
			}
		}
		
		//年,月,日
		int year = calCalendar.get(Calendar.YEAR);
		int month = calCalendar.get(Calendar.MONTH);
		int day = calCalendar.get(Calendar.DAY_OF_MONTH);
		
		if(service == null){
			service = DbService.getInstance(mContext);
		}
		
		//判斷是否有Task標記
		if(service.hasTask(year, month, day)){
		    ivMark.setVisibility(View.VISIBLE);
		}else{
			ivMark.setVisibility(View.GONE);
		}
		
		if (iMonth == iMonthViewCurrentMonth) {
			//是当前月份的日期 設置文字顏色黑色 
			tvToDay.setTextColor(resources.getColor(R.color.ToDayText));
			tvDay.setTextColor(resources.getColor(R.color.Text));
		} else {
			//填充的日期設置文字顏色灰色
			tvDay.setTextColor(resources.getColor(R.color.noMonth));
			tvToDay.setTextColor(resources.getColor(R.color.noMonth));
		}
		
		//設置日期
		tvDay.setText(String.valueOf(day));
		return convertView;
	}

}
