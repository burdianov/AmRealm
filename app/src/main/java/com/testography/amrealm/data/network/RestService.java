package com.testography.amrealm.data.network;

import com.testography.amrealm.data.network.res.CommentRes;
import com.testography.amrealm.data.network.res.ProductRes;
import com.testography.amrealm.utils.ConstantsManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface RestService {

    // @GET("error/400")
    @GET("products")
    Observable<Response<List<ProductRes>>> getProductResObs
            (@Header(ConstantsManager.IF_MODIFIED_SINCE_HEADER) String
                     lastEntityUpdate);

    @POST("products/{productId}/comments")
    Call<CommentRes> uploadComment(@Path("productId") String productId,
                                   @Body CommentRes commentRes);
}
