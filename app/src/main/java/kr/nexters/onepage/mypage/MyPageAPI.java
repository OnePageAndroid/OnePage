package kr.nexters.onepage.mypage;

import android.util.Log;

import java.util.List;

import io.reactivex.functions.Consumer;
import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.model.Page;
import kr.nexters.onepage.common.model.PageRepo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface MyPageAPI {
    @GET("page/user")
    Call<PageRepo> findPageByUser(
            @Query("email") String email,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPageSize") Integer perPageSize
    );

    @GET("page/heart")
    Call<PageRepo> findPageByHeart(
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

        public static void findPageByUser(String email, Integer pageNumber, Integer perPageSize, Consumer<List<Page>> addFunc) {
            create().findPageByUser(email, pageNumber, perPageSize).enqueue(new Callback<PageRepo>() {
                @Override
                public void onResponse(Call<PageRepo> call, Response<PageRepo> response) {
                    if(response.isSuccessful() && response.body().isPresent()) {
                        try {
                            addFunc.accept(response.body().getPages());
                        } catch (Exception e) {
                            Log.e("indPageByUser : ", e.getMessage());
                        }
                    }
                }
                @Override
                public void onFailure(Call<PageRepo> call, Throwable t) {
                    Log.e("findPageByUser : ", t.getMessage());
                }
            });
        }

        public static Call<PageRepo> findPageByHeart(String email, Integer pageNumber, Integer perPageSize) {
            return create().findPageByHeart(email, pageNumber, perPageSize);
        }
    }
}
