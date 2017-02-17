package kr.nexters.onepage.common;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {
    // where my server lives.
    public static final String SERVER = "http://138.197.194.138:8089/OnePage/api/v1/";
    public static final String LOCAL_SERVER = "http://localhost:8080/api/v1/";
    private static final String SERVER_WEATHER ="http://apis.skplanetx.com/";
    private Retrofit client;
    private Retrofit weatherClient;

    private NetworkManager(){
        //Retrofit Enviroment setting.
        client = new Retrofit.Builder()
                .baseUrl(SERVER) // where your server lives
                .addConverterFactory(GsonConverterFactory.create()) // with what data format you want to transmit, in my case JSON
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        weatherClient =  new Retrofit.Builder()
                .baseUrl(SERVER_WEATHER) // where your server lives
                .addConverterFactory(GsonConverterFactory.create()) // with what data format you want to transmit, in my case JSON
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    // singleton holder pattern : thread safe, lazy class initialization, memory saving.
    private static class InstanceHolder{ private static final NetworkManager INSTANCE = new NetworkManager();}

    public static NetworkManager getInstance(){ return InstanceHolder.INSTANCE; }

    public WeatherAPI getWeatherApi() {
        return weatherClient.create(WeatherAPI.class);
    }

    public APIService getApi() {
        return client.create(APIService.class);
    }

}