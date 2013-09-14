
package com.light.ancalendar.model;


import android.content.ContentValues; 
import android.database.Cursor;

/**
 * 抽象的数据类型类,
 * 所有的数据原型都要继承这个类
 * @author KrisLight
 *
 */
public abstract class DataRow
{
	/** 这个类型对应表中的字段集合 **/
	private DataField[] fields = null;
	/** 用于存放ContentValues和字段的ContentValues綁定 **/
	private ContentValues values = new ContentValues();
	/** 構造方法 **/
	public DataRow(){}
		
	/**
	 * 設置字段集合并與其綁定
	 * @param fields 字段集合
	 */
	public void setTableDefinition(DataField[] fields)
	{
		this.fields = fields;		
		updateDataFieldsParentRow(this);
	}
	
	/**　將數據類型綁定其對應的所有DataField(字段) **/
	public void updateDataFieldsParentRow(DataRow row)
	{
		for (int i = 0; i < fields.length; i++)
			fields[i].setParentRow(row);
	}
	
	/** 得到表中的所有DataField(字段) **/
	public DataField[] getTableDef()
	{
		return fields;
	}
	
	/** 清空ContentValues **/
	public void clearContentValues()
	{
		values.clear();
	}
	
	/** 得到ContentValues **/
	public ContentValues getContentValues()
	{
		return values;
	}

	/** 設置ContentValues **/
	public void setContentValues(ContentValues values)
	{
		this.values = values;
		updateDataFieldsParentRow(this);
	}
	
	/** 根據索引取出DataField(字段) **/
	public DataField getField(int idx)
	{
		return fields[idx];
	}
	
	/** 得到索引位置的字段名 **/
	public String fieldName(int idx)
	{
		return fields[idx].getName();	
	}	
	
	/** 查詢結果 遍歷Cursor結果集給對應的DataField(字段)賦值 **/
	public boolean getValuesFromCursor(Cursor cr)
	{
		if ((cr != null) && (cr.getPosition() != -1))
		{
			//遍歷
			for (int idx = 0; idx < fields.length; idx++)
			{
				DataField field = getField(idx);
				//check if null value
				if (cr.isNull(idx))
				{
					//如果查出的值為空 ,設置為空
					field.setNull();
				} else {
				//根據其對應的類型取值
					final DataField.Type t = field.getType();
					//parse value by type
					if (t == DataField.Type.INT)
						field.set(cr.getLong(idx));
					if (t == DataField.Type.TEXT)
						field.set(cr.getString(idx));
					if (t == DataField.Type.BOOL)
						field.set((cr.getInt(idx) == 1)?true:false);
				}				
			}
			return true;
		}
		return false;
	}
	

	/** 得到表名 **/
	public abstract String getTableName();	
	
}

