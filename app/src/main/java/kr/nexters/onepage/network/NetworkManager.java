package kr.nexters.onepage.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {
    // where my server lives.
    private static final String SERVER ="http://";
    Retrofit client;

    private NetworkManager(){
        //Retrofit Enviroment setting.
        client = new Retrofit.Builder()
                .baseUrl(SERVER) // where your server lives
                .addConverterFactory(GsonConverterFactory.create()) // with what data format you want to transmit, in my case JSON
                .build();
    }

    // singleton holder pattern : thread safe, lazy class initialization, memory saving.
    public static class InstanceHolder{ public static final NetworkManager INSTANCE = new NetworkManager();}
    public static NetworkManager getInstance(){ return InstanceHolder.INSTANCE; }

    //API Return
    public <T> T getApiFromClass(Class<T> serviceClass){
        // connecting my API and my Retrofit environment and return.
        // then I'm able to call my API and make use of it
        return client.create(serviceClass);
    }

    public APIService getApi() {
        return getApiFromClass(APIService.class);
    }

}