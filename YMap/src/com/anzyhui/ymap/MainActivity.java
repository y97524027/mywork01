package com.anzyhui.ymap;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.*;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.*;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity implements OnGetPoiSearchResultListener, BaiduMap.OnMarkerClickListener {

    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationClient client;
    private PoiSearch poiSearch;
    private LatLng currentLatLng;
    private RadioGroup rgDistance;
    private RadioGroup rgType;
    private Button btnSearch;
    private LinearLayout choiceContainer;

    private InfoWindow infoWindow;

    //存储标记的Marker对象
    private List<Marker> markers;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //在Application中初始化地图



        //得到搜索按钮
        btnSearch = (Button)findViewById(R.id.btn_search);
       //得到两组单选按钮
        rgDistance = (RadioGroup)findViewById(R.id.rg_distance);
        rgType = (RadioGroup)findViewById(R.id.rg_type);
        //容纳选项的布局
        choiceContainer = (LinearLayout) findViewById(R.id.choise_container);

        //得到地图控件
        mapView = (MapView)findViewById(R.id.map_view);


        ListView listView = new ListView(this);
        listView.addView(new View(this));

        markers = new LinkedList<>();


        //得到地图对象
        baiduMap = mapView.getMap();

        //地图对象上的marker添加点击事件监听
        baiduMap.setOnMarkerClickListener(this);
        //baiduMap.setMapType();

        //POi检索工具初始化
        poiSearch = PoiSearch.newInstance();
        //设置poi检索结果监听
        poiSearch.setOnGetPoiSearchResultListener(this);

        //自动初始化当前位置
        initLoaction();
        //首先要得到当前位置

        //判断网络衔接状态，没有联网则打开网络设置
        if(!isNetworkAvailble())
            modifySetting();


    }

    @Override
    protected void onStart() {
        super.onStart();

        //如果网络状态可用  就发送定位请求
        if(isNetworkAvailble())
            client.requestLocation();
        else
            modifySetting();

    }

    private void modifySetting(){
        //打开设置
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    /**
     * 判断网络状态
     * @return
     */
    private boolean isNetworkAvailble(){

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo==null)
            return false;
        return true;
    }

    /**
     * 按 选择的半径和 关键字查找poi
     */
    private void searchNearBy(String keyword,int radius){

        PoiNearbySearchOption option = new PoiNearbySearchOption();
        //设置每页容量
        option.pageCapacity(10);
        //查找半径
        option.radius(radius);
        //关键字
        option.keyword(keyword);
        //查找的中心点，先默认设置为当前位置
        option.location(currentLatLng);
        poiSearch.searchNearby(option);

//        //指定范围内搜索，
//        poiSearch.searchInBound(PoiBoundSearchOption );
//        //指定城市内搜索
//        poiSearch.searchInCity(PoiCitySearchOption);
//        //详情搜索
//        poiSearch.searchPoiDetail(PoiDetailSearchOption);

    }

    /**
     * 定位  ，需要定位的相关权限
     */
    private void initLoaction(){
        //定位工具类
        client = new LocationClient(getApplicationContext());
        //得到该定位服务的参数对象，可以设置各种参数
        LocationClientOption locOption = client.getLocOption();
        //打开gps
        locOption.setOpenGps(true);
        //设置定位模式，高精度
        locOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //坐标类型为百度的坐标系   默认为gcj02 国测局
        locOption.setCoorType("bd09ll");
        //需要地址
        locOption.setIsNeedAddress(true);
        //设置定位时间的间隔 <1000则需要手动定位 >=1000则自动定位
        locOption.setScanSpan(999);
        //需要设备的方向
        locOption.setNeedDeviceDirect(true);
        //把设置的参数应用到 定位服务中
        client.setLocOption(locOption);
        //定位监听
        client.registerLocationListener(new MyBDLocationListener());
        //开启定位服务，并发出一次定位请求
        client.start();
        //发出一次定位请求
        //client.requestLocation();
    }

    /**
     * 当找到poi 结果时
     * @param poiResult
     */
    @Override
    public void onGetPoiResult(PoiResult poiResult) {

        //删除原有覆盖物
        if (markers!=null && markers.size()!=0){
            while(!markers.isEmpty()){
                Marker marker = markers.remove(0);
                marker.remove();
            }
//            for(Marker marker:markers) {
//                marker.remove();
//                markers.remove(marker);
//            }
//            markers.clear();
        }

        //得到检索的结果后，用marker标记起来
        List<PoiInfo> poiInfos = poiResult.getAllPoi();
        //如果查找结果不为空
        if (poiInfos != null) {
            int i = 1;
            for(PoiInfo poiInfo:poiInfos){

                //得到纬经度
                LatLng latLng = poiInfo.location;
                //得到手机号
                String phone = poiInfo.phoneNum;
                //得到名字
                String name = poiInfo.name;

                Bundle extras = new Bundle();
                extras.putString("name",name);
                extras.putString("phone",phone);

                //为每个poi设置标记
                MarkerOptions options = new MarkerOptions();
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromAsset("Icon_mark" + i + ".png");
                options.icon(descriptor).position(latLng).extraInfo(extras);
                i++;
                //将得到的覆盖物对象保存
                Marker marker = (Marker)baiduMap.addOverlay(options);
                markers.add(marker);
            }

        }

    }

    /**
     * 当得到poi结果详情时
     * @param poiDetailResult
     */
    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    public void btnSearch(View view) {

        //如果完全透明，设置淡入效果
        float alpha = choiceContainer.getAlpha();
        if (alpha==0f){
            layoutAlphaAnimate(0.0f,1.0f);
            choiceContainer.setAlpha(1.0f);
            return;
        }

//        //如果选择部分不显示，就让他显示,这次点击肯定是选不了的，下次点击才能选
//        if(choiceContainer.getVisibility()==View.INVISIBLE) {
//            choiceContainer.setVisibility(View.VISIBLE);
//            return;
//        }

        //得到选中的搜索距离
        int rbDistanceId = rgDistance.getCheckedRadioButtonId();
        //种类
        int rbTypeId = rgType.getCheckedRadioButtonId();
        if(rbDistanceId==-1) {
            Toast.makeText(this, "请选择搜索范围", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(rbTypeId==-1) {
            Toast.makeText(this, "请选择搜索种类", Toast.LENGTH_SHORT).show();
            return;
        }
        //得到选中的那个单选按钮
        RadioButton rbDistance = (RadioButton)rgDistance.findViewById(rbDistanceId);

        RadioButton rbType = (RadioButton)rgType.findViewById(rbTypeId);

        //得到单选按钮上的文本
        String distance = rbDistance.getText().toString();
        String type = rbType.getText().toString();

        //得到第一个非数字的位置
        //int end = distance.indexOf("\\D");
        //得到半径
        //int radius = Integer.parseInt(getNumString(distance));
        int radius = Integer.parseInt(distance.substring(0,distance.length()-1));

        //搜索
        searchNearBy(type, radius);
        //搜索后，设置淡出效果
        if(choiceContainer.getAlpha()==1.0f) {
            layoutAlphaAnimate(1.0f, 0.0f);
            choiceContainer.setAlpha(0.0f);
            return;
        }

        //搜索了后，选择框又不可见
        //choiceContainer.setVisibility(View.INVISIBLE);


        //choiceContainer.setLayoutTransition();
        //LayoutAnimationController layoutAnimation = choiceContainer.getLayoutAnimation();
        //layoutAnimation.

    }

    private void layoutAlphaAnimate(float min,float max){
        //用来存放动画的动作
        AnimationSet animationSet = new AnimationSet(true);
        //定义一个动画的动作   0.0---1.0   --->颜色加深
        AlphaAnimation alphaAnimation = new AlphaAnimation(min,max);
        //指定动画执行的持续时间
        alphaAnimation.setDuration(1000);
        //动作添加到动作集中
        animationSet.addAnimation(alphaAnimation);
        //执行动画集中的动作
        choiceContainer.startAnimation(animationSet);

    }

    /**
     * 低昂marker被点击时
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle extraInfo = marker.getExtraInfo();
        if (extraInfo != null) {
            String name = extraInfo.getString("name");
            String phone = extraInfo.getString("phone");
            //得到标记物位置
            LatLng position = marker.getPosition();

            View view = LayoutInflater.from(this).inflate(R.layout.info_window, null);
            TextView txtName = (TextView)view.findViewById(R.id.txt_name);
            TextView txtPhone = (TextView)view.findViewById(R.id.txt_phone);

            txtName.setText(name);
            txtPhone.setText(phone);
            //当电话号被点击时
            txtPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    baiduMap.hideInfoWindow();

                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
                    startActivity(intent);
                }
            });

            infoWindow = new InfoWindow(view,position,70);
            baiduMap.showInfoWindow(infoWindow);

        }

        return false;
    }
//    private String getNumString(String str){
//        String num = null;
//
//        String regex = "[0-9]{1,}";
//
//        Pattern pattern = Pattern.compile(regex);
//
//        Matcher matcher = pattern.matcher(str);
//
//        if (matcher != null) {
//            num = matcher.group();
//        }
//        return num;
//
//    }

    //内部类，监听定位状态
    class MyBDLocationListener implements BDLocationListener{

        /**
         * 每次接收到新位置信息，回调该方法，把地图设置到定位到的经纬度
         * @param bdLocation
         */
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //当前设备的方向，单位度
            float direction = bdLocation.getDirection();

            //altitude海拔 高度
            double currentAltitude = bdLocation.getAltitude();
            //当前经纬度
            double currentLatitude = bdLocation.getLatitude();
            double currentLongitude = bdLocation.getLongitude();
            currentLatLng = new LatLng(currentLatitude,currentLongitude);
            //设置地图显示的位置
            showSpecificLocation(currentLatLng);
            //标记指定的位置
            markSpecificLocation(currentLatLng);
        }
    }

    /**
     * 将地图以指定位置为中心显示
     * @param latLng
     */
    private void showSpecificLocation(LatLng latLng){
        //修改地图的显示状态（内容）
        //利用工厂得到一个指定位置指定缩放比例的状态对象
        MapStatusUpdate statusUpdate =
                MapStatusUpdateFactory.newLatLngZoom(latLng,17);

        //设置地图为该状态
        baiduMap.setMapStatus(statusUpdate);
    }

    /**
     * 给指定的经纬度作标记
     * @param latLng
     */
    private void markSpecificLocation(LatLng latLng){

        MarkerOptions options = new MarkerOptions();
        //options.icon()
        //Marker marker = new Marker(options);
        //设置编辑的图标
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromAsset("Icon_start.png");
        options.icon(bitmapDescriptor);
        options.position(latLng);
        //OverlayOptions overlayOptions = new OverlayOptions();
        //设置标记对象，传递一个标记对象OverlayOptions（MarkerOptions实现了它）
        baiduMap.addOverlay(options);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        //取消定位
        client.stop();
        //释放poi检索
        poiSearch.destroy();
    }


}
