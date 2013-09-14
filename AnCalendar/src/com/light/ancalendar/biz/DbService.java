package com.light.ancalendar.biz;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.light.ancalendar.database.DatabaseOpenHelper;
import com.light.ancalendar.model.DataField;
import com.light.ancalendar.model.DataRow;
import com.light.ancalendar.model.Task;
import com.light.ancalendar.model.TaskTag;
import com.light.ancalendar.util.Utils;

public class DbService {
	private final String TAG = DbService.class.getSimpleName();
	/** DB輔助類 **/
	private DatabaseOpenHelper helper;
	private static DbService mInstance;
	private SQLiteDatabase db;
	
	public static DbService getInstance(Context context){
		if(mInstance == null){
			mInstance = new DbService(context);
		}
		return mInstance;
	}
	
	private DbService(Context context){
   	  helper = new DatabaseOpenHelper(context);
    }
    
 	/** 基本的插入操作 **/
 	public long insertValues(DataRow dr)
 	{
 		 db = helper.getWritableDatabase();
 		 long lRowId = db.insert(dr.getTableName(), null, dr.getContentValues());
 		 Log.i("Light", "插入");
         return lRowId; 
 	}
 	
 	/** 基本的根據ID更新操作 **/
 	public long updateValues(long lRowId,DataRow dr)
 	{
 	    db = helper.getWritableDatabase();
 		String sWhere = String.format("_id = %d", lRowId);
 		long lRowsUpdated = db.update(dr.getTableName(), dr.getContentValues(), sWhere, null);
 		 Log.i("Light", "更新");
 		return lRowsUpdated;
 	}
   
 	/** 基本的根據ID刪除操作 **/
   public long deleteDataRow(long lRowId,DataRow dr)
   {
	    db = helper.getWritableDatabase();
 		String sWhere = String.format("_id = %d", lRowId);
 		long lRowsUpdated = db.delete(dr.getTableName(), sWhere, null);
 		 Log.i("Light", "刪除");
 		return lRowsUpdated;
   }
 	
   /** 根據Task_id更新Tag表 **/
   private long updateTagValueByTaskId(long taskId,TaskTag dr){
	    db = helper.getWritableDatabase();
		String sWhere = String.format("task_id = %d", taskId);
		long lRowsUpdated = db.update(dr.getTableName(), dr.getContentValues(), sWhere, null);
		 Log.i("Light", "更新Tag");
		return lRowsUpdated;
   }
   
   /** 根據Task_id刪除Tag **/
   private long deleteTagValueByTaskId(long taskId,TaskTag dr){
	    db = helper.getWritableDatabase();
		String sWhere = String.format("task_id = %d", taskId);
		long lRowsDeleted = db.delete(dr.getTableName(), sWhere, null);
		 Log.i("Light", "刪除Tag");
		return lRowsDeleted;
   }
   
   
   /** 基本的根據Id查詢表中的所有數據 **/
 	public Cursor loadDataRowById(long lRowId,DataRow dr)
 	{
 		helper.getReadableDatabase();
 		List<String> columnNames = getColumn(dr);
 		String cols = Utils.join(columnNames, ",");
 		final String s = "select %s from %s where _id = %d";		
 		String sql = String.format(s, cols, dr.getTableName(), lRowId);
 		Log.i(TAG,"loadDataRowById SQL:"+sql);
 		Cursor cr = db.rawQuery(sql, null);
 		 Log.i("Light", "根據Id查詢");
 		//if cursor valid, set first data row as current
 		if ((cr != null) && (cr.getCount() > 0))
 			cr.moveToFirst();
 		return cr;
 	}
 	
 	
 	/** 更新Task和TaskTag **/
 	public boolean updateTaskAndTag(long taskId,Task task,TaskTag tag){
 		Log.i("Light", "updateTaskAndTag");
 		db = helper.getWritableDatabase();
 		db.beginTransaction();
 		try{
 			long rowNum = updateValues(taskId, task);
 			if(rowNum != 0){
 				tag.setTaskId(taskId);
 				if(updateTagValueByTaskId(taskId, tag)!=0){
 					db.setTransactionSuccessful();
 					return true;
 				}else{
 					throw new Exception("更新Tag失敗");
 				}
 			}else{
 				throw new Exception("更新Task失敗");
 			}
 			
 		}catch (Exception e) {
 			e.printStackTrace();
 			return false;
		}finally{
			db.endTransaction();
		}
 	}
 	
 	
 	
