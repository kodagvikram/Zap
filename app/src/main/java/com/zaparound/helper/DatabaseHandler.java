package com.zaparound.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.SingleChatMessage;

import java.util.ArrayList;


public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int db_version = 1;
	private static final String db_name = "chat_db";
	private static final String table_single_message = "single_message";
	private static final String table_chatroom_message = "chatroom_message";

	private static final String id = "id", from = "from", to = "to", message = "message", time = "time",
			messageid = "messageid", self = "self", messagetype = "messagetype", tickstate = "tickstate",
			username = "username";

	public DatabaseHandler(Context context) {
		super(context, db_name, null, db_version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table IF NOT EXISTS " + table_single_message + "  (" + id
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, `" + from + "` INTEGER NOT NULL, `" + to
				+ "` INTEGER NOT NULL, `" + message + "` TEXT NOT NULL, `" + time + "` TEXT NOT NULL, `" + messageid
				+ "` INTEGER NOT NULL UNIQUE, `" + self + "` INTEFER NOT NULL, `" + messagetype + "` TEXT NOT NULL, `"
				+ tickstate + "` INTEGER NOT NULL);";
		db.execSQL(sql);


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + table_single_message);
		onCreate(db);
	}

	public void insertOneOnOneMessage(SingleChatMessage message) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("`" + messageid + "`", message.getMessageId());
			values.put("`" + this.message + "`", message.getMessage());
			values.put("`" + self + "`", message.getIsMyMessage());
			values.put("`" + messagetype + "`", message.getMessageType());
			values.put("`" + tickstate + "`", message.getTickStatus());
			values.put("`" + time + "`", message.getTimestamp());
			values.put("`" + from + "`", message.getFrom());
			values.put("`" + to + "`", message.getTo());
			db.insertOrThrow(table_single_message, null, values);
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			db.close();
		}
	}

	public ArrayList<SingleChatMessage> getAllMessages(long from, long to) {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "Select * from " + table_single_message + " where (`" + this.from + "` =" + from + "  AND `"
				+ this.to + "` =" + to + " ) OR (`" + this.to + "` =" + from + "  AND `" + this.from + "` =" + to
				+ " )";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			ArrayList<SingleChatMessage> messages = new ArrayList<>();
			while (!cursor.isAfterLast()) {
				SingleChatMessage msg = new SingleChatMessage();

				msg.setFrom(cursor.getString(cursor.getColumnIndex(this.from)));
				msg.setTo(cursor.getString(cursor.getColumnIndex(this.to)));
				msg.setTickStatus(cursor.getInt(cursor.getColumnIndex(tickstate)));
				msg.setIsMyMessage(cursor.getString(cursor.getColumnIndex(self)));
				msg.setMessageType(cursor.getString(cursor.getColumnIndex(messagetype)));
				msg.setTimestamp(cursor.getString(cursor.getColumnIndex(time)));
				msg.setMessage(cursor.getString(cursor.getColumnIndex(message)));
				msg.setMessageId(cursor.getString(cursor.getColumnIndex(messageid)));
				messages.add(msg);
				cursor.moveToNext();
			}
			if (cursor != null) {
				cursor.close();
			}
			db.close();
			return messages;
		} catch (Exception e) {
			// e.printStackTrace();
		} catch (Error e) {
			// e.printStackTrace();
		}
		if (cursor != null) {
			cursor.close();
		}
		db.close();
		return null;
	}


	/**
	 * Used to get last message
	 *
	 * @param from,to fgh
	 * @return User string lastmessage
	 * @throws Exception
	 */
	public SingleChatMessage getLastMessage(long from, long to) throws Exception {

		String lastmessage="";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			String sql = "Select * from " + table_single_message + " where (`" + this.from + "` =" + from + "  AND `"
					+ this.to + "` =" + to + " ) OR (`" + this.to + "` =" + from + "  AND `" + this.from + "` =" + to
					+ " )"+"  ORDER BY id DESC LIMIT 1";
			Cursor cursor = null;
			SingleChatMessage msg = new SingleChatMessage();
			try {
				cursor = db.rawQuery(sql, null);
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					msg.setFrom(cursor.getString(cursor.getColumnIndex(this.from)));
					msg.setTo(cursor.getString(cursor.getColumnIndex(this.to)));
					msg.setTickStatus(cursor.getInt(cursor.getColumnIndex(tickstate)));
					msg.setIsMyMessage(cursor.getString(cursor.getColumnIndex(self)));
					msg.setMessageType(cursor.getString(cursor.getColumnIndex(messagetype)));
					msg.setTimestamp(cursor.getString(cursor.getColumnIndex(time)));
					msg.setMessage(cursor.getString(cursor.getColumnIndex(message)));
					msg.setMessageId(cursor.getString(cursor.getColumnIndex(messageid)));
					cursor.moveToNext();
				}
				if (cursor != null) {
					cursor.close();
				}
				db.close();
			return msg;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		} catch (Exception e) {

			throw new Exception(e.getMessage());
		}
	}

	public void updateMessageDetails(SingleChatMessage message) {
		try {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("`" + messageid + "`", message.getMessageId());
		values.put("`" + this.message + "`", message.getMessage());
		values.put("`" + self + "`", message.getIsMyMessage());
		values.put("`" + messagetype + "`", message.getMessageType());
		values.put("`" + tickstate + "`", message.getTickStatus());
		values.put("`" + time + "`", message.getTimestamp());
		values.put("`" + from + "`", message.getFrom());
		values.put("`" + to + "`", message.getTo());
		db.update(table_single_message, values, "`" + messageid + "`=" + message.getMessageId(), null);
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
      /*
      *update tickstatus to read
      */
	public void updateReadstatus(String from, long to) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("`" + tickstate + "`", 1);
			db.update(table_single_message, values, "`" + this.from + "` =" + from + "  AND `"+ this.to + "` =" + to, null);
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
     *get Unread count
     */
	public int  getUnreadCount() {
		int a=0;
		try {
			SQLiteDatabase db=this.getReadableDatabase();
			String unreadquery = "SELECT * FROM "+table_single_message+" WHERE "+this.tickstate+" = '0'";
			Cursor cursor=db.rawQuery(unreadquery, null);
			cursor.moveToFirst();
			if(cursor.getCount()>0)
			{
				a=cursor.getCount();
			}
			cursor.close();
			db.close();
			return a;
		}catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}

	}

	public SingleChatMessage getMessageDetails(String msgid) {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "Select * from " + table_single_message + " where `" + this.id + "` =" + msgid;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				DatabaseUtils.dumpCursor(cursor);
				cursor.moveToFirst();
				SingleChatMessage msg = new SingleChatMessage();
				msg.setFrom(cursor.getString(cursor.getColumnIndex(this.from)));
				msg.setTo(cursor.getString(cursor.getColumnIndex(this.to)));
				msg.setTickStatus(cursor.getInt(cursor.getColumnIndex(tickstate)));
				msg.setIsMyMessage(cursor.getString(cursor.getColumnIndex(self)));
				msg.setMessageType(cursor.getString(cursor.getColumnIndex(messagetype)));
				msg.setTimestamp(cursor.getString(cursor.getColumnIndex(time)));
				msg.setMessage(cursor.getString(cursor.getColumnIndex(message)));
				msg.setMessageId(cursor.getString(cursor.getColumnIndex(messageid)));
				return msg;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	/**
	 * To delete deletemessagesbyId as for user id
	 *
	 * @param to pass
	 * @return number of rows deleted
	 * @throws Exception
	 */
	public long deletemessagesbyId(String to,String from) throws Exception {
		try {
			long count;
			SQLiteDatabase database = getWritableDatabase();
			database.beginTransaction();
			count = database.delete(table_single_message," (`" + this.from + "` =" + from + "  AND `"
					+ this.to + "` =" + to + " ) OR (`" + this.to + "` =" + from + "  AND `" + this.from + "` =" + to
					+ " )", null);
			database.setTransactionSuccessful();
			database.endTransaction();
			database.close();


//			database.beginTransaction();
//			count = database.delete(table_single_message,"to='"+from+"' and from='"+to+"'", null);
//			database.setTransactionSuccessful();
//			database.endTransaction();
//			database.close();

			return count;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}


	/**
	 * To delete all messages
	 *
	 * @return count of deleted columns
	 * @throws Exception
	 */
	public int deleteallmessages() throws Exception {
		try {
			int count;
			SQLiteDatabase database = getWritableDatabase();
			database.beginTransaction();
			count = database.delete(table_single_message , null, null);
			database.setTransactionSuccessful();
			database.endTransaction();
			database.close();
			return count;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
