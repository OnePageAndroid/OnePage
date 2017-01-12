package kr.nexters.onepage.common;

import kr.nexters.onepage.common.model.WeatherRepo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by ohjaehwan on 2017. 1. 5..
 */

public interface WeatherAPI {
    @Headers({
            "Accept: application/json",
            "appKey: 5f1ae395-cd12-315f-bff7-9e7622e81aa9"
            })
    @GET("/weather/current/hourly")
    Call<WeatherRepo> getWeather(
            @Query("version") int version,
            @Query("lat") String lat,
            @Query("lon") String lon
    );
}