 	/** 刪除task和Task_tag表數據 **/
 	public boolean deleteTaskAndTaskTag(long taskId,Task task,TaskTag tag){
 		db = helper.getWritableDatabase();
 		db.beginTransaction();
 	    long tagId = 0;
 		try{
 			if(deleteDataRow(taskId, task) != 0){
 				if(deleteTagValueByTaskId(taskId, tag) != 0){
 					db.setTransactionSuccessful();
 					return true;
 				}else{
 					throw new Exception("刪除Tag失敗");
 				}
 			}else{
 				throw new Exception("刪除Task失敗");
 			}
 		}catch (Exception e) {
 			e.printStackTrace();
 			 return false;
		}finally{
			db.endTransaction();
		}
 	}
 	
 	/** 插入Task和Task_tag表數據 **/
 	public boolean insertTaskAndTaskTag(Task task,TaskTag tag){
 		db = helper.getWritableDatabase();
 		db.beginTransaction();
 		try{
 			long taskId = insertValues(task);
 			if(taskId != -1){
 			  tag.setTaskId(taskId);
 			  if(insertValues(tag)!= -1){
 				  db.setTransactionSuccessful();
 				  return true;
 			  }else{
 	 				throw new Exception("插入Tag表失敗");
 	 		  }
 			}else{
 				throw new Exception("插入Task表失敗");
 			}
 		}catch (Exception e) {
 			e.printStackTrace();
 			  return false;
		}finally{
			db.endTransaction();
		}
 	}
 	
 	 /** 查詢表中的所有數據  **/
 	public Cursor loadAllDataRow(DataRow dr)
 	{
 		db = helper.getReadableDatabase();
 		List<String> columnNames = getColumn(dr);
 		String cols = Utils.join(columnNames, ",");
 		final String s = "select %s from %s";		
 		String sql = String.format(s, cols, dr.getTableName());
 		Log.i(TAG,"loadAllDataRow SQL:"+sql);
 		Cursor cr = db.rawQuery(sql, null);
 		 Log.i("Light", "查詢所有");
 		if ((cr != null) && (cr.getCount() > 0))
 			cr.moveToFirst();
 		return cr;
 	}
 	
	
 	/** 查詢指定時間查詢所有Task信息 **/
 	public Cursor loadTaskByStartTime(int year,int month,int day){
 		db = helper.getReadableDatabase();
 		final String s = "select a._id ,a.subject ,a.start_date from agenda a,agenda_tag b where a._id = b.task_id and b.year = %d and b.month = %d and b.day = %d order by a.start_date";		
 		String sql = String.format(s,year,month,day);
 		Log.i(TAG,"loadTaskByStartTime SQL:"+sql);
 		Cursor cr = db.rawQuery(sql, null);
 		 Log.i("Light", "查詢指定時間的所有Task信息");
 		if ((cr != null) && (cr.getCount() > 0))
 			cr.moveToFirst();
 		return cr;
 	}
 	
 	
 	/** 判斷指定日期是否有Task **/
 	public boolean hasTask(int year,int month,int day){
 		db = helper.getReadableDatabase();
 		final String s = "select _id from agenda_tag a where a.year = %d and a.month = %d and a.day= %d limit 1";		
 		String sql = String.format(s,year,month,day);
 		Log.i(TAG,"hasTask SQL:"+sql);
 		Cursor cr = db.rawQuery(sql, null);
 		 Log.i("Light", "判斷指定日期是否有Task");
 		if ((cr != null) && (cr.getCount() > 0)){
 			cr.close();
 			return true;
 		}else{
 			cr.close();
 			return false;
 		}
 	}
 	
	
 	
	/**　得到這個表中的所有字段 **/
	public List<String> getColumn(DataRow dr) {
	    List<String> ar = new ArrayList<String>();
	    try{
	    	  DataField[] fields = dr.getTableDef();
	    	  for(DataField f : fields){
	    	   	ar.add(f.getName());
	    	  }
	    }catch (Exception e) {
	            e.printStackTrace();
	    } 
	    return ar;
	}
   
 	
 	public void closeDataBase(){
 		this.helper.close();
 	}
}
