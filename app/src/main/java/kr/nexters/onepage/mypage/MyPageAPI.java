package kr.nexters.onepage.mypage;

import android.util.Log;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.model.Page;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface MyPageAPI {
    @GET("/api/v1/page/user")
    Call<List<Page>> findPageByUser(
            @Query("email") String email,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPageSize") Integer perPageSize
    );

    @GET("/api/v1/page/heart")
    Call<List<Page>> findPageByHeart(
            @Query("email") String email,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPageSize") Integer perPageSize
    );

    class Factory {
        public static MyPageAPI create(){
            return new Retrofit.Builder()
                    .baseUrl(NetworkManager.SERVER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(MyPageAPI.class);
        }

        public static List<Page> findPageByUser(String email, Integer pageNumber, Integer perPageSize) {
            try {
                return create().findPageByUser(email, pageNumber, perPageSize).execute().body();
            } catch (IOException e) {
                Log.e("findPageByUser : ", e.getMessage());
                return Lists.newArrayList();
            }
        }

        public static List<Page> findPageByHeart(String email, Integer pageNumber, Integer perPageSize) {
            try {
                return create().findPageByHeart(email, pageNumber, perPageSize).execute().body();
            } catch (IOException e) {
                Log.e("findPageByUser : ", e.getMessage());
                return Lists.newArrayList();
            }
        }
    }
}
