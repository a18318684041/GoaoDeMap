package com.example.administrator.my_map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.cunoraz.gifview.library.GifView;

import java.util.List;

public class Activity_Weather extends AppCompatActivity implements WeatherSearch.OnWeatherSearchListener {

    private TextView time;

    private TextView wendu;
    private TextView shidu;
    private TextView wind;
    private TextView weather;
    private Button btn_change;
    private WeatherSearchQuery mquery;
    private WeatherSearchQuery mquery1;
    private WeatherSearch mweathersearch;
    private WeatherSearch mweathersearch1;


    private ListView listView;
    private MyAdapter adapter;


    //天气预报
    private TextView tv_broadcast;
    private ImageView img;
    private com.ant.liao.GifView gif1;

    //用Android materia 简化界面
    private RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather);
        gif1 = (com.ant.liao.GifView) findViewById(R.id.gif);
        gif1.setGifImage(R.drawable.yu);
        gif1.setShowDimension(1100,550);


        init();
        initMateria();


    }

    private void initMateria() {
        listView = (ListView) findViewById(R.id.listview);

    }

    private void init() {
        time = (TextView) findViewById(R.id.time);
        wendu = (TextView) findViewById(R.id.wendu);
        shidu = (TextView) findViewById(R.id.shidu);
        wind = (TextView) findViewById(R.id.wind);
        weather = (TextView) findViewById(R.id.weather);

/*        tv_broadcast = (TextView) findViewById(R.id.tv_broadcast);*/
        img = (ImageView) findViewById(R.id.img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity_Weather.this,MainActivity.class);
                startActivity(intent);
            }
        });

        mquery = new WeatherSearchQuery("广州", WeatherSearchQuery.WEATHER_TYPE_LIVE);

        mweathersearch=new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索


        mquery1 = new WeatherSearchQuery("广州", WeatherSearchQuery.WEATHER_TYPE_FORECAST);
        mweathersearch1=new WeatherSearch(this);
        mweathersearch1.setOnWeatherSearchListener(this);
        mweathersearch1.setQuery(mquery1);
        mweathersearch1.searchWeatherAsyn(); //异步搜索
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult , int rCode) {
        if (rCode == 1000) {

            LocalWeatherLive weatherlive = weatherLiveResult.getLiveResult();
            time.setText("发布时间："+weatherlive.getReportTime());
            wendu.setText("温度："+weatherlive.getTemperature()+"℃");
            shidu.setText("湿度："+weatherlive.getHumidity()+"%");
            wind.setText(weatherlive.getWindDirection()+"风"+weatherlive.getWindPower()+"级");
            weather.setText("天气："+weatherlive.getWeather());
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int rcode) {
        //未来几天的天气预报的实现

        LocalWeatherForecast forecastResult = localWeatherForecastResult.getForecastResult();
        adapter = new MyAdapter(Activity_Weather.this,localWeatherForecastResult.getForecastResult().getWeatherForecast());
        listView.setAdapter(adapter);
/*        String result = "";
        time2.setText("发布时间："+localWeatherForecastResult.getForecastResult().getReportTime());
        for (int i = 0; i <forecastResult.getWeatherForecast().size() ; i++) {
            result+= forecastResult.getWeatherForecast().get(i).getDate() + "     白天:"+ forecastResult.getWeatherForecast().get(i).getDayWeather()+"    夜间:"+forecastResult.getWeatherForecast().get(i).getNightWeather()+"    温度:"+forecastResult.getWeatherForecast().get(i).getDayTemp()+"℃/"+forecastResult.getWeatherForecast().get(i).getNightTemp()+"℃\n";
            Log.d("AAA", result);
        }
        tv_broadcast.setText(result);*/
    }







}