package com.light.ancalendar.activity;

import java.util.Calendar;
import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import com.light.ancalendar.adapter.CalendarAdapter;
import com.light.ancalendar.base.BaseActivity;
import com.light.ancalendar.biz.DbService;
import com.light.ancalendar.util.Utils;

public class CalActivity extends BaseActivity {

	private static final String TAG = CalActivity.class.getSimpleName();
	
	//msg.what
	private static final int FIND_DATE_TASK = 0x1001;
	
	//Bundle中的Key
	//選擇的日期
	public static final String SELECT_DATE = "selectDate";
	//選擇日期對應的TaskId的集合
	public static final String TASK_ID_LIST = "taskIdList";
	//當前日曆中含有Task的List
	public static final String TASK_LIST = "taskList";
	//Bundle
	public static final String BUNDLE = "bundle";
	
	private ImageButton preBtn,nextBtn;
	private TextView todayTv;
	private GridView curMonthGv;// 当前月
	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历開始日期
	private Calendar calSelected = Calendar.getInstance(); // 重新选择的日历日期
	private Calendar calToday = Calendar.getInstance(); // 今日的日期
	private int iMonthViewCurrentMonth = 0; // 当前视图月
	private int iMonthViewCurrentYear = 0; // 当前视图年
	private DbService service;//Dao
	private boolean isDateHasTask = false;
	
