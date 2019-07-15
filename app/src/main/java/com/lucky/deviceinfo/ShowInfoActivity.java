package com.lucky.deviceinfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lucky.deviceinfo.adapter.ShowInfoAdapter;
import com.lucky.deviceinfo.info.IInfo;
import com.lucky.deviceinfo.info.impl.BatteryInfo;
import com.lucky.deviceinfo.info.impl.CpuInfo;
import com.lucky.deviceinfo.info.impl.SystemInfo;
import com.lucky.deviceinfo.info.impl.NetworkInfo;
import com.lucky.deviceinfo.info.impl.OtherInfo;
import com.lucky.deviceinfo.info.impl.RecognitionInfo;
import com.lucky.deviceinfo.info.impl.SensorInfo;
import com.lucky.deviceinfo.info.impl.SimInfo;
import com.lucky.deviceinfo.info.impl.StorageInfo;

import java.util.Map;

public class ShowInfoActivity extends AppCompatActivity {

    private Map<String, String> map;
    private IInfo info;
    private Toolbar mToolbar;
    private String[] allItems = {"基本信息", "CPU信息", "电池信息", "存储信息", "SIM卡信息", "网络信息",
            "传感器信息", "其他信息", "危险信息"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showinfo);


        int position = getIntent().getIntExtra("position", 0);

        setToolbar(position);
        switch (position) {
            //"设备信息", "CPU信息", "电池信息", "存储信息", , "SIM卡信息", "网络信息",
            //            "传感器信息", "其他检测","行为信息"
            case 0:
                info = new SystemInfo();
                break;

            case 1:
                info = new CpuInfo();
                break;

            case 2:
                info = new BatteryInfo();
                break;

            case 3:
                info = new StorageInfo();

            case 4:
                info = new SimInfo();
                break;

            case 5:
                info = new NetworkInfo();
                break;

            case 6:
                info = new SensorInfo();
                break;
            case 7:
                info = new OtherInfo();
                break;
            case 8:
                info = new RecognitionInfo();
                break;
        }
        map = info.getInfo(this);
        RecyclerView recyclerView = findViewById(R.id.show_info);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));

        ShowInfoAdapter adapter = new ShowInfoAdapter(this, map);
        recyclerView.setAdapter(adapter);
    }

    private void setToolbar(int position) {
        mToolbar = findViewById(R.id.toolbar);

        TextView tv = findViewById(R.id.tv_info);
        tv.setText(allItems[position]);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            //设置是否有返回箭头
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //移除左边显示的标题
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
