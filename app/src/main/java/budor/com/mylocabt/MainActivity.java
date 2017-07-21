package budor.com.mylocabt;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_OPEN_BT = 0x01;
    private BluetoothAdapter btAdapter;
    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit = (EditText) findViewById(R.id.edit_dada);
        String inputText = load();
        if (!TextUtils.isEmpty(inputText)) {
            edit.setText(inputText);
            edit.setSelection(inputText.length());
            showToast(getString(R.string.succeeded));
        }
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            showToast(getString(R.string.deviceNoBlueTooth));
            return;
        }
        Button btnOpenBt = (Button) findViewById(R.id.btn_open);
        Button btnSave = (Button) findViewById(R.id.btn_input_data);
        Button btnRead = (Button) findViewById(R.id.btn_output_data);
        Button btnDb = (Button) findViewById(R.id.btn_new_database);
        Button btnGetAddressList = (Button) findViewById(R.id.btn_get_address);
        btnGetAddressList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddressActivity.class);
                startActivity(intent);
            }
        });
        btnDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connector.getDatabase();
                showToast("使用litepal建立数据库完成");
            }
        });
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("database", MODE_PRIVATE);
                String name = pref.getString("name", "");
                int age = pref.getInt("age", 0);
                boolean married = pref.getBoolean("married", false);
                Log.i("MainActivity", "name is " + name);
                Log.i("MainActivity", "age is " + age);
                Log.i("MainActivity", "married is " + married);
                showToast("读取数据中");
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("database", MODE_PRIVATE).edit();
                editor.putString("name", "Tom");
                editor.putInt("age", 22);
                editor.putBoolean("married", false);
                editor.apply();
                showToast("保存数据完成");
            }
        });
        btnOpenBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btAdapter.isEnabled()) {
                    showToast("蓝牙已处于打开状态...");
                    boolean isClose = btAdapter.disable();
                    showToast("蓝牙已关闭..." + isClose);
                } else {
                    //boolean isOpen =  btAdapter.enable();
                    Intent open = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(open, REQUEST_OPEN_BT);
                    //  showToast("蓝牙的状态是" + isOpen);
                }
            }
        });
    }

    private String load() {
        FileInputStream in;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput("data");
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String inputText = edit.getText().toString();
        save(inputText);
    }

    private void save(String inputText) {
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_OPEN_BT == requestCode) {
            showToast("请求成功");
        }
    }
}