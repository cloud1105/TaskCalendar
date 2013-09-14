package com.light.ancalendar.model;

import com.light.ancalendar.database.DatabaseOpenHelper;

/**
 * Agenda_Tag
 * @author KrisLight
 *
 */
public class TaskTag extends DataRow
{
	/** 字段索引 **/
	public static class Fid
	{
		public static final int Id = 0;
		public static final int TastId = 1;
		public static final int Year = 2;
		public static final int Month = 3;
		public static final int Day = 4;
	};
	
	//Table definition
	/** 字段定義  **/
	private final DataField[] TableDef = {
			new DataField(Fid.Id, "_id", DataField.Type.INT, true, true), //Id主鍵
			new DataField(Fid.TastId, "task_id", DataField.Type.INT, true, false), //Task Id
			new DataField(Fid.Year, "year", DataField.Type.INT, true, false), //Year
			new DataField(Fid.Month, "month", DataField.Type.INT, true, false), //Month
			new DataField(Fid.Day, "day", DataField.Type.INT, true, false), //Day
	};
	
	private long taskId;
	private int year;
	private int month;
	private int day;
	
	public TaskTag()
	{
		setTableDefinition(TableDef);
	}
	
	public TaskTag(long taskId,int year,int month,int day){
		setTableDefinition(TableDef);
		setTaskId(taskId);
		setYear(year);
		setMonth(month);
		setDay(day);
	}
	
	//setter,getter
	public long getTaskId() {
		return taskId;
	}
	
	public void setTaskId(long taskId) {
		this.taskId = taskId;
		getField(Fid.TastId).set(taskId);	
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
		getField(Fid.Year).set(year);
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
		getField(Fid.Month).set(month);
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
		getField(Fid.Day).set(day);
	}


	@Override
	public String getTableName()
	{
		return DatabaseOpenHelper.TABLE_NAME_AGENDA_TAG;
	}	
	
}
