package kr.nexters.onepage.common;

import io.reactivex.Flowable;
import kr.nexters.onepage.common.model.LocationList;
import kr.nexters.onepage.common.model.PageRepo;
import kr.nexters.onepage.common.model.ServerResponse;
import kr.nexters.onepage.main.model.LocationContentRepo;
import kr.nexters.onepage.main.model.LocationSearchRepo;
import kr.nexters.onepage.write.model.PageSaveResponse;
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
    Call<PageSaveResponse> savePage(
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

    //Location
    //모든 랜드마크 리스트 가져오기
    @GET("location/all")
    Call<LocationList> getLocationList();

    //랜드마크에 작성된 총 페이지 수
    @GET("page/count/total")
    Call<Integer> getTotalPageSize(
            @Query("locationId") Long locationId
    );

    //해당 기간 안에 랜드마크에 작성된 총 페이지 수
    @GET("page/count")
    Call<Integer> getPageSizeByPeriod(
            @Query("locationId") Long locationId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );
      
    //지역 이미 받아오기
    @GET("locationImage")
    Flowable<LocationContentRepo> getFlowableLocationImageFromId(
        @Query("locationId") long locationId,
        @Query("weather") String weather
    );

    @GET("heart/save")
    Flowable<Boolean> getBookmark(
            @Query("pageId") long pageId,
            @Query("email") String email
    );

    //좋아요
    @FormUrlEncoded
    @POST("heart/save")
    Flowable<ServerResponse> saveBookmark(
              @Field("pageId") long pageId,
              @Field("email") String email
    );
}
