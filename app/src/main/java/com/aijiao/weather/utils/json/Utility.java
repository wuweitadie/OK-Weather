package com.aijiao.weather.utils.json;

import android.text.TextUtils;

import com.aijiao.weather.db.City;
import com.aijiao.weather.db.County;
import com.aijiao.weather.db.Province;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by 25858 on 2017/4/3.
 */

public class Utility {

    /**
     * 解析服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                //使用非GOJSON解析方式
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getString("id"));
                    province.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                //使用非GOJSON解析方式
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject provinceObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(provinceObject.getString("name"));
                    city.setCityCode(provinceObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                //使用非GOJSON解析方式
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
