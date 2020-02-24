package com.ibercivis.webviewwsm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private int webViewPreviousState;
    private final int PAGE_STARTED = 0x1;
    private final int PAGE_REDIRECTED = 0x2;
    GeolocationPermissions.Callback mGeoLocationCallback;
    String mGeoLocationRequestOrigin;
    WebView visor;
    String url = "https://crowdwall.ibercivis.es/";
    ImageView logo1, logo2;
    LocationManager locationManager;
    CoordinatorLayout rootView;
    BroadcastReceiver broadcastReceiver;
    int conectado = 0;

    private ConnectivityReceiver connectivityReceiver;
    private ConnectivityReceiver getConnectivityReceiver() {
        if (connectivityReceiver == null)
           connectivityReceiver = new ConnectivityReceiver();

        return connectivityReceiver;
    }


    boolean internet_connection(){
        //Check if connected to internet, output accordingly
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (internet_connection()){

            // Execute DownloadJSON AsyncTask
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            installListener();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            visor = (WebView) findViewById(R.id.webview);
            logo1 = findViewById(R.id.logoizda);
            logo2 = findViewById(R.id.logodcha);


            Drawable myDrawable = getResources().getDrawable(R.drawable.eovalue);
            logo1.setImageDrawable(myDrawable);
            Drawable myDrawable2 = getResources().getDrawable(R.drawable.logo_vertical);
            logo2.setImageDrawable(myDrawable2);
            logo1.setVisibility(View.VISIBLE);
            logo2.setVisibility(View.VISIBLE);


            if (Build.VERSION.SDK_INT >= 23) {
                // Marshmallow+ Permission APIs
                fuckMarshMallow();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                    WebView.setWebContentsDebuggingEnabled(true);
                }
            }
            //     visor.setInitialScale(1);
            //     visor.getSettings().setLoadWithOverviewMode(true);
            //visor.getSettings().setUseWideViewPort(true);
            //visor.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            //visor.setScrollbarFadingEnabled(false);

            visor.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            visor.getSettings().setBuiltInZoomControls(true);
            visor.setWebViewClient(new GeoWebViewClient());
            // Below required for geolocation
            visor.getSettings().setJavaScriptEnabled(true);
            visor.getSettings().setGeolocationEnabled(true);
            visor.setWebChromeClient(new GeoWebChromeClient());

            visor.getSettings().setAppCacheEnabled(true);
            visor.getSettings().setDatabaseEnabled(true);
            visor.getSettings().setDomStorageEnabled(true);

            visor.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());


            visor.loadUrl(url);

        }else{
            //create a snackbar telling the user there is no internet connection and issuing a chance to reconnect
            buildDialog(this).show();
        }

    }








    public class GeoWebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin,
                                                       final GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it

            mGeoLocationRequestOrigin = null;
            mGeoLocationCallback = null;

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    new AlertDialog.Builder(MainActivity.this).setMessage("Necesitamos permisos de geolocalización").setNeutralButton("Perfecto", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGeoLocationRequestOrigin = origin;
                            mGeoLocationCallback = callback;
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        }
                    }).show();
                } else {

                    mGeoLocationRequestOrigin = origin;
                    mGeoLocationCallback = callback;
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                }
            } else {
                callback.invoke(origin, true, true);
            }





            //            final boolean remember = false;
            //            AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
            //            builder.setTitle("Locations");
            //            builder.setMessage("Would like to use your Current Location ")
            //                    .setCancelable(true).setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            //                public void onClick(DialogInterface dialog, int id) {
            //                    // origin, allow, remember
            //                    callback.invoke(origin, true, remember);
            //                }
            //            }).setNegativeButton("Don't Allow", new DialogInterface.OnClickListener() {
            //                public void onClick(DialogInterface dialog, int id) {
            //                    // origin, allow, remember
            //                    callback.invoke(origin, false, remember);
            //                }
            //            });
            //            AlertDialog alert = builder.create();
            //            alert.show();
    }}

    /**
     * WebViewClient subclass loads all hyperlinks in the existing WebView
     */
    public class GeoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // When user clicks a hyperlink, load in the existing WebView
            view.loadUrl(url);
            return true;
        }

        Dialog loadingDialog = new Dialog(MainActivity.this);

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            webViewPreviousState = PAGE_STARTED;

            if (loadingDialog == null || !loadingDialog.isShowing())
                loadingDialog = ProgressDialog.show(MainActivity.this, "",
                        "Loading Please Wait", true, true,
                        new DialogInterface.OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                // do something
                            }
                        });

            loadingDialog.setCancelable(false);
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request,
                                    WebResourceError error) {

      /*      if (isConnected()) {
                final Snackbar snackBar = Snackbar.make(rootView, "onReceivedError : " + error.getDescription(), Snackbar.LENGTH_INDEFINITE);
                snackBar.setAction("Reload", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        visor.loadUrl("javascript:window.location.reload( true )");
                    }
                });
                snackBar.show();
            } else {
                final Snackbar snackBar = Snackbar.make(rootView, "No Internet Connection ", Snackbar.LENGTH_INDEFINITE);
                snackBar.setAction("Enable Data", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);
                        visor.loadUrl("javascript:window.location.reload( true )");
                        snackBar.dismiss();
                    }
                });
                snackBar.show();
            }

            super.onReceivedError(view, request, error); */

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedHttpError(WebView view,
                                        WebResourceRequest request, WebResourceResponse errorResponse) {

      /*      if (isConnected()) {
                final Snackbar snackBar = Snackbar.make(rootView, "HttpError : " + errorResponse.getReasonPhrase(), Snackbar.LENGTH_INDEFINITE);

                snackBar.setAction("Reload", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        visor.loadUrl("javascript:window.location.reload( true )");
                    }
                });
                snackBar.show();
            } else {
                final Snackbar snackBar = Snackbar.make(rootView, "No Internet Connection ", Snackbar.LENGTH_INDEFINITE);
                snackBar.setAction("Enable Data", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);
                        visor.loadUrl("javascript:window.location.reload( true )");
                        snackBar.dismiss();
                    }
                });
                snackBar.show();
            }
            super.onReceivedHttpError(view, request, errorResponse); */
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            if (webViewPreviousState == PAGE_STARTED) {

                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                    loadingDialog = null;
                }
            }
        }
    }


    /**
     * Check if there is any connectivity
     *
     * @return is Device Connected
     */
    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }

        return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if( mGeoLocationCallback != null) {
                        mGeoLocationCallback.invoke(mGeoLocationRequestOrigin, true, true);
                    }
                } else {
                    if (mGeoLocationCallback != null) {
                        mGeoLocationCallback.invoke(mGeoLocationRequestOrigin, false, false);
                    }
                }
return;
                                                        }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void fuckMarshMallow() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Show Location");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {

                // Need Rationale
                String message = "App need access to " + permissionsNeeded.get(0);

                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        Toast.makeText(this, "No new Permission Required- Launching App .You are Awesome!!", Toast.LENGTH_SHORT)
                .show();
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {

        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }

    private void installListener() {

        if (broadcastReceiver == null) {

            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    Bundle extras = intent.getExtras();

                    NetworkInfo info = (NetworkInfo) extras
                            .getParcelable("networkInfo");

                    NetworkInfo.State state = info.getState();
                    Log.d("InternBroadcastReceiver", info.toString() + " "
                            + state.toString());

                    if (state == NetworkInfo.State.CONNECTED) {



                    } else {

                       /* Intent intent2 = new Intent(context, Main2Activity.class);
                        startActivity(intent2);
                        finish();*/

                    }

                }
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }
}




