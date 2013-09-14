package com.light.ancalendar.adapter;

import java.util.Calendar;
import java.util.Date;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.light.ancalendar.activity.R;
import com.light.ancalendar.model.Task;
import com.light.ancalendar.model.Task.Fid;
import com.light.ancalendar.util.Utils;

public class MySimpleCursorAdapter extends SimpleCursorAdapter {

	private Cursor mCursor;
	private Context mContext;

	public MySimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.mCursor = c;
		this.mContext = context;
	}
    
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView subjectView;
		TextView dateView;
		if(view==null)
		{
		  view =  LayoutInflater.from(mContext).inflate(R.layout.activity_task_info_item,null);
		}
		subjectView = (TextView) view.findViewById(R.id.taskSubject);
	    dateView = (TextView) view.findViewById(R.id.taskStartTime);
		String subject = mCursor.getString(mCursor.getColumnIndex("subject"));
		long startDate = mCursor.getLong(mCursor.getColumnIndex("start_date"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(startDate));
		String startTime = Utils.getLongTime(cal, true);
		subjectView.setText(subject);
		dateView.setText(startTime);
	}
	
}
