package kr.nexters.onepage.common;

import kr.nexters.onepage.common.model.ServerResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by ohjaehwan on 2017. 1. 5..
 */

public interface APIService {
    @FormUrlEncoded
    @POST("user/save")
    Call<ServerResponse> login(
            @Field("email") String email
    );
}
