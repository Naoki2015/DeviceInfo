package com.lucky.checkinfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lucky.dangerinfo.emulator.Emulator;
import com.lucky.dangerinfo.root.Root;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tc = (TextView) findViewById(R.id.tv);

        Emulator.getInstance().distinguishVM(this);

        tc.setText("是否为模拟器:");

        tc.append(Emulator.getInstance().getVM());

        tc.append("\n");

        tc.append("模拟器名字:");

        tc.append(Emulator.getInstance().getVmName());

        tc.append("\n");

        tc.append("是否root:");

        tc.append(Root.getInstance().isRoot(this));

    }
}
