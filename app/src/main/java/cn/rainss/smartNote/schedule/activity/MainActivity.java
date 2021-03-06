package cn.rainss.smartNote.schedule.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xutil.XUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.rainss.smartNote.R;
import cn.rainss.smartNote.schedule.adapter.ListAdapter;
import cn.rainss.smartNote.schedule.db.DBManager;
import cn.rainss.smartNote.schedule.model.Schedule;
import cn.rainss.smartNote.schedule.receiver.ClockReceiver;
import cn.rainss.smartNote.utils.XToastUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton addBtn;
    private DBManager dm;
    private List<Schedule> scheduleDataList = new ArrayList<>();
    private ListAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyListTextView;

    private long waitTime = 2000;
    private long touchTime = 0;
    private int version = 2;


    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_main);
        /**
         * ????????????
         */
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("????????????");
            builder.setMessage("???????????????????????????????????????????????????????????????APP???????????????");
            builder.setCancelable(false);
            builder.setPositiveButton("??????", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            });
            builder.setNegativeButton("??????", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            Window dialogWindow = dialog.getWindow();
            WindowManager m = getWindowManager();
            Display d = m.getDefaultDisplay(); // ?????????????????????
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // ?????????????????????????????????
            Point point = new Point();
            d.getSize(point);
            // ????????????
            p.width = (int) (point.x * 0.95); // ????????????????????????0.95
            p.gravity = Gravity.CENTER;//????????????
            dialogWindow.setAttributes(p);
        }


        init();

        //????????????????????????
        //Bmob.initialize(this, "bdc479c9f78d163df6442083ce8578e8");

        //??????????????????
        List<Schedule> clockTimeList = new ArrayList<>();
        dm = new DBManager(this);
        dm.readFromDBByClockTime(clockTimeList);
        AlarmManager[] alarmManager = new AlarmManager[clockTimeList.size() + 1];
        List<PendingIntent> intentArray = new ArrayList<>();
        for (int i = 0; i < clockTimeList.size(); i++) {
            //??????????????????
            Intent intent = new Intent(MainActivity.this, ClockReceiver.class);
            intent.putExtra("content", clockTimeList.get(i).getContent());
            pendingIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager[i].set(AlarmManager.RTC_WAKEUP, clockTimeList.get(i).getClockTime(), pendingIntent);
            intentArray.add(pendingIntent);
        }

    }


    //?????????
    private void init() {
        dm = new DBManager(this);
        dm.readFromDBById(scheduleDataList);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("????????????");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //?????????????????????
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.list);
        addBtn = findViewById(R.id.add);
        emptyListTextView = findViewById(R.id.empty);
        addBtn.setOnClickListener(this);

        //?????????????????? ?????????????????????
        Collections.reverse(scheduleDataList);
        adapter = new ListAdapter(this, scheduleDataList);
        recyclerView.setAdapter(adapter);

        //?????????????????????
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int noteId = adapter.getItem(position).getId();
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("id", noteId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                final Schedule schedule = adapter.getItem(position);
                final int id = schedule.getId();
                new MaterialDialog.Builder(MainActivity.this)
                        .content(R.string.are_you_sure)
                        .positiveText(R.string.delete)
                        .negativeText(R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                                      @Override
                                      public void onPositive(MaterialDialog dialog) {
                                          DBManager.getInstance(MainActivity.this).deleteNote(id);
                                          adapter.removeItem(position);
                                          XToastUtils.toast("????????????");
                                          updateView();
                                      }
                                  }
                        ).show();

            }
        });
        setStatusBarColor();
        updateView();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    //???????????????
    private void updateView() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    //?????????????????????
    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // ??????????????????????????????
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // ?????????????????????
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#00574B"));
    }

    //??????????????????????????????
    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, EditNoteActivity.class);
        switch (view.getId()) {
            case R.id.add:
                startActivity(i);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title(R.string.about)
                        .content("??????????????????????????????")
                        .positiveText("??????")
                        .build();

                dialog.show();
                break;
            case R.id.action_clean:
                new MaterialDialog.Builder(MainActivity.this)
                        .content(R.string.are_you_sure)
                        .positiveText(R.string.clean)
                        .negativeText(R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                dm.deleteAllNote(version++);

                                recyclerView.setVisibility(View.GONE);
                                emptyListTextView.setVisibility(View.VISIBLE);
                            }
                        }).show();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //???????????????
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
                touchTime = currentTime;
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.login_out) {
            Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
