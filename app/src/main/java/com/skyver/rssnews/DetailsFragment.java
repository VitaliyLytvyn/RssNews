package com.skyver.rssnews;

import android.app.Activity;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.skyver.rssnews.retrofit.Article;
import com.skyver.rssnews.viewmodel.MyViewModel;
import com.skyver.rssnews.viewmodel.MyViewModelFactory;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;



public class DetailsFragment extends LifecycleFragment {

    public static final String ARG_PARAM1 = "param1";

    private Integer mParam1;
    private MyViewModel model;
    boolean mDualPane;
    private TextView mTextView;
    private Article mArticle;

    @Inject
    MyViewModelFactory myViewModelFactory;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance( Integer param1) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
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
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        }

        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        mTextView = v.findViewById(R.id.textView);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        if(fab != null){
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = mArticle.getLink();
                Intent intent = WebViewActivity.makeIntent(getActivity(), url);
                startActivity(intent);
            }
            });
        }

        model.getSelected().observe(this, new Observer<Article>() {
            @Override
            public void onChanged(@Nullable Article article) {
                if(article == null){
                    return;
                }

                mArticle = article;
                fillViewWithContent(article);
            }
        });

        return v;
    }

    private void fillViewWithContent(Article article){
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        String s = article.getFulltext();

        mTextView.setText(Html.fromHtml(s));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View detailsFrame = getActivity().findViewById(R.id.titles);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (!mDualPane) {

            model.getNewsFromDB().observe(this, new Observer<List<Article>>() {
                @Override
                public void onChanged(@Nullable List<Article> articleList) {
                    if(articleList == null){
                        return;
                    }

                    if(mParam1 < articleList.size()){
                        Article article = articleList.get(mParam1);
                        mArticle = article;
                        fillViewWithContent(article);
                    }

                }
            });

        }
    }

}
