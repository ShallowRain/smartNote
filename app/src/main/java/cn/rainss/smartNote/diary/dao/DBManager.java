package cn.rainss.smartNote.diary.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.rainss.smartNote.diary.model.Diary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBManager {
    private static DBManager mgr;
    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor c;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getReadableDatabase();
    }

    public static DBManager getMgr(Context context) {
        if (mgr == null)
            mgr = new DBManager(context);
        return mgr;
    }

    /**
     * add diary
     */
    public void add(Diary diary) {
        db.beginTransaction();    //开始事务
        try {
            db.execSQL("insert into diary(diary_label,diary_content,diary_mood,diary_weather,diary_type) values(?,?,?,?,?)",
                    new Object[]{diary.getLabel(), diary.getContent(), diary.getMood(), diary.getWeather(), "useful"});
            db.setTransactionSuccessful();    //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update diary
     */
    public void updateDiary(Diary diary) {
        db.execSQL("update diary set diary_label = ? , diary_content = ?,diary_mood = ?,diary_weather = ? where _id = ? ",
                new String[]{diary.getLabel(), diary.getContent(), diary.getMood(), diary.getWeather(), String.valueOf(diary.get_id())});
    }

    /**
     * delete diary
     */
    public void deleteDiary(String id) {
        db.delete("diary", "_id = ?", new String[]{id});
    }

    /**
     * delete diary to draft
     */
    public void movetodraft(Diary diary) {
        db.execSQL("insert into diary(diary_label,diary_content,diary_mood,diary_weather,diary_type) values(?,?,?,?,?) ",
                new Object[]{diary.getLabel(), diary.getContent(), diary.getMood(), diary.getWeather(), "trash"});
    }

    /**
     * delete diary to draft
     */
    public void movetotrash(String id) {
        db.execSQL("update diary set diary_type = ? where _id = ? ", new String[]{"trash", id});
    }

    /**
     * query all diaries, return list
     *
     * @return List<Diary>
     */
    public Diary query(String id) {
        ArrayList<Diary> diaries = new ArrayList<>();
        c = queryId(id);
        while (c.moveToNext()) {
            Diary diary = new Diary();
            diary.set_id(c.getInt(c.getColumnIndex("_id")));
            diary.setLabel(c.getString(c.getColumnIndex("diary_label")));
            diary.setContent(c.getString(c.getColumnIndex("diary_content")));
            diary.setDate(Timestamp.valueOf(c.getString(c.getColumnIndex("diary_date"))));
            diary.setMood(c.getString(c.getColumnIndex("diary_mood")));
            diary.setWeather(c.getString(c.getColumnIndex("diary_weather")));
            diaries.add(diary);
        }
        c.close();
        return diaries.get(0);
    }

    /**
     * query all diaries, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor() {
        c = db.rawQuery("SELECT * FROM diary  where diary_type = ? ORDER BY diary_date DESC", new String[]{"useful"});
        return c;
    }


    /**
     * query all drafts, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTrash() {
        c = db.rawQuery("SELECT * FROM diary  where diary_type = ? ORDER BY diary_date DESC", new String[]{"trash"});
        return c;
    }

    /**
     * query the diaries from the key, return cursor
     *
     * @return Cursor
     */
    public Cursor find(String key) {
        c = db.rawQuery("SELECT * FROM diary where diary_content like ? or diary_label like ? or " +
                "diary_date like ? and diary_type = ?", new String[]{"%" + key + "%", "%" + key + "%", "%" + key + "%", "useful"});
        return c;
    }


    /**
     * query the trashes from the key, return cursor
     *
     * @return Cursor
     */
    public Cursor findTrash(String key) {
        c = db.rawQuery("SELECT * FROM diary where diary_type = ? and (diary_content like ? or diary_label like ? or " +
                "diary_date like ?)", new String[]{"trash", "%" + key + "%", "%" + key + "%", "%" + key + "%"});
        return c;
    }

    /**
     * query all diaries, return cursor
     *
     * @return Cursor
     */
    public Cursor queryId(String id) {
        c = db.rawQuery("SELECT * FROM diary where _id = ? ", new String[]{id});
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }

    public void deleteAllDiary() {
        db.delete("diary", "diary_type = ?", new String[]{"trash"});
    }

    public void recoverDiary(String id1) {
        db.execSQL("update diary set diary_type = ? where _id = ?", new String[]{"useful", id1});
    }

    /**
     * 获取某个天气的天数
     * @return
     */
    public Map<Integer,Integer> getWeather(){
        Map<Integer, Integer> data = new HashMap<>();
        Cursor weatherdb = db.rawQuery("select diary_weather as weather,count(_id) as num from diary group by diary_weather", null);
        while(weatherdb.moveToNext()){
            int weather = weatherdb.getInt(0);
            int num = weatherdb.getInt(1);
            data.put(weather,num);
        }
        return data;
    }

    /**
     * 获取某个心情的天数
     * @return
     */
    public Map<Integer,Integer> getMood(){
        Map<Integer, Integer> data = new HashMap<>();
        Cursor mooddb = db.rawQuery("select diary_mood as mood,count(_id) as num from diary group by diary_mood", null);
        while(mooddb.moveToNext()){
            int mood = mooddb.getInt(0);
            int num = mooddb.getInt(1);
            data.put(mood,num);
        }
        return data;
    }
}
            