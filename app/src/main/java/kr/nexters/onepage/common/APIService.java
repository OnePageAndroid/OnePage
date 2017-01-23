package kr.nexters.onepage.common;

import kr.nexters.onepage.common.model.ServerResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ohjaehwan on 2017. 1. 5..
 */

public interface APIService {
    @FormUrlEncoded
    @POST("user/save")
    Call<ServerResponse> login(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("page/save")
    Call<ServerResponse> savePage(
            @Field("locationId") long locationId,
            @Field("email") String email,
            @Field("content") String content
    );

    @Multipart
    @POST("page/image/save")
    Call<ServerResponse> savePageImage(
            @Part("pageId") long pageId,
            @Part MultipartBody.Part file
    );
}
