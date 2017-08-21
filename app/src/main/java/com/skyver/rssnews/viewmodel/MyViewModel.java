package com.skyver.rssnews.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.skyver.rssnews.database.RssDAO;
import com.skyver.rssnews.retrofit.Article;
import com.skyver.rssnews.retrofit.RSSMain;
import com.skyver.rssnews.retrofit.Restapi;
import com.skyver.rssnews.util.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

import static com.skyver.rssnews.util.Constants.IMAGE_DIRCTORY;
import static com.skyver.rssnews.util.Constants.IMAGE_POSTEFIX;
import static com.skyver.rssnews.util.Constants.IMAGE_PREFIX;

/**
 * Created by skyver on 8/19/17.
 */

public class MyViewModel extends ViewModel {

    private MutableLiveData<RSSMain> mRssMain;
    public LiveData<List<Article>> mArticleList;

    private Call<RSSMain> call;
    private Application application;
    private Retrofit retrofit;
    private RssDAO rssDAO;
    private Executor executor;

    public MyViewModel(Application application, Retrofit retrofit, RssDAO rssDAO, Executor executor) {
        this.application = application;
        this.retrofit = retrofit;
        this.rssDAO = rssDAO;
        this.executor = executor;
    }

    private final MutableLiveData<Article> selected = new MutableLiveData<Article>();

    public void select(int item) {
        if(mArticleList.getValue() != null
                && item < mArticleList.getValue().size())
        selected.setValue(mArticleList.getValue().get(item));
    }

    public LiveData<Article> getSelected() {
        return selected;
    }


    public LiveData<List<Article>> getNewsFromDB() {

        if (mArticleList == null) {
            mArticleList = new MutableLiveData<>();
        }

        mArticleList = rssDAO.getArticles();
        return mArticleList;
    }

    public LiveData<Article> getArticleByLinc(final String link) {
        return rssDAO.getArticleById(link);
    }

    public void start(){
        loadNews();
    }

    public void loadNews() {

        //Create a retrofit call object
        call = retrofit.create(Restapi.class).loadRSSFeed();

        //Enqueue the call
        call.enqueue(new Callback<RSSMain>() {
            @Override
            public void onResponse(Call<RSSMain> call, Response<RSSMain> response) {

                if (response.isSuccessful()) {
                    final RSSMain rssResponce = response.body();
                    if(rssResponce != null){

                        //////////////////TODO CHECK
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {

                                Long[] res = rssDAO.insertAllArticles(rssResponce.getArticleList());

                            }
                        });
                        //////////////////
                    }

                    Timber.d("response.body(): "+ response.body());

                } else {
                    Timber.d("response NOT Successful()");
                    Timber.d("response.body(): "+ response.body());

                }
            }

            @Override
            public void onFailure(Call<RSSMain> call, Throwable t) {

                Timber.d("response onFailure toString: "+t.toString());
            }
        });
    }

    public void deleteAll(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                rssDAO.deleteAllArticles();
            }
        });

        Article article = new Article();
        article.setFulltext("");
        selected.setValue(article);

    }

    private void loadImage(final Article article){
        String row = article.getImage();
        String url = row.substring(row.indexOf("http"), row.lastIndexOf("\""));
        article.setImage(url);
        String fileName = Constants.makeFileName(article.getGuid());

        Picasso.with(application).load(url).into(picassoImageTarget(application, IMAGE_DIRCTORY, fileName,  article));


    }

    private Target picassoImageTarget(Context context, final String imageDir, final String imageName, final Article article) {
        Timber.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(fos != null){
                                    fos.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Timber.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }

    @Override
    protected void onCleared() {
        if(call != null && !call.isCanceled()){
            call.cancel();
            call = null;
        }
    }

}

