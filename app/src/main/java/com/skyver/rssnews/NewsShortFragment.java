package com.skyver.rssnews;

import android.app.Activity;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyver.rssnews.retrofit.Article;
import com.skyver.rssnews.viewmodel.MyViewModel;
import com.skyver.rssnews.viewmodel.MyViewModelFactory;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;



public class NewsShortFragment extends LifecycleFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    boolean mDualPane;
    int mCurCheckPosition = 0;

    private MyViewModel model;
    private List<Article> mArticleList;

    private RecyclerView recyclerView;


    @Inject
    MyViewModelFactory myViewModelFactory;

    public NewsShortFragment() {
        // Required empty public constructor
    }

    public static NewsShortFragment newInstance(String param1) {
        NewsShortFragment fragment = new NewsShortFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MyApplication) getActivity().getApplication()).getNetComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity(), myViewModelFactory).get(MyViewModel.class);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            showDetails(mCurCheckPosition);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);

        model.getNewsFromDB().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(@Nullable List<Article> articleList) {
                if(articleList == null){
                    return;
                }

                mArticleList = articleList;
                ContentAdapter adapter = new ContentAdapter(mArticleList, getContext());

                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //TODO CHECK IF NEEDED
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            }
        });


        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView picture;
        public TextView title;
        public TextView description;
        public TextView time;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            picture = itemView.findViewById(R.id.list_news_pic);
            title =  itemView.findViewById(R.id.list_news_title);
            description =  itemView.findViewById(R.id.list_news_text);
            time =  itemView.findViewById(R.id.list_news_time);

        }
    }

     class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Article> articleList;
        private  Context context;
        private int selectedPos = -1;

        public ContentAdapter( List<Article> articleList, Context context) {
             this.articleList  = articleList;
             this.context  = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            Article item = articleList.get(position);
            holder.title.setText(item.getTitle());
            holder.description.setText(Html.fromHtml(item.getFulltext()));
            holder.time.setText(item.getPubDate());

            String imageUrl = item.getImageFile();

            if(imageUrl != null){//load from file
                Picasso.with(getActivity()).load(new File(imageUrl)).into(holder.picture);
            } else {//load from url

                String row = item.getImage();
                String url = row.substring(row.indexOf("http"), row.lastIndexOf("\""));
                Picasso.with(context)
                        .load(url)
                        .fit()
                        .centerCrop()
                        .into(holder.picture);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    model.select(holder.getAdapterPosition());
                    selectedPos = holder.getAdapterPosition();

                    notifyItemChanged(selectedPos);
                    showDetails(selectedPos);

                }
            });
        }

        @Override
        public int getItemCount() {
            return articleList.size();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {
            // Check what fragment is currently shown, replace if needed.
            DetailsFragment details = (DetailsFragment)
                    getFragmentManager().findFragmentById(R.id.details);
            if (details == null ) {
                // Make new fragment to show this selection.
                details = DetailsFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (index == 0) {
                    ft.replace(R.id.details, details);
                }

                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsActivity.class);
            intent.putExtra(DetailsFragment.ARG_PARAM1, index);
            startActivity(intent);
        }
    }

}
