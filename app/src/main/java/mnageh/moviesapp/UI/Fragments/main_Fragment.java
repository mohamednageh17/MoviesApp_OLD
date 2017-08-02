package mnageh.moviesapp.UI.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mnageh.moviesapp.UI.Activities.MainActivity;
import mnageh.moviesapp.UI.Activities.detailed_Activity;
import mnageh.moviesapp.Adapters.movie_Adapter;
import mnageh.moviesapp.Data.MovieContract;
import mnageh.moviesapp.Models.movie_Model;
import mnageh.moviesapp.R;
import mnageh.moviesapp.Utils.Fetching_Data;
import mnageh.moviesapp.Utils.network_Response;

public class main_Fragment extends Fragment {

    boolean InstanceState;
    Activity CurrentActivity;
    ArrayList<movie_Model> Movies;
    static boolean checkFrag = false;
    movie_Adapter movieAdapter;
    Bundle MoviesInfo;
    Context context;
    boolean IsTablet;
    RecyclerView MoviesRecyclerView;
    View view;
    mainFragment_processes m_frag_processes;
    TextView type;

    private final static String MENU_SELECTED = "selected";
    private int selected = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.main_fragment, container, false);
        m_frag_processes = new mainFragment_processes();
        context = view.getContext();
        CurrentActivity = getActivity();
        movieAdapter = new movie_Adapter();
        IsTablet = getResources().getBoolean(R.bool.isTablet);
        MoviesInfo = new Bundle();
        type = (TextView) view.findViewById(R.id.type);

        if(savedInstanceState!=null) {
            selected = savedInstanceState.getInt(MENU_SELECTED);
        }else {
            type.setText("Popular TV series:");
            m_frag_processes.collectData("Popular Movies");
        }
        MoviesRecyclerView = (RecyclerView) view.findViewById(R.id.MoviesRecyclerView);
        if (IsTablet)
            MoviesRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        else
            MoviesRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        return view;
        }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Movies",Movies);
        outState.putInt(MENU_SELECTED, selected);
    }

    @Override
    public void onResume() {
        super.onResume();
        m_frag_processes.DisplayFavouriteMovies();
    }

    public class mainFragment_processes{

    public mainFragment_processes(){}

    public void collectData(String Key){
        if(MainActivity.NetworkState()) {
            Fetching_Data fetchData = new Fetching_Data(Key, "");
            ClickEvent();
            fetchData.execute();
            fetchData.setNetworkResponse(new network_Response()  {

                @Override
                public void OnSuccess(String JsonData) {
                    Movies = movie_Model.ParsingTrailerData(JsonData);
                    movieAdapter = new movie_Adapter(Movies, context);
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
            Toast.makeText(getActivity()," No Internet Connection", Toast.LENGTH_SHORT).show();
            CheckTablet();
            ClickEvent();
        }
    }

    public void CheckTablet(){
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

    public  void DisplayFavouriteMovies(){
        Movies=getFavouriteMovies();
        movieAdapter=new movie_Adapter(Movies,context);
        MoviesRecyclerView.setAdapter(movieAdapter);
        CheckTablet();
        ClickEvent();
        detailed_Fragment.IsFavouriteSelected(true);
    }


    public ArrayList<movie_Model> getFavouriteMovies(){
        ArrayList<movie_Model>FavouriteList=new ArrayList<>();
        movie_Model movie_model;
        Cursor cursor;
        cursor=getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_ID);
                String title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH);
                String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                String rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_VOTE_AVERAGE);
                String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE_DATE);
                movie_model  = new movie_Model(id, title, posterPath, overview, rating, releaseDate);
                FavouriteList.add(movie_model);
            } while (cursor.moveToNext());
        }
        return FavouriteList;
    }

    public void ClickEvent(){
        movieAdapter.setClickListener(new movie_Adapter.RecyclerViewClickListener() {
            @Override
            public void ItemClicked(View v, int position) {
                movie_Model movie=new movie_Model();
                movie=Movies.get(position);
                MoviesInfo.putParcelable("movie_Model",movie);
                if (!IsTablet) {
                    Intent in = new Intent( CurrentActivity , detailed_Activity.class);
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuInflater inflater_ = getActivity().getMenuInflater();
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate menu to add items to action bar if it is present.
        //inflater.inflate(R.menu.menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (item.getItemId()==R.id.pop)
        {
            selected=id;
            type.setText("Popular TV series:");
            m_frag_processes.collectData("Popular Movies");

        }
        else if(item.getItemId()==R.id.top){
            selected=id;
            type.setText("Top Rated TV series:");
            m_frag_processes.collectData("Top Rated Movies");
        }
        else if(item.getItemId()==R.id.fav){
            selected=id;
            type.setText("Favourite TV series:");
             m_frag_processes.DisplayFavouriteMovies();
        }
        else if(item.getItemId()==R.id.latest_Url){
            selected=id;
            type.setText("Latest TV series");
            m_frag_processes.collectData("latest_Url");
        }
        else if(item.getItemId()==R.id.airing_today){
            selected=id;
            type.setText("Airing Today TV series:");
            m_frag_processes.collectData("airing_today");
        }
        else if(item.getItemId()==R.id.on_the_air){
            selected=id;
            type.setText("On The Air TV series:");
            m_frag_processes.collectData("on_the_air");
        }


        return true;
    }
}