package cn.kxmrg.smartwheelview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SmartWheelView smartWheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> datas = new ArrayList<>();
        datas.add("1");
        datas.add("2");
        datas.add("3");
        datas.add("4");
        datas.add("5");
        datas.add("6");
        datas.add("7");
        datas.add("8");
        datas.add("9");
        smartWheelView = (SmartWheelView) findViewById(R.id.smartWheelView);
//        smartWheelView.setBold(true);
//        smartWheelView.setOffset(2);
//        smartWheelView.setTextColor(Color.RED);
        smartWheelView.setSeletion(2);
        smartWheelView.setDatas(datas);
        smartWheelView.setOnWheelSelectedListener(new SmartWheelView.OnWheelSelectedListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(SmartWheelView.TAG, item);
            }
        });
    }
}
