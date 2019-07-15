package com.lucky.deviceinfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lucky.deviceinfo.adapter.MainAdapter;
import com.lucky.deviceinfo.utils.PermissionsUtil;

public class MainActivity extends AppCompatActivity implements MainAdapter.OnItemClickListener,
        PermissionsUtil.IPermissionsCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBar();
        setListView();

        //开启log
        PermissionsUtil.with(this).requestCode(1).isDebug(true)//开启log
                .permissions(PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,
                        PermissionsUtil.Permission.Location.ACCESS_FINE_LOCATION,
                        PermissionsUtil.Permission.Phone.READ_PHONE_STATE,
                        PermissionsUtil.Permission.Location.ACCESS_COARSE_LOCATION,
                        PermissionsUtil.Permission.Location.ACCESS_FINE_LOCATION).request();
    }

    private void setActionBar() {

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            //设置是否有返回箭头
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //移除左边显示的标题
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        //需要调用onRequestPermissionsResult
//        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        //监听跳转到权限设置界面后再回到应用
//        permissionsUtil.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    public void onPermissionsGranted(int requestCode, String... permission) {
        //权限获取回调
    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {
        //权限被拒绝
    }

    private void setListView() {
        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        MainAdapter mainAdapter = new MainAdapter(this);
        recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL
                , false));

        mainAdapter.setOnItemClickListener(this);
        recycler_view.setAdapter(mainAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("确认");
            builder.setMessage("当前版本:" + getVersionName(this));
            builder.setPositiveButton("是", null);
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取版本名称
     *
     * @param context 上下文
     * @return 版本名称
     */
    public static String getVersionName(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d("ddd", position + "");
        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.setClass(this, ShowInfoActivity.class);
        startActivity(intent);
    }
}
