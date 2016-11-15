# SmartWheelView
完善的WheelView,修改View测量部分代码,增加点击操作,Xml样式修改.  
https://github.com/wangjiegulu/WheelView
## Xml样式设置
* textBold:文本是否加粗
* textSize:文本大小
* textPadding:文本padding
* textColor:正常文本颜色
* selectedTextColor:选中文本的颜色
* lineColor:分割线的颜色
* lineWidth:分割线的宽度
* offset:偏移量

## 使用示例:
#### xml
        <com.zxcily.library.view.SmartWheelView
                android:id="@+id/wheelView"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:lineColor="@color/Green"
                app:lineWidth="2dp"
                app:offset="1"
                app:selectedTextColor="@color/Green"
                app:textBold="true" />
                
#### java
        List<String> datas = new ArrayList<>();
        datas.add("1");
        datas.add("2");
        datas.add("3");
        datas.add("4");
        datas.add("5");
        
        SmartWheelView smartWheelView=new SmartWheelView(context);
        smartWheelView.setDatas(datas);
        smartWheelView.setSeletion(1);
        smartWheelView.setOffset(2);
        smartWheelView.setTextColor(Color.RED);
        smartWheelView.setOnWheelSelectedListener(new SmartWheelView.OnWheelSelectedListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(TAG,item);
            }
        });
