package com.chakam.kampunganggrek.api;

/**
 * Created by Robby Dianputra on 10/31/2017.
 */

import com.chakam.kampunganggrek.model.city.ItemCity;
import com.chakam.kampunganggrek.model.cost.ItemCost;
import com.chakam.kampunganggrek.model.province.ItemProvince;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // Province
    @GET("province")
    @Headers("key:88ba8ce398ba5a7257b4fc23d0b92c0d")
    Call<ItemProvince> getProvince();

    // City
    @GET("city")
    @Headers("key:88ba8ce398ba5a7257b4fc23d0b92c0d")
    Call<ItemCity> getCity (@Query("province") String province);

    // Cost
    @FormUrlEncoded
    @POST("cost")
    Call<ItemCost> getCost (@Field("key") String Token,
                            @Field("origin") String origin,
                            @Field("destination") String destination,
                            @Field("weight") String weight,
                            @Field("courier") String courier);

}
