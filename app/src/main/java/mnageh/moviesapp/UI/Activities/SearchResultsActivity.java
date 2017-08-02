package mnageh.moviesapp.UI.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mnageh.moviesapp.R;
import mnageh.moviesapp.Adapters.movie_Adapter;
import mnageh.moviesapp.UI.Fragments.detailed_Fragment;
import mnageh.moviesapp.UI.Fragments.main_Fragment;
import mnageh.moviesapp.Models.movie_Model;
import mnageh.moviesapp.Utils.Fetching_Data;
import mnageh.moviesapp.Utils.network_Response;



public class SearchResultsActivity extends AppCompatActivity {

    private ArrayList<movie_Model> Movies;
    private movie_Adapter movieAdapter;
    RecyclerView MoviesRecyclerView;
    main_Fragment m_frag_processes;
    Context context;
    boolean IsTablet=false;
    Bundle MoviesInfo;
    boolean InstanceState;
    static boolean checkFrag = false;
    TextView textView;
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        MoviesRecyclerView=(RecyclerView)findViewById(R.id.RecyclerView);
        MoviesRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        textView=(TextView)findViewById(R.id.txtS);
        textView1=(TextView)findViewById(R.id.txtS1);
        textView1.setVisibility(View.GONE);
        MoviesInfo=new Bundle();
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            textView.setText("Search Result: "+query);
            //use the query to search
            collectData("search",query);
            Toast.makeText(SearchResultsActivity.this,"^__^",Toast.LENGTH_SHORT).show();


        }
    }

    public void collectData(String Key,String q){
        if(MainActivity.NetworkState()) {
            Fetching_Data fetchData = new Fetching_Data(Key, q);
            fetchData.execute();
            fetchData.setNetworkResponse(new network_Response()  {

                @Override
                public void OnSuccess(String JsonData) {
                    Movies = movie_Model.ParsingTrailerData(JsonData);
                    if(Movies.size()==0)
                        textView1.setVisibility(View.VISIBLE);
                    movieAdapter = new movie_Adapter(Movies, SearchResultsActivity.this);
                    MoviesRecyclerView.setAdapter(movieAdapter);
                    if(JsonData==null)
                        Toast.makeText(MainActivity.ctx," No Internet Connection", Toast.LENGTH_SHORT).show();
                    CheckTablet();
                    ClickEvent();
                }
            });
        }
        else{
            Movies = new ArrayList<>();
            movieAdapter = new movie_Adapter(Movies, context);
            MoviesRecyclerView.setAdapter(movieAdapter);
            Toast.makeText(this," No Internet Connection", Toast.LENGTH_SHORT).show();
            CheckTablet();
            ClickEvent();
        }
    }

    private void CheckTablet() {

        movie_Model movie =  new movie_Model();
        if (IsTablet ) {
            if (Movies.size() != 0)
                movie = Movies.get(0);
            MoviesInfo.putParcelable("movie_Model", movie);
            if (!InstanceState && !checkFrag ) {
                detailed_Fragment detailedFragment1 = new detailed_Fragment();
                detailedFragment1.setArguments(MoviesInfo);
                getFragmentManager().beginTransaction().replace(R.id.DetailedFragment, detailedFragment1).commit();
                checkFrag = true;
            }
        }
    }

    private void ClickEvent() {
        movieAdapter.setClickListener(new movie_Adapter.RecyclerViewClickListener() {
            @Override
            public void ItemClicked(View v, int position) {
                movie_Model movie=new movie_Model();
                movie=Movies.get(position);
                MoviesInfo.putParcelable("movie_Model",movie);
                if (!IsTablet) {
                    Intent in = new Intent( SearchResultsActivity.this , detailed_Activity.class);
                    in.putExtra("MoviesInfo", MoviesInfo);
                    startActivity(in);
                } else {
                    detailed_Fragment  detailedFragment1=new detailed_Fragment();
                    detailedFragment1.setArguments(MoviesInfo);
                    getFragmentManager().beginTransaction().replace(R.id.DetailedFragment,detailedFragment1).commit();

                }
            }
        });
    }
}
