package mnageh.moviesapp.UI.Activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import mnageh.moviesapp.UI.Fragments.main_Fragment;
import mnageh.moviesapp.R;

//import mnageh.moviesapp.Utils.MySharedPref;

public class MainActivity extends AppCompatActivity  {
    main_Fragment mainFragment;
    public static Activity ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;
        mainFragment=  new main_Fragment();
        if(savedInstanceState==null){
            getFragmentManager().beginTransaction().add(R.id.MainFragment, mainFragment).commit();
        }
        else{
            mainFragment=(main_Fragment) getFragmentManager().findFragmentById(R.id.MainFragment);
        }

    }

    public static boolean NetworkState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}