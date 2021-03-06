package cn.rainss.smartNote.diary;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import cn.rainss.smartNote.diary.dao.DBManager;
import cn.rainss.smartNote.diary.model.Diary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.rainss.smartNote.R;
public class InsertActivity extends BaseActivity {
    public ImageView mood, weather;
    public GridView gridView;
    int[] moods = DiaryApplication.moods;
    int[] weathers = DiaryApplication.weathers;
    private DBManager mgr;
    private PopupWindow popupWindow;
    private int wh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_insert);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        wh = display.getWidth();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mgr = DBManager.getMgr(this);
        mood = (ImageView) findViewById(R.id.mood);
        mood.setImageResource(R.drawable.vector_drawable_mood1);
        mood.setTag(R.drawable.vector_drawable_mood1);
        mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniPopupWindow("mood");
                popupWindow.showAsDropDown(v);
            }
        });

        weather = (ImageView) findViewById(R.id.weather);
        weather.setImageResource(R.drawable.weather3);
        weather.setTag(R.drawable.weather3);
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniPopupWindow("weather");
                popupWindow.showAsDropDown(v);
            }
        });

    }

    private void iniPopupWindow(final String s) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.popup, null);
        gridView = (GridView) root.findViewById(R.id.gridView);
        popupWindow = new PopupWindow(root, wh / 3, ViewGroup.LayoutParams.WRAP_CONTENT);
        SimpleAdapter adapter = new SimpleAdapter(InsertActivity.this,
                getData(s), R.layout.popup_item, new String[]{"item_img"}
                , new int[]{R.id.popup_item});
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (s.equals("mood")) {
                    mood.setImageResource(moods[position]);
                    mood.setTag(moods[position]);
                    popupWindow.dismiss();
                } else {
                    weather.setImageResource(weathers[position]);
                    weather.setTag(weathers[position]);
                    popupWindow.dismiss();
                }
            }
        });

        //??????popwindow?????????????????????item????????????????????????android4.4??????????????????????????????????????????
        popupWindow.setFocusable(true);

        // ??????popupwindow??????????????????????????????
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_popupwindow));// ????????????????????????????????????????????????????????????????????????
        popupWindow.setOutsideTouchable(true);// ??????popupwindow?????????popupwindow?????????
        // ??????????????????popupwindow??????????????????????????????????????????
    }

    private List<Map<String, Object>> getData(String s) {
        List<Map<String, Object>> list = new ArrayList<>();
        int i;
        if (s.equals("mood")) {
            for (i = 0; i < 12; i++) {
                Map<String, Object> map = new HashMap<>();
                map.put("item_img", moods[i]);
                list.add(map);
            }
        } else {
            for (i = 0; i < 12; i++) {
                Map<String, Object> map = new HashMap<>();
                map.put("item_img", weathers[i]);
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.insert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.diary_save:
                diary_save();
                break;
            case R.id.diary_list:
                draft_save();
                break;
        }
        return true;
    }

    private void diary_save() {
        String label = ((EditText) findViewById(R.id.edit_diarylabel))
                .getText().toString();
        String content = ((EditText) findViewById(R.id.edit_diarycontent))
                .getText().toString();
        String mood = findViewById(R.id.mood).getTag().toString();
        String weather = findViewById(R.id.weather).getTag().toString();
        if (label.length() < 1) {
            Toast.makeText(InsertActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Diary diary = new Diary(label, content, mood, weather);
            mgr.add(diary);
            Toast.makeText(InsertActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception x) {
            Toast.makeText(InsertActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void draft_save() {
        String label = ((EditText) findViewById(R.id.edit_diarylabel))
                .getText().toString();
        String content = ((EditText) findViewById(R.id.edit_diarycontent))
                .getText().toString();
        String mood = findViewById(R.id.mood).getTag().toString();
        String weather = findViewById(R.id.weather).getTag().toString();
        if (label.length() < 1) {
            finish();
        } else {
            try {
                Diary diary = new Diary(label, content, mood, weather);
                mgr.movetodraft(diary);
                Toast.makeText(InsertActivity.this, "??????????????????!", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception x) {
                Toast.makeText(InsertActivity.this, "?????????????????????!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
