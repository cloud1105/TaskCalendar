
package com.light.ancalendar.model;


import java.util.Calendar;
import android.content.*;

/**
 *  表中的字段類
 * @author KrisLight
 */
public class DataField
{
	//types 
	/**　Type枚举 如：INT,TEXT,BOOL**/
	public static enum Type { INT, TEXT, BOOL };

	//fields 
	/**　Calendar實例　**/
	private Calendar dateOut = Calendar.getInstance();
	
	//fields 
	/**　對應的數據類型　**/
	private DataRow dataRow = null;
	/**　该字段对应的ContentValues　**/
	private ContentValues values = null;
	
	//fields
	/**字段索引**/
	private int index = 0;
	/** ContentValues存放鍵值對的鍵值 **/
	private String sName = "";
	/** 字段类型 从Type枚举中取一个类型的值 默認為Int **/
	private Type FieldType = Type.INT;
	/**是否可以為空**/
	private boolean bCanBeNull = true;
	/**是否是主鍵**/
	private boolean bPrimaryKey = false;
	
	
	//methods
	/**
	 * 构造方法 
	 * @param index 索引
	 * @param sName 字段名
	 * @param FieldType 字段类型
	 * @param bCanBeNull 是否为空
	 * @param bPrimaryKey 是否为主键
	 */
	public DataField(int index, String sName, Type FieldType, boolean bCanBeNull, boolean bPrimaryKey)
	{
		this.index = index;
		this.sName = sName;
		this.FieldType = FieldType;
		this.bCanBeNull = bCanBeNull;
		this.bPrimaryKey = bPrimaryKey;
	}
	
	/**
	 * 得到每一個字段描述 形如: "name int PRIMARY KEY Not Null"
	 * @return
	 */
	public String getColumnDefinition()
	{
		String s = sName + " " + getSqlType(FieldType);
		if (bPrimaryKey)
			s += " PRIMARY KEY";
		if (!bCanBeNull)
			s += " NOT NULL";
		return s;
	}
	
	public Type getType()
	{
		return FieldType;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	/** 根據SqlType返回真實的類型字段 注意BOOL對應的是Integer 其他類型的用Text **/
	public String getSqlType(Type value)
	{
		switch (value)
		{
			case INT: return "INTEGER";
			case TEXT: return "TEXT";
			case BOOL: return "INTEGER";
		}
		return "TEXT";
	}
	
	/** 设置这个字段对应的数据类型 **/
	public void setParentRow(DataRow dataRow)
	{
		this.dataRow = dataRow;
		this.values = this.dataRow.getContentValues();
	}
	
	/** 得到字段的名字 **/
	public String getName()
	{
		return sName;
	}
	
	//getters
	/** 字符串类型的字段的值 **/
	public String asString()
	{
		return values.getAsString(sName);
	}
	
	/** Long类型的字段的值 **/
	public long asLong()
	{
		return values.getAsLong(sName);
	}
	
	/** Integer类型的字段的值 **/
	public int asInteger()
	{
		return values.getAsInteger(sName);
	}
	
	/** Boolean类型的字段的值 **/
	public boolean asBoolean()
	{
		return (values.getAsLong(sName) == 1);
	}
	
	/** 判断是否为Null **/
	public boolean isNull()
	{
		return (values.get(sName) == null);		
	}
	
	/** 将Long类型的日期类型转换成标准的日期时间 **/
	public Calendar asCalendar()
	{
		dateOut.setTimeInMillis(values.getAsLong(sName));
		return dateOut; 
	}

	//setters
	/** 设置字符串值 **/
	public void set(String value)
	{	
		values.put(sName, value);
	}
	
	/** 设置整型值 **/
	public void set(long value)
	{		
		values.put(sName, value);
	}	

	/** 设置布尔值 **/
	public void set(boolean value)
	{
		int i = (value)?1:0;
		values.put(sName, i);		
	}	

	/** 设置日期值 **/
	public void set(Calendar value)
	{
		values.put(sName, value.getTimeInMillis());
	}

	/** 设置Null值 **/
	public void setNull()
	{
		values.put(sName, (String)null);
	}
	
}