	//Calendar適配器
	private CalendarAdapter curGvAdapter;
	public Handler handler  = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Bundle data; 
			switch (msg.what) {
			case FIND_DATE_TASK:
				Log.i(TAG, "--------查詢Task信息");
				//如果查到當天日期有Task,則將當天日期設定的所有Task查詢顯示出來執行Update
				//否則跳轉到新建New Task頁面
				data = msg.getData();
				if(isDateHasTask){
					Log.i(TAG, "--------有Task");
					infoTask(data);
				}else{
					Log.i(TAG, "--------無Task");
					addTask(data);
				}
				break;
			}
		}

		private void infoTask(Bundle data) {
			startActivity(TaskInfoActivity.class, data);
		};
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		initDate();
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (service != null) {
			service.closeDataBase();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cal);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                .penaltyLog().penaltyDeath().build());
		init();
		initListener();
		initDate();
	}

	@Override
	protected void init() {
		preBtn = (ImageButton) findViewById(R.id.btnPrev);
		nextBtn = (ImageButton) findViewById(R.id.btnNext);
		todayTv = (TextView) findViewById(R.id.textToday);
		curMonthGv = (GridView) findViewById(R.id.gridView);
		//計算第一天
		UpdateStartDateForMonth();
		//初始化開始日期
		getCalendarStartDate();
		//日曆下面的LinearLayout
		CreateGirdView();
	}
	
	@Override
	protected void initListener() {
		preBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//上一月視圖
				setPrevViewItem();
				CreateGirdView();
				
			}
		});
		nextBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
               //下一月視圖	
				setNextViewItem();
				CreateGirdView();
			}
		});
		
		todayTv.setOnClickListener(new OnClickListener() {
			//本月視圖
			@Override
			public void onClick(View v) {
				setToDayViewItem();
				CreateGirdView();
			}
		});
	}
	
	
	/**
	 * 
	 * @Function: com.light.mycal.CalActivity.initDate
	 * @Description:重新加載日曆視圖數據
	 * @version: v1.0
	 * @author: KrisLight
	 * @date: 2013/9/9 下午2:34:03
	 *
	 * Modification History:
	 * Date         Author      Version     Description
	 * -----------------------------------------------------------------
	 * 2013/9/9    KrisLight      v1.0.0         create
	 */
	private void initDate(){
		if(curGvAdapter !=null){
		 curGvAdapter.notifyDataSetChanged();
		}
	}
	
	
	/**
	 * 
	 * @Function: com.light.mycal.CalActivity.CreateGirdView
	 * @Description: 創建日曆視圖
	 * @version: v1.0
	 * @author: KrisLight
	 * @date: 2013/9/6 上午11:25:38
	 *
	 * Modification History:
	 * Date         Author      Version     Description
	 * -----------------------------------------------------------------
	 * 2013/9/6    KrisLight      v1.0.0         create
	 */
	private void CreateGirdView() {

		Calendar tempSelected = Calendar.getInstance(); // 临时
		
		//開始日期
		tempSelected.setTime(calStartDate.getTime());

		curGvAdapter = new CalendarAdapter(this, tempSelected ,null);
		curMonthGv.setAdapter(curGvAdapter);
		curMonthGv.setOnItemClickListener(new CalendarItemClickListener());

		String s = calStartDate.get(Calendar.YEAR)
				+ "-"
				+ Utils.leftPad2Zero(calStartDate.get(Calendar.MONTH) + 1);

		//標題顯示當前月份
		todayTv.setText(s);
	}
	
	/**
	 *   得到開始日期
	 */
	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(Utils.iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			//當前日曆
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(Utils.iFirstDayOfWeek);
		} else {
			//跳轉到指定的日曆
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(Utils.iFirstDayOfWeek);
		}

		return calStartDate;
	}
	
	
	/** 上一个月 **/
	private void setPrevViewItem() {
		Log.i("Light", "----------------------Prev----------------");
		iMonthViewCurrentMonth--;// 当前选择月--
		// 如果当前月为负数的话显示上一年
		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); // 设置月
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear); // 设置年
		curGvAdapter.notifyDataSetChanged();
	}

	/** 当月 **/
	private void setToDayViewItem() {
		Log.i("Light", "-----------------Today---------------------------");
		
		iMonthViewCurrentMonth = calToday.get(Calendar.MONTH);
		iMonthViewCurrentYear = calToday.get(Calendar.YEAR);
		
		calSelected.setTimeInMillis(calToday.getTimeInMillis());
		calSelected.setFirstDayOfWeek(Utils.iFirstDayOfWeek);
		
		calStartDate.setTimeInMillis(calToday.getTimeInMillis());
		calStartDate.setFirstDayOfWeek(Utils.iFirstDayOfWeek);
		curGvAdapter.notifyDataSetChanged();
	}

	/** 下一个月 **/
	private void setNextViewItem() {
		Log.i("Light", "-----------------Next---------------------------");
		//加一個月
		iMonthViewCurrentMonth++;
		
		//如果已經是12月 加一年
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}
		
		//刷新
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		curGvAdapter.notifyDataSetChanged();
	}
	
	
	/**
	 * 
	 * @Function: com.light.mycal.CalActivity.UpdateStartDateForMonth
	 * @Description:  計算當前日曆顯示視圖的第一天
	 *
	 * @version: v1.0
	 * @author: KrisLight
	 * @date: 2013/9/6 上午11:32:03
	 *
	 * Modification History:
	 * Date         Author      Version     Description
	 * -----------------------------------------------------------------
	 * 2013/9/6    KrisLight      v1.0.0         create
	 */
	private void UpdateStartDateForMonth() {
		// 设置成当月第一天
		calStartDate.set(Calendar.DATE, 1); 
		// 得到当前日历显示的月
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		// 得到当前日历显示的年
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
		// 星期一是2 星期天是1  填充前面的剩余天数
		int iDay = 0;
		int iStartDay = Utils.iFirstDayOfWeek;
		//開始日期和一周的開始周別求差值,得到這個月開始第一周前面填滿需要補齊的天數
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		//換成週一對應的日期 
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
		// 默認週一,減一后周日日期變為月的第一位
		calStartDate.add(Calendar.DAY_OF_MONTH, -1);
	}
	
	/** 查看選擇的日期Task事項 **/
	private class CalendarItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//通过日期查询这一天是否被标记，如果标记了日程就查询出这天的所有Task信息 ,如果沒有標記就新建一個Task
			final Date curDate = (Date)curGvAdapter.getItem(position);
            new Thread(new Runnable() {
				@Override
				public void run() {
					if(service == null){
						service = DbService.getInstance(CalActivity.this);
					}
					Calendar cal = Calendar.getInstance();
					cal.setTime(curDate);
					Log.i("Light", "當前選擇時間:"+cal.getTimeInMillis());
					isDateHasTask = service.hasTask(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
					Message message = handler.obtainMessage(FIND_DATE_TASK);
					Bundle bundle = new Bundle();
					bundle.putLong(SELECT_DATE,cal.getTimeInMillis());
					message.setData(bundle);
					handler.sendMessage(message);
				}
			}).start();
		}
	}
	
	//add task
	private void addTask(Bundle b){
		Intent intent = new Intent();
		intent.putExtra(BUNDLE, b);
		intent.setClass(this, TaskActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}
	
}
