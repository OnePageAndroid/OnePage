package kr.nexters.onepage.mypage;

import java.util.List;

import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.model.Page;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface MyPageAPI {
    @GET("page/user")
    Call<List<Page>> findPageByUser(
            @Query("email") String email,
            @Query("pageNumber") Integer pageNumber,
            @Query("perPageSize") Integer perPageSize
    );

    @GET("page/heart")
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

        public static Call<List<Page>> findPageByUser(String email, Integer pageNumber, Integer perPageSize) {
            return create().findPageByUser(email, pageNumber, perPageSize);
        }

        public static Call<List<Page>> findPageByHeart(String email, Integer pageNumber, Integer perPageSize) {
            return create().findPageByHeart(email, pageNumber, perPageSize);
        }
    }
}
