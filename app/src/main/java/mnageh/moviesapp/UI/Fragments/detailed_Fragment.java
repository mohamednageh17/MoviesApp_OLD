package mnageh.moviesapp.UI.Fragments;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import mnageh.moviesapp.UI.Activities.MainActivity;
import mnageh.moviesapp.Data.MovieContract;
import mnageh.moviesapp.Models.movie_Model;
import mnageh.moviesapp.Models.review_Model;
import mnageh.moviesapp.Models.trailer_Model;
import mnageh.moviesapp.R;
import mnageh.moviesapp.Utils.Fetching_Data;
import mnageh.moviesapp.Utils.network_Response;


public class detailed_Fragment extends Fragment {

    final public  String img_String= "http://image.tmdb.org/t/p/w185/";
    static boolean Favourite_Selected=false;
    ImageView Poster_Img;
    TextView Title;
    TextView Overview;
    TextView Release_Date;
    TextView Vote_Rating;
    TextView Review_Author;
    TextView Review_Content;
    TextView Trailer_Name;
    ImageView Favourite;
    ImageView button;
    detailedFragment_processes d_Frag_processes;
    Bundle Movies_Info ;
    movie_Model movie ;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.detailed_fragment,container,false);
        view=v;
        Movies_Info=new Bundle();
        movie=new movie_Model();
        d_Frag_processes=new detailedFragment_processes();
        Poster_Img=(ImageView)v.findViewById(R.id.Poster_Image);
        Title=(TextView)v.findViewById(R.id.Title);
        Release_Date=(TextView)v.findViewById(R.id.Release_Date);
        Overview=(TextView)v.findViewById(R.id.Overview);
        Vote_Rating=(TextView)v.findViewById(R.id.Vote_Rating);
        Review_Author=(TextView)v.findViewById(R.id.ReviewAuthor);
        Review_Content=(TextView)v.findViewById(R.id.ReviewContent);
        Trailer_Name=(TextView)v.findViewById(R.id.TrailerName);
        Favourite=(ImageView)v.findViewById(R.id.Favourite);
        button=(ImageView)v.findViewById(R.id.button);
        Movies_Info=this.getArguments();

        if(savedInstanceState!=null){
            movie= savedInstanceState.getParcelable("movie_Model");
            d_Frag_processes.setReviewDetails();
            d_Frag_processes.setTrailerDetails();
        }
        else{
            movie=Movies_Info.getParcelable("movie_Model");
            if(!Favourite_Selected) {
                d_Frag_processes.FetchReview();
                d_Frag_processes.FetchTrailer();
            }
            else {
                d_Frag_processes.setReviewDetails();
                d_Frag_processes.setTrailerDetails();
            }
        }
        d_Frag_processes.setMovieDetails();
        movie.Favourite=isFavorite();
        if(movie.Favourite)
            Favourite.setImageResource(R.drawable.staron);
        else
            Favourite.setImageResource(R.drawable.staroff);
        Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movie.Favourite){
                    movie.Favourite=true;
                    markAsFavorite();
                    Toast.makeText(getActivity(),"Save in Favourite Movies", Toast.LENGTH_SHORT).show();
                }
                else {
                    movie.Favourite=false;
                    removeFromFavorites();
                    Toast.makeText(getActivity(),"Remove From Favourite Movies", Toast.LENGTH_SHORT).show();

                }
            }
        });
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie_Model",movie);
        super.onSaveInstanceState(outState);
    }

    public class detailedFragment_processes{

        public detailedFragment_processes(){
        }

        public void FetchReview() {
            if(MainActivity.NetworkState()){
                Fetching_Data  fetchData = new Fetching_Data ("review_Model",movie.getId());
                fetchData.execute();
                fetchData.setNetworkResponse(new network_Response()  {
                    @Override
                    public void OnSuccess(String JsonData) {
                        review_Model review = new review_Model();
                        review =  review_Model.ParsingReviewData(JsonData);
                        movie.setReview(review);
                        setReviewDetails();
                    }


                });
            }
            else {
                review_Model review = new review_Model();
                movie.setReview(review);
                setReviewDetails();
                Toast.makeText(getActivity()," No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
        public void FetchTrailer() {
            if(MainActivity.NetworkState()) {
                Fetching_Data  fetchData = new Fetching_Data("trailer_Model", movie.getId());
                fetchData.execute();
                fetchData.setNetworkResponse(new network_Response()  {
                    @Override
                    public void OnSuccess(String JsonData) {
                        trailer_Model trailer = new trailer_Model();
                        trailer = trailer_Model.ParsingTrailerData(JsonData);
                        movie.setTrailer(trailer);
                        setTrailerDetails();
                    }


                });
            }
            else{
                trailer_Model trailer = new trailer_Model();
                movie.setTrailer(trailer);
                setTrailerDetails();
                Toast.makeText(getActivity()," No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        public void setMovieDetails(){
            Picasso.with(view.getContext()).load(img_String+ movie.getPoster_ImageUrl())
                    .placeholder(R.drawable.loadingicon).error(R.drawable.error).into(Poster_Img);
            Title.setText( movie.getTitle());
            Overview.setText(movie.getOverview());
            Release_Date.setText(movie.getRelease_Date());
            Vote_Rating.setText(movie.getVote_average()+"/10");
        }
        public void setTrailerDetails(){
            Trailer_Name.setText(movie.getTrailerName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+ movie.getTrailerKey())));
                }
            });
        }
        public void setReviewDetails(){
            Review_Author.setText(movie.getReviewAuthor());
            Review_Content.setText(movie.getReviewContent());
        }
    }

    public static void IsFavouriteSelected(boolean isSelected) {
        Favourite_Selected=isSelected;
    }

    public void markAsFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            movie.getId());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            movie.getTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                            movie.getPoster_ImageUrl());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            movie.getOverview());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                            movie.getVote_average());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                            movie.getRelease_Date());

                    getContext().getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI,
                            movieValues
                    );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Favourite.setImageResource(R.drawable.staron);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void removeFromFavorites() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + movie.getId(), null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Favourite.setImageResource(R.drawable.staroff);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private boolean isFavorite() {
        Cursor movieCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + movie.getId(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }
}
