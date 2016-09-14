package com.whyalwaysmea.bigboom.module.movie.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.whyalwaysmea.bigboom.R;
import com.whyalwaysmea.bigboom.base.BaseView;
import com.whyalwaysmea.bigboom.base.MvpFragment;
import com.whyalwaysmea.bigboom.bean.MovieInfo;
import com.whyalwaysmea.bigboom.bean.MovieListResponse;
import com.whyalwaysmea.bigboom.module.movie.presenter.MovieListPresenterImp;
import com.whyalwaysmea.bigboom.module.movie.ui.adapter.Top250MovieAdapter;
import com.whyalwaysmea.bigboom.module.movie.view.IMovieListView;
import com.whyalwaysmea.bigboom.view.MyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Long
 * on 2016/9/5.
 */
public class Top250MovieListFragment extends MvpFragment<IMovieListView, MovieListPresenterImp> implements IMovieListView, SwipeRefreshLayout.OnRefreshListener, MyRecyclerView.OnLoadMoreListener {


    @BindView(R.id.recyclerview)
    MyRecyclerView mRecyclerView;
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private GridLayoutManager mLayoutManager;
    private MovieListPresenterImp mMovieListPresenter;
    private List<MovieInfo> mTop250Movies;
    private Top250MovieAdapter mTop250MovieAdapter;
    private int start = 0;
    private int count = 20;


    public static Top250MovieListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        Top250MovieListFragment fragment = new Top250MovieListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
    }

    @Override
    protected MovieListPresenterImp createPresenter(BaseView view) {
        mMovieListPresenter = new MovieListPresenterImp(this);
        return mMovieListPresenter;
    }

    @Override
    protected void initView() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.material_amber_500, R.color.material_blue_500,
                R.color.material_cyan_500, R.color.material_deep_purple_500);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new GridLayoutManager(getContext(), 1);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mTop250Movies = new ArrayList<>();
        mTop250MovieAdapter = new Top250MovieAdapter(getContext(), mTop250Movies, true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mTop250MovieAdapter);
        mRecyclerView.setOnLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        onRefresh();
    }

    @Override
    public void setData(MovieListResponse movieListResponse) {
        if(start == 0) {
            mTop250Movies.clear();
        }
        mTop250Movies.addAll(movieListResponse.getSubjects());
        mTop250MovieAdapter.notifyDataSetChanged();
        start = movieListResponse. getCount();
    }


    @Override
    public void onRefresh() {
        start = 0;
        mMovieListPresenter.loadTop250(start, count);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mSwipeRefreshLayout.post(() ->mSwipeRefreshLayout.setRefreshing(true));

    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        mSwipeRefreshLayout.post(() ->mSwipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public void onLoadMore() {
        if(!mSwipeRefreshLayout.isRefreshing()) {
            mMovieListPresenter.loadTop250(start, count);
        }
    }


}
