package com.bcp.WMSBarcode;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 3/18/15
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class Data_Barang {
    String Kode,Keterangan;
    public Data_Barang(String Kode,String Keterangan){
        this.Kode = Kode;
        this.Keterangan = Keterangan;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }

    public String getKode() {
        return Kode;
    }

    public void setKode(String kode) {
        Kode = kode;
    }
}
