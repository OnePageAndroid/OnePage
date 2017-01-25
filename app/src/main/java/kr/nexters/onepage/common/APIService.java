package kr.nexters.onepage.common;

import io.reactivex.Flowable;
import kr.nexters.onepage.common.model.PageRepo;
import kr.nexters.onepage.common.model.ServerResponse;
import kr.nexters.onepage.main.model.LocationSearchRepo;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by ohjaehwan on 2017. 1. 5..
 */

public interface APIService {
    //회원가입
    @FormUrlEncoded
    @POST("user/save")
    Call<ServerResponse> login(
            @Field("email") String email
    );

    //페이지 저장
    @FormUrlEncoded
    @POST("page/save")
    Call<ServerResponse> savePage(
            @Field("locationId") long locationId,
            @Field("email") String email,
            @Field("content") String content
    );

    //페이지 이미지 저장
    @Multipart
    @POST("page/image/save")
    Call<ServerResponse> savePageImage(
            @Part("pageId") long pageId,
            @Part MultipartBody.Part file
    );

    //위도경도로 SearchedLocation Id 받아오기
    @GET("location/search/coordinates")
    Call<LocationSearchRepo> getLocationsFromCoordniates(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude
    );

    //페이지 받아오기
    @GET("page/location")
    Call<PageRepo> getPagesFromLocation(
            @Query("locationId") long locationId,
            @Query("pageNumber") int pageNumber,
            @Query("perPageSize") int perPageSize
    );


    /** API for Rx  **/

    //위도경도로 SearchedLocation Id 받아오기
    @GET("location/search/coordinates")
    Flowable<LocationSearchRepo> getFlowableLocationsFromCoordniates(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude
    );


    //페이지 받아오기
    @GET("page/location")
    Flowable<PageRepo> getFlowablePagesFromLocation(
            @Query("locationId") long locationId,
            @Query("pageNumber") int pageNumber,
            @Query("perPageSize") int perPageSize
    );
}
