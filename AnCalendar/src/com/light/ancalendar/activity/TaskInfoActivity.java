package com.light.ancalendar.activity;

import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.light.ancalendar.adapter.MySimpleCursorAdapter;
import com.light.ancalendar.base.BaseActivity;
import com.light.ancalendar.biz.DbService;
import com.light.ancalendar.model.Task;
import com.light.ancalendar.model.Task.Fid;

/**
 *  顯示當天的所有Task展示成列表
 *  @author KrisLight
 *
 */
public class TaskInfoActivity extends BaseActivity {

	public final static String EDIT = "edit";
	public final static String ROW_ID = "rowId";
	private ListView listView;
	private SimpleCursorAdapter adapter;
	private Long mDate;	//需要查詢的日期
	private Cursor cursor;
	private DbService service;
	private Calendar selectDay;//存儲傳過來的日期
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		startActivity(CalActivity.class);
	}
	
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
		setContentView(R.layout.activity_task_info);
		init();
		initListener();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.add_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnAdd:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putLong(CalActivity.SELECT_DATE,selectDay.getTimeInMillis());
			intent.putExtra(CalActivity.BUNDLE, bundle);
			intent.setClass(this, TaskActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
			break;
		}
		return true;
	}

	
	@SuppressWarnings("deprecation")
	@Override
	protected void init() {
		if(service == null){
		  service = DbService.getInstance(this);
		}
		Intent intent = getIntent();
		if(intent != null && intent.getExtras() != null){
			Bundle data = intent.getExtras();
			mDate = data.getLong(CalActivity.SELECT_DATE);
			Date date = new Date(mDate);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			selectDay = c;
			//取得傳過來的日期,查詢日期對應的所有Task
			cursor = service.loadTaskByStartTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));
			
		}else{
			showLogError("Light", "Can't get the select date");
		}
		listView = (ListView) findViewById(R.id.listView);
		Task task = new Task();
		adapter = new MySimpleCursorAdapter(
				     this,
				     R.layout.activity_task_info_item,
				     cursor,
				     new String[]{task.fieldName(Fid.Subject),task.fieldName(Fid.StartDate)},
				     new int[]{R.id.taskSubject,R.id.taskStartTime});
		listView.setAdapter(adapter);
		startManagingCursor(cursor);
	}

	@Override
	protected void initListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//單擊顯示Task詳情,可進行更新操作
				//將Task的Id傳過去
				Bundle data = new Bundle();
				data.putBoolean(EDIT, true);
				Log.i("Light", "傳過去的ID"+id);
				data.putLong(ROW_ID, id);
				Intent intent = new Intent();
				intent.putExtra(CalActivity.BUNDLE, data);
				intent.setClass(TaskInfoActivity.this, TaskActivity.class);
		    	startActivity(intent);
			}
		});
		
	}
}
