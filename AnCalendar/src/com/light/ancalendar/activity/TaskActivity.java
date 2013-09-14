package com.light.ancalendar.activity;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.light.ancalendar.base.BaseActivity;
import com.light.ancalendar.biz.DbService;
import com.light.ancalendar.model.Task;
import com.light.ancalendar.model.TaskTag;
import com.light.ancalendar.util.Utils;

/**
 * 新增Task頁面 ,新增完畢 返回上層日曆視圖,刷新頁面 如果是從TaskInfo跳轉過來查詢Task信息執行Update
 * 
 * @author KrisLight
 * 
 */
public class TaskActivity extends BaseActivity {

	private Intent intent;
	private EditText tvSubject, tvContent;
	private CheckBox cbIsAlarm;
	private Button btnStartDate, btnStartTime;
	private ImageButton ibSave, ibDelete;
	private Long mDate;
	private static int year = -1;
	private static int monthOfYear = -1;
	private static int dayOfMonth = -1;
	private static int hourOfDay = -1;
	private static int minute = -1;
	// 緩存傳過來的日期
	private static Date mStartDate;
	private static int mYear;
	private static int mMonthOfYear;
	private static int mDayOfMonth;
	private Long rowId;
	private Long tagRowId;
	private DbService service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);
		init();
		initListener();
	}

	@Override
	protected void init() {
		if(service == null){
			service = DbService.getInstance(this); 
		}
		tvSubject = (EditText) findViewById(R.id.edTaskSubject);
		tvContent = (EditText) findViewById(R.id.edTaskContent);
		cbIsAlarm = (CheckBox) findViewById(R.id.chkTaskAlarm);
		btnStartDate = (Button) findViewById(R.id.btnTaskStartDate);
		btnStartTime = (Button) findViewById(R.id.btnTaskStartTime);
		ibSave = (ImageButton) findViewById(R.id.btnTaskSave);
		ibDelete = (ImageButton) findViewById(R.id.btnTaskDelete);
		intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			Bundle data = intent.getBundleExtra(CalActivity.BUNDLE);
			mDate = data.getLong(CalActivity.SELECT_DATE);
			Log.i("Light","Date"+mDate);
			if (data.getBoolean(TaskInfoActivity.EDIT, false)) {
				//Update動作 對原有Task更新操作 設置刪除按鈕可見 得到rowId
				ibDelete.setVisibility(View.VISIBLE);
				rowId = data.getLong(TaskInfoActivity.ROW_ID);
				setTaskInfoById(rowId);
			}
		}

		// 初始化時間控件
		Calendar cal = Calendar.getInstance();
		if (hourOfDay == -1 && minute == -1) {
			hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
			minute = cal.get(Calendar.MINUTE);
		}

		// 初始化日期控件
		if (mDate != 0) {
			Date date = new Date(mDate);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			year = c.get(Calendar.YEAR);
			monthOfYear = c.get(Calendar.MONTH);
			dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
			mStartDate = date;
			mYear = year;
			mMonthOfYear = monthOfYear;
			mDayOfMonth = dayOfMonth;
			StringBuffer sb = new StringBuffer();
			// yyyy-MM-dd
			sb.append(year).append("-").append(monthOfYear + 1).append("-")
					.append(dayOfMonth);
			btnStartDate.setText(sb.toString());
		}

	}

	@Override
	protected void initListener() {
		// 開始日期選擇
		btnStartDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(TaskActivity.this,
						new OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								TaskActivity.year = year;
								TaskActivity.monthOfYear = monthOfYear;
								TaskActivity.dayOfMonth = dayOfMonth;
								StringBuffer sb = new StringBuffer();
								// yyyy-MM-dd
								sb.append(year).append("-")
										.append(monthOfYear + 1).append("-")
										.append(dayOfMonth);
								btnStartDate.setText(sb.toString());
							}
						}, year, monthOfYear, dayOfMonth).show();
			}
		});

		// 開始時間選擇
		btnStartTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new TimePickerDialog(TaskActivity.this,
						new OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								TaskActivity.hourOfDay = hourOfDay;
								TaskActivity.minute = minute;
								StringBuffer sb = new StringBuffer();
								String h = String.valueOf(hourOfDay);
								if(h.length() == 1){
									h = "0"+h;
								}
								// HH:mm
								sb.append(h).append(":").append(minute);
								btnStartTime.setText(sb.toString());
							}
						}, hourOfDay, minute, true).show();
			}
		});

		ibSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validate()) {
					if(rowId !=null){
						Log.i("Light", "updateRowId"+rowId);
						Date startDate = getStartDate().getTime();
						if(Utils.isSameDate(mStartDate, startDate) ){
							//只更新Task
							Task task = createTask();
							if(service.updateValues(rowId, task)!=0){
								showShortToast(R.string.updateSuccess);
							}else{
								showShortToast(R.string.updateFail);
							}
						}else{
							//更新Task和Tag
							updateDate(rowId);
						}
					}else{
						Log.i("Light", "save"+rowId);
						// 新增
						saveDate();
					}
					defaultFinish();
				} else {
					ShowMsgResStr(R.string.subject_or_content_is_empty,
							BaseActivity.MSGTYPE_INFO);
				}
			}
		});

		ibDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlertDialog("刪除提示", "確認刪除？", "是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//刪除數據
						deleteDate(rowId);
						
						defaultFinish();
					}
				}, "否", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
			}
		});
	}

	private void saveDate() {
		// 保存
		Task task = createTask();
		TaskTag tag = createTag();
		if(service.insertTaskAndTaskTag(task, tag)){
			showShortToast(R.string.saveSuccess);
		}else{
			showShortToast(R.string.saveFail);
		}
	}
	
	private void updateDate(long taskId) {
		// 更新
		Task task = createTask();
		TaskTag tag = createTag();
		if(service.updateTaskAndTag(taskId, task, tag)){
			showShortToast(R.string.updateSuccess);
		}else{
			showShortToast(R.string.updateFail);
		}
	}

	// 刪除
	private void deleteDate(long taskId) {
		Task task = new Task();
		TaskTag tag = new TaskTag();
		if(service.deleteTaskAndTaskTag(taskId, task, tag)){
			showShortToast(R.string.deleteSuccess);
		}else{
			showShortToast(R.string.deleteFail);
		}
	}
	
	// 用選擇的日期和時間構建StartDate
	private Calendar getStartDate() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, monthOfYear);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		return c;
	}
	
	private Task createTask(){
		Task task = new Task();
		task.setSubject(tvSubject.getText().toString());
		task.setContent(tvContent.getText().toString());
		task.setAlarm(cbIsAlarm.isChecked());
		Calendar startDate = getStartDate();
		task.setStartDate(startDate);
		return task;
	}
	
	private TaskTag createTag(){
		TaskTag tag = new TaskTag();
		if(rowId != null){
		 tag.setTaskId(rowId);
		}
		tag.setYear(year);
		tag.setMonth(monthOfYear);
		tag.setDay(dayOfMonth);
		return tag;
	}
	
	private void setStartTime(Calendar cal){
		year = cal.get(Calendar.YEAR);
		monthOfYear = cal.get(Calendar.MONTH);
		dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
	}
	

	/** 驗證主題和內容是否為空 **/
	private boolean validate() {
		if (tvSubject.getText().length() > 0
				&& tvContent.getText().length() > 0) {
			return true;
		}
		return false;
	}

	private void setTaskInfoById(long id){
		Task task = new Task();
		if(service == null){
		  service = DbService.getInstance(this);
		}
		Cursor cursor = service.loadDataRowById(id, task);
		//將值放入字段中
		task.getValuesFromCursor(cursor);
		//字段值存入dr的屬性中
		task.getValuesFromDataRow();
		tvSubject.setText(task.getSubject());
		tvContent.setText(task.getContent());
		cbIsAlarm.setChecked(task.getAlarm());
		mStartDate = task.getStartDate().getTime();
		Calendar c = task.getStartDate();
		setStartTime(c);
		String taskStartDate = Utils.getShortDate(c);
		String taskStartTime = Utils.getLongTime(c, true);
		btnStartDate.setText(taskStartDate);
		btnStartTime.setText(taskStartTime);
		cursor.close();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (service != null) {
			service.closeDataBase();
		}
	}

}
