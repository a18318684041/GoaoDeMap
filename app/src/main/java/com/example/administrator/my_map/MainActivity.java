package com.example.administrator.my_map;

import android.app.Application;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.RouteSearch;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationSource,AMapLocationListener, PoiSearch.OnPoiSearchListener, AMap.OnCameraChangeListener {

    private MapView mMapView;//显示地图的视图
    private AMap aMap;//定义AMap 地图对象的操作方法与接口。

    private OnLocationChangedListener mListener;//位置发生变化时的监听
    private AMapLocationClient mapLocationClient;//定位服务类。此类提供单次定位、持续定位、地理围栏、最后位置相关功能。
    private AMapLocationClientOption mapLocationClientOption;//定位参数设置，通过这个类可以对定位的相关参数进行设置
    //在AMapLocationClient进行定位时需要这些参数

    //设置地图样式的按钮
    private Button btn_weixing;
    private Button btn_normal;
    private Button btn_night;

    //实现高德地图POI搜索
    private EditText edt_serch;
    private Button btn_search;
    private RouteSearch  routeSearch;
    private String type = "";//poi搜索的类型
    private String  city = "";
    private PoiSearch.Query query;//Poi查询条件类
    private  PoiSearch poisearch;
    private PoiResult poiresult;//Poi搜索返回的结果
    private LatLng latlng;//经纬度
    private List<PoiItem> poiItems;

    Marker marker = null;
    boolean isChange = false;
    private Button btn_shuaxin;


    //用Android materia 简化界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);//必须调用
        init();
        //设置地图样式
        initType();
        //开始搜寻目的地
        initQuery();
    }

    private void initQuery() {

        btn_search = (Button) findViewById(R.id.btn_search);
        edt_serch = (EditText) findViewById(R.id.edt_serch);
        routeSearch = new RouteSearch(this);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.clear();
                doQuery();
                poisearch.searchPOIAsyn();
            }
        });
        btn_shuaxin = (Button) findViewById(R.id.btn_shuaxin);
        btn_shuaxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Activity_Weather.class);
                startActivity(intent);
            }
        });
    }

    private void doQuery() {
        aMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
        int currentPage = 0;
        type = edt_serch.getText().toString();
        Toast.makeText(MainActivity.this,type,Toast.LENGTH_LONG).show();
        query = new PoiSearch.Query("", type, "广州");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        LatLonPoint lp = new LatLonPoint(latlng.latitude, latlng.longitude);
        poisearch = new PoiSearch(this, query);
        poisearch.setOnPoiSearchListener(MainActivity.this);
        poisearch.setBound(new PoiSearch.SearchBound(lp, 2000, true));     // 设置搜索区域为以lp点为圆心，其周围2000米范围
    }

    private void initType() {
        btn_normal = (Button) findViewById(R.id.map_normal);
        btn_weixing = (Button) findViewById(R.id.map_weixing);
        btn_night  = (Button) findViewById(R.id.map_night);
        //标准模式
        btn_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            }
        });
        //卫星图模式
        btn_weixing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
            }
        });
        //夜间模式
        btn_night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);
            }
        });

    }

    //实例化Amap对象
    public void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            setConfigrationAmap();
        }
    }
    //配置Amap对象
    public void setConfigrationAmap() {
        aMap.setLocationSource(MainActivity.this);//设置定位监听
        aMap.setMyLocationEnabled(true);//设置显示定位层，并可以出发定位
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置显示定位按  钮
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位类型
    }
    // 必须重写
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mMapView.onSaveInstanceState(outState);
    }
    // 必须重写
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

    }
    // 必须重写
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }
    // 必须重写
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMapView.onDestroy();
        if(mapLocationClient!=null){
            mapLocationClient.onDestroy();
        }
    }
    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {  System.out.println("已经激活定位-------------activate");
        mListener=onLocationChangedListener;
        if(mapLocationClient==null){
            mapLocationClient=new AMapLocationClient(MainActivity.this);
            mapLocationClientOption=new AMapLocationClientOption();

            mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为 高精度
            mapLocationClient.setLocationOption(mapLocationClientOption);//设置配置
            mapLocationClient.setLocationListener(this);//设置位置变化监听
            mapLocationClient.startLocation();
        }
    }

    //关闭定位
    @Override
    public void deactivate() {

        mListener=null;
        if(mapLocationClient!=null){
            mapLocationClient.stopLocation();
            mapLocationClient.onDestroy();
        }
        mapLocationClient =null;
        System.out.println("已经关闭定位-------------deactivate");
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(mListener!=null&&aMapLocation!=null){
            if(aMapLocation!=null&&aMapLocation.getErrorCode()==0){
                latlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                Toast.makeText(MainActivity.this,aMapLocation.getAddress(),Toast.LENGTH_SHORT).show();
                mListener.onLocationChanged(aMapLocation);
                System.out.println(aMapLocation.getLatitude() + "----" + aMapLocation.getLongitude() + "---------" + aMapLocation.getErrorCode());
            }
        }else{
            Toast.makeText(MainActivity.this,"定位失败:"+aMapLocation.getErrorCode(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int code) {
        if(code == 1000){
            if(result!=null && result.getQuery()!=null){
               if(result.getQuery().equals(query)){
                   poiresult = result;
                   poiItems = poiresult.getPois();
                   Toast.makeText(MainActivity.this,"搜索成功"+poiresult.getPois().size(),Toast.LENGTH_LONG).show();
                   List<SuggestionCity> suggestCity = poiresult.getSearchSuggestionCitys();

                  for (int i = 0; i < poiItems.size() ; i++) {
                       Log.d("AAA",  poiItems.get(i).toString());
                      //在地图上将这些POI点标出来
                       marker = aMap.addMarker(new MarkerOptions()
                              .position(new LatLng( poiItems.get(i).getLatLonPoint().getLatitude(), poiItems.get(i).getLatLonPoint().getLongitude()))
                              .title(poiItems.get(i).getTitle())
                              .draggable(true));
                      marker.showInfoWindow();// 设置默认显示一个infowinfow

                  }
               }
            }
        }else  if(code == 27){
            Toast.makeText(MainActivity.this,"网络问题",Toast.LENGTH_LONG).show();
        }else if(code == 32){
            Toast.makeText(MainActivity.this,"错误的Key",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this,"code"+code,Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }
}