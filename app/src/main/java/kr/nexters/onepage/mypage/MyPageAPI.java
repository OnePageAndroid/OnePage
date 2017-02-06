package kr.nexters.onepage.mypage;

import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.model.PageRepo;
import retrofit2.Call;
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
        public static MyPageAPI create() {
            return new Retrofit.Builder()
                    .baseUrl(NetworkManager.SERVER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(MyPageAPI.class);
        }
    }
}
