package com.bcp.WMSBarcode;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 3/18/15
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 */
public interface API_Barang {
    @GET("/ws/split.php")
    public void getBrg(@Query("sku") String id,Callback<List<Data_Barang>> response);

    @GET("/index.php")
    public void getGenerateBarCode(Callback<String> response);
}
