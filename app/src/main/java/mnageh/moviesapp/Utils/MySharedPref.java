/*package mnageh.moviesapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import mnageh.moviesapp.Models.movie_Model;

public class MySharedPref {
    ArrayList<String> MoviesFavourite_List;
    Context context;
    String File_Name;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public MySharedPref(Context context, String FileName){
        this.context=context;
        this.File_Name=FileName;
        this.sharedPref = context.getSharedPreferences(FileName, Context.MODE_PRIVATE);
        this.editor=sharedPref.edit();
        MoviesFavourite_List=new ArrayList<>();
    }
    public  void SaveData(movie_Model movie){
        Gson gson = new Gson();
        String json = gson.toJson(movie);
        Log.d("gason", json);
        editor.putString(movie.getId(),json);
        editor.commit();
        MoviesFavourite_List.add(movie.getId());
        Gson gson1=new Gson();
        String FavouriteList =gson1.toJson(MoviesFavourite_List);
        editor.putString("Favourites",FavouriteList);
        editor.commit();
    }

    public movie_Model RetriveData(String id){
        Gson gson = new Gson();
        String json = sharedPref.getString(id,"");
        if(json.equals(""))
            return null;
        movie_Model m = gson.fromJson(json, movie_Model.class);
        return m ;
    }
    public void RemoveMovie(String id){
        editor.remove(id);
        editor.commit();
        MoviesFavourite_List=getFavouriteMovielist();
        MoviesFavourite_List.remove(id);
        Gson gson1=new Gson();
        String FavouriteList =gson1.toJson(MoviesFavourite_List);
        editor.putString("Favourites",FavouriteList);
        editor.commit();
    }

    public boolean CheckFavourite(String id){
        if(getFavouriteMovielist()==null)
             return false;
        else
            MoviesFavourite_List = getFavouriteMovielist();
        for (String s : MoviesFavourite_List)
            if(s.equals(id))
                return true;
        return false;
    }

    public void setUserSetting(String UserSetting){
        editor.putString("UserSetting",UserSetting);
        editor.commit();
    }

    public String getUserSetting(){
        String UserSetting=sharedPref.getString("UserSetting","");
        return UserSetting;
    }

    public boolean IsFirstTime(){
        String check=sharedPref.getString("FirstTime","");
        if(check.equals("yes"))
            return false;
         return true;
    }

    public void FirstTime(){
        editor.putString("FirstTime","yes");
        editor.commit();
    }

    public void Clear(){
        editor.clear();
        editor.commit();
    }

    public ArrayList<String> getFavouriteMovielist(){
        Gson gson = new Gson();
        String json = sharedPref.getString("Favourites","");
        if(json.equals(""))
            return null;
        ArrayList<String> list=new ArrayList<>();
        Type type=new TypeToken<ArrayList<String>>() {}.getType();
        list = gson.fromJson(json,type );
        return list;
    }
}
*/