package com.example.administrator.my_map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.ant.liao.GifView;

import java.util.List;
import java.util.zip.Inflater;

import static com.example.administrator.my_map.R.id.gif;

/**
 * Created by Administrator on 2017/3/22 0022.
 */
public class MyAdapter extends BaseAdapter{

    private Context context;
    private List<LocalDayWeatherForecast> weather;

    public MyAdapter(Context context,List<LocalDayWeatherForecast> weather){
        this.context = context;
        this.weather = weather;
    }
    @Override
    public int getCount() {
        return weather.size();
    }

    @Override
    public Object getItem(int position) {
        return weather.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.list_weather);
        if(weather.get(position).getDayWeather().equals("晴天")){
            textView.setBackgroundResource(R.drawable.qingtian);
        }else if (weather.get(position).getDayWeather().equals("大雨")||weather.get(position).getNightWeather().equals("大雨")){
            textView.setBackgroundResource(R.drawable.dayu);
        }

        textView.setText("日期:"+weather.get(position).getDate()+"\n夜间温度:"+weather.get(position).getNightTemp()+"℃\n白天温度:"+weather.get(position).getDayTemp()+"℃\n白天天气:"+weather.get(position).getDayWeather()+"\n夜间天气:"+weather.get(position).getNightWeather());
        return convertView;
    }
}
