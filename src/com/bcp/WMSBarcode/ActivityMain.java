package com.bcp.WMSBarcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class ActivityMain extends Activity {
    /**
     * Called when the activity is first created.
     */
    JSONArray jarray = null;
    Button BtnSubmit;
    String StatusRequest="0";

    public static  final String ENDPOINT = "http://192.168.31.10:9020/DFA_DUMMY";
    public static  final String LOCALPOINT = "http://192.168.28.57/PrintServer";
    TextView TxtKode,TxtKeterangan;
    ProgressBar Pb;
    EditText InputBarcode;
    String[] RandomBarcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        BtnSubmit = (Button) findViewById(R.id.Main_BtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new GenerateBarCode(ActivityMain.this).execute();
                if((InputBarcode.getText().toString().length()>5)&&(TxtKode.getText().toString().length()>5)&&(TxtKeterangan.getText().toString().length()>4)){
                    requestGenerateBarCode();
                }else{
                    Toast.makeText(getApplicationContext(),"Barcode masih salah",Toast.LENGTH_SHORT).show();
                }
            }
        });

        TxtKode = (TextView) findViewById(R.id.Barcode_SKU);
        TxtKeterangan = (TextView) findViewById(R.id.Barcode_NamaSKU);

        Pb = (ProgressBar) findViewById(R.id.Barang_PB);
        Pb.setVisibility(View.GONE);

        InputBarcode = (EditText) findViewById(R.id.Barang_InputBrg);
        InputBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((InputBarcode.getText().toString()).length()>5){
                    requestDataBrg();
                }
            }
        });

        RandomBarcode = new String[11];
        RandomBarcode[0] = "11210006508";
        RandomBarcode[1] = "11210007253";
        RandomBarcode[2] = "11210009387";
        RandomBarcode[3] = "14067853";
        RandomBarcode[4] = "15700051443";
        RandomBarcode[5] = "15700052013";
        RandomBarcode[6] = "27000379134";
        RandomBarcode[7] = "27000388112";
        RandomBarcode[8] = "27000390146";
        RandomBarcode[9] = "300410807009";
        RandomBarcode[10] = "3014260007836";

        Random generator = new Random();
        InputBarcode.setText(RandomBarcode[generator.nextInt(10)]);

    }

    public class GenerateBarCode extends AsyncTask<Void, Integer, Void> {

        Context context;
        Handler handler;
        Dialog dialog;
        TextView txtLoadingProgress;
        int showDialog=0;

        GenerateBarCode(Context context, Handler handler){
            this.context=context;
            this.handler=handler;
        }

        GenerateBarCode(Context context){
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // create dialog
            dialog=new Dialog(context);
            dialog.setCancelable(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.p_loading);
            txtLoadingProgress =(TextView) dialog.findViewById(R.id.Loading_TxtLoading);
            txtLoadingProgress.setText("Downloading . .");
            dialog.show();
        }


        @Override
        protected Void doInBackground(Void... arg0) {

            final FN_JSONParser jParser = new FN_JSONParser();

            try {
                JSONObject json = jParser.getJSONFromUrl(LOCALPOINT);
                StatusRequest = json.getString("STATUS");

                if (StatusRequest.equals("1")){
                    jarray = json.getJSONArray("data");

                    for (int i=0;i<jarray.length();i++){
                        JSONObject a = jarray.getJSONObject(i);
                        StatusRequest = a.getString("status");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showDialog=0;
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (showDialog){
                case 0:{
                    dialog.dismiss();
                    break;
                }
                default:break;
            }

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (StatusRequest.equals("1")){
                Toast.makeText(getApplicationContext(),"Berhasil",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Guagal",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestDataBrg(){
        Pb.setVisibility(View.VISIBLE);
        RestAdapter adapter =new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();

        API_Barang api = adapter.create(API_Barang.class);

        api.getBrg(InputBarcode.getText().toString(), new Callback<List<Data_Barang>>() {
            @Override
            public void success(List<Data_Barang> dataDeliveries, Response response) {
                for (int i = 0; i < dataDeliveries.size(); i++) {
                    Data_Barang dBrg = dataDeliveries.get(i);
                    TxtKode.setText(dBrg.getKode());
                    TxtKeterangan.setText(dBrg.getKeterangan());
                    Pb.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                Pb.setVisibility(View.GONE);
                TxtKode.setText("0");
                TxtKeterangan.setText("0");
            }
        });
    }

    private void requestGenerateBarCode(){
        Pb.setVisibility(View.VISIBLE);
        RestAdapter adapter =new RestAdapter.Builder()
                .setEndpoint(LOCALPOINT)
                .build();

        API_Barang api = adapter.create(API_Barang.class);
        api.getGenerateBarCode(new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if (s.equals("1")){
                    Toast.makeText(getApplicationContext(),"Berhasil",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Gagal",Toast.LENGTH_SHORT).show();
                }
                Pb.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getApplicationContext(),"Gagal",Toast.LENGTH_SHORT).show();
                Pb.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ActivityMain.this.finish();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Konfirmasi Keluar").setMessage("Yakin ingin keluar?").setPositiveButton("Ya", dialogClickListener)
                    .setNegativeButton("Tidak", dialogClickListener).show();
        }
        return false;
    }
}
