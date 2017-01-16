package kr.nexters.onepage.common;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {
    // where my server lives.
    private static final String SERVER = "http://138.197.194.138:8089/OnePage/api/v1/";
    private static final String SERVER_WEATHER ="http://apis.skplanetx.com/";
    Retrofit client;
    Retrofit weatherClient;

    private NetworkManager(){
        //Retrofit Enviroment setting.
        client = new Retrofit.Builder()
                .baseUrl(SERVER) // where your server lives
                .addConverterFactory(GsonConverterFactory.create()) // with what data format you want to transmit, in my case JSON
                .build();

        weatherClient =  new Retrofit.Builder()
                .baseUrl(SERVER_WEATHER) // where your server lives
                .addConverterFactory(GsonConverterFactory.create()) // with what data format you want to transmit, in my case JSON
                .build();
    }

    // singleton holder pattern : thread safe, lazy class initialization, memory saving.
    public static class InstanceHolder{ public static final NetworkManager INSTANCE = new NetworkManager();}

    public static NetworkManager getInstance(){ return InstanceHolder.INSTANCE; }

    public WeatherAPI getWeatherApi() {
        return weatherClient.create(WeatherAPI.class);
    }

    public APIService getApi() {
        return client.create(APIService.class);
    }

}