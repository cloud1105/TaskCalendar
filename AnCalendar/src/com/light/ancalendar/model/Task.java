package com.light.ancalendar.model;

import java.util.Calendar;
import com.light.ancalendar.database.DatabaseOpenHelper;

/**
 * Agenda
 * @author KrisLight
 *
 */
public class Task extends DataRow
{
	/** 字段索引 **/
	public static class Fid
	{
		public static final int Id = 0;
		public static final int Subject = 1;
		public static final int Content = 2;
		public static final int StartDate = 3;
		public static final int Alarm = 4;
	};
	
	//Table definition
	/** 字段定義  **/
	private final DataField[] TableDef = {
			new DataField(Fid.Id, "_id", DataField.Type.INT, true, true), //Id主鍵
			new DataField(Fid.Subject, "subject", DataField.Type.TEXT, true, false), //主題
			new DataField(Fid.Content, "content", DataField.Type.TEXT, true, false), //內容
			new DataField(Fid.StartDate, "start_date", DataField.Type.INT, true, false), //開始日期
			new DataField(Fid.Alarm, "alarm", DataField.Type.BOOL, true, false), //是否鬧鈴
	};
	
	
	//存取字段值的變量
	private String sSubject ;
	private String sContent ;
	private Calendar calDateStart = Calendar.getInstance();
	private boolean bAlarm = true;
	
	
	//methods
	public Task()
	{
		setTableDefinition(TableDef);
	}
	
	public Task(String subject,String content,Calendar cal,boolean isAlarm)
	{
		setTableDefinition(TableDef);
		setSubject(subject);
		setContent(content);
		setStartDate(cal);
		setAlarm(isAlarm);
	}
	
	// setters
	public void setSubject(String value){
		sSubject = new String(value.trim());
		getField(Fid.Subject).set(sSubject);	
	}
	
	public void setContent(String value){
		sContent = new String(value.trim());
		getField(Fid.Content).set(sContent);	
	}
	
	//設置開始日期 忽略秒
	public void setStartDate(Calendar calDate)
	{   
		calDateStart.setTimeInMillis(calDate.getTimeInMillis());	
		calDateStart.set(Calendar.SECOND, 0);
		calDateStart.set(Calendar.MILLISECOND, 0);
		getField(Fid.StartDate).set(calDateStart);
	}
	
	public void setAlarm(boolean value)
	{
		bAlarm = value;
		getField(Fid.Alarm).set(bAlarm);
	}
	
	//getters
	public String getSubject()
	{
		return sSubject;
	}
	
	public String getContent(){
		return sContent;
	}
	
	public Calendar getStartDate()
	{
		return calDateStart;
	}
	
	public boolean getAlarm()
	{
		return bAlarm;
	}

	@Override
	public String getTableName()
	{
		return DatabaseOpenHelper.TABLE_NAME_AGENDA;
	}	
	
	public void getValuesFromDataRow()
	{
		setSubject(getField(Fid.Subject).asString());
		setContent(getField(Fid.Content).asString());
		setStartDate(getField(Fid.StartDate).asCalendar());
		setAlarm(getField(Fid.Alarm).asBoolean());
	}
	
}
