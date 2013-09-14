
package com.light.ancalendar.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.light.ancalendar.model.DataField;
import com.light.ancalendar.model.DataRow;
import com.light.ancalendar.model.Task;
import com.light.ancalendar.model.TaskTag;
import com.light.ancalendar.util.Utils;

/**
 * DB工具類
 * @author KrisLight
 *
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper
{
	
	private static final String TAG = DatabaseOpenHelper.class.getSimpleName();
	/** Agenda表名 **/
	public static final String TABLE_NAME_AGENDA = "agenda";
	/** AgendaTag表名 **/
	public static final String TABLE_NAME_AGENDA_TAG = "agenda_tag";
	/** 數據庫名 **/
	private static final String DB_NAME = "mycal.db";
	/** 版本号 **/
	private static final int DB_VERSION = 1;
	/**數據庫操作類**/
	private SQLiteDatabase db = null;
	
	public DatabaseOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		DataRow nDataRow = new Task();
		String create_task_sql = getSqlTableDefinition(nDataRow.getTableName(), nDataRow.getTableDef());
		DataRow nTagDataRow = new TaskTag();
		String create_tag_sql = getSqlTableDefinition(nTagDataRow.getTableName(), nTagDataRow.getTableDef());
		Log.d(TAG, "create_task_sql: ---------------------"+create_task_sql+ "------------");
		Log.d(TAG, "create_tag_sql: ---------------------"+create_tag_sql+ "------------");
		sqLiteDatabase.execSQL(create_task_sql);
		sqLiteDatabase.execSQL(create_tag_sql);
		
	}
	
	/**
	 * 得到創建表的SQL語句
	 * @param sTableName 表名
	 * @param vecTableDef 表的字段數組
	 * @return
	 */
	public String getSqlTableDefinition(String sTableName, DataField[] vecTableDef)
	{
		String def = "CREATE TABLE " + sTableName + " (";
		for (int i = 0; i < vecTableDef.length; i++)
		{
			def += vecTableDef[i].getColumnDefinition();
			if (i < (vecTableDef.length - 1))
				//中間逗號分隔
				def += ", ";
		}	
		def += ")";
		return def;
	}
	
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (newVersion) {
		  case 2:
			/** only add column on old table**/
//			String add_column_sql = getSqlTableAddColumn(TABLE_NAME,newColumn);
//			Log.d(TAG, "add_column_sql:--------------"+add_column_sql+"----------");
//			db.execSQL(add_column_sql);
//			break;
          case 3:
        	  //原來的字段集合
    		  List<String> columns = getColumns(db, TABLE_NAME_AGENDA);
    		  String rename_sql = getSqlTableRename(TABLE_NAME_AGENDA);
    		  
    		  List<String> columns2 = getColumns(db, TABLE_NAME_AGENDA_TAG);
    		  String rename_sql2 = getSqlTableRename(TABLE_NAME_AGENDA_TAG);
    		  
    		  Log.d(TAG, "rename_sql:--------------"+rename_sql+"----------");
    		  Log.d(TAG, "rename_sql2:--------------"+rename_sql2+"----------");
    		  //重命名
    		  db.execSQL(rename_sql);
    		  db.execSQL(rename_sql2);
    		  //創建新表
    		  onCreate(db);
    		  
    		  //舊字段和新的字段求交集
    		  columns.retainAll(getColumns(db, TABLE_NAME_AGENDA));
    		  String cols = Utils.join(columns, ",");
    		  //將舊的數據插入到新表 
    		  db.execSQL(String.format( "INSERT INTO %s (%s) SELECT %s from temp_%s", TABLE_NAME_AGENDA, cols, cols, TABLE_NAME_AGENDA));
    		  
    		  //舊字段和新的字段求交集
    		  columns2.retainAll(getColumns(db, TABLE_NAME_AGENDA_TAG));
    		  String cols2 = Utils.join(columns2, ",");
    		  //將舊的數據插入到新表 
    		  db.execSQL(String.format( "INSERT INTO %s (%s) SELECT %s from temp_%s", TABLE_NAME_AGENDA_TAG, cols2, cols2, TABLE_NAME_AGENDA_TAG));
    		  
    		  
    		  String drop_sql = getSqlTableDrop("temp_"+TABLE_NAME_AGENDA);
    		  String drop_sql2 = getSqlTableDrop("temp_"+TABLE_NAME_AGENDA_TAG);
    		  
    		  Log.d(TAG, "drop_sql:--------------"+drop_sql+"----------");
    		  Log.d(TAG, "drop_sql2:--------------"+drop_sql2+"----------");
    		  
    		  db.execSQL(drop_sql);
    		  db.execSQL(drop_sql2);
    		  
			break;
		  default:
			  String ds = getSqlTableDrop(TABLE_NAME_AGENDA);
			  String ds2 = getSqlTableDrop(TABLE_NAME_AGENDA_TAG);
			  Log.d(TAG, "drop_sql:--------------"+ds+"----------");
			  Log.d(TAG, "drop_sql2:--------------"+ds2+"----------");
			  db.execSQL(ds);
			  db.execSQL(ds2);
		    break;
		}
		  

	}
	
	/**
	 *  得到重命名SQL
	 */
	private String getSqlTableRename(String tableName){
		  StringBuffer sb = new StringBuffer();
		  sb.append("ALTER TABLE ");
		  sb.append(tableName);
		  sb.append(" RENAME TO temp_");
		  sb.append(tableName);
		  return sb.toString();
	}
	
	/**
	 *  得到刪除表SQL
	 */
	private String getSqlTableDrop(String tableName){
		  StringBuffer sb = new StringBuffer();
		  sb.append("DROP TABLE IF EXISTS ");
		  sb.append(tableName);
		  return sb.toString();
	}
	
	/**
	 *  得到新增字段的表SQL
	 */
	private String getSqlTableAddColumn(String tableName,String columnName){
		  StringBuffer sb = new StringBuffer();
		  sb.append("ALTER TABLE ");
		  sb.append(tableName);
		  sb.append(" ADD COLUMN ");
		  sb.append(columnName);
		  return sb.toString();
	}
	
	/**
	 *  得到這個Table中的所有字段組成的List集合
	 */
	public static List<String> getColumns(SQLiteDatabase db, String tableName) {
	    List<String> ar = null;
	    Cursor c = null;
	    try {
	    	//查一條數據得到所有字段
	        c = db.rawQuery("select * from " + tableName + " limit 1", null);
	        if (c != null) {
	            ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (c != null)
	            c.close();
	    }
	    return ar;
	}

	
	/**
	 *  得到數據庫的名字
	 * 
	 */
	public final String getName()
	{
		return DB_NAME;
	}

		
	/**關閉DB**/
	public void close()
	{
		if (isOpened())
			db.close();
	}
	
	/**判斷DB是否打開**/
	public boolean isOpened()
	{
		if (db != null)
			return true;
		return false;
	}
	
	/**得到DB寫操作類 **/
	public SQLiteDatabase getWriteSQLiteDb()
	{
		return getWritableDatabase();
	}
	
	/**得到DB讀操作類 **/
	public SQLiteDatabase getReadSQLiteDb()
	{
		return getReadableDatabase();
	}
	
	
}
