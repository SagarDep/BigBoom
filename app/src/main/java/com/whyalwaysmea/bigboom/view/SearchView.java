package com.whyalwaysmea.bigboom.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.whyalwaysmea.bigboom.R;
import com.whyalwaysmea.bigboom.utils.KeyBoardUtils;
import com.whyalwaysmea.bigboom.utils.MeasureUtil;

/**
 * Created by Long
 * on 2016/9/23.
 */

public class SearchView {

    private Context mContext;
    private ImageView mSearchBack;
    private ImageView mSearchClear;
    private ImageView mSearchSure;
    private EditText mSearchInput;
    private OnSearchClickListener mOnSearchClickListener;

    private View showAtView;
    private PopupWindow mPopupWindow;

    public SearchView(Context context, View view, OnSearchClickListener onSearchClickListener) {
        this.mContext = context;
        this.mOnSearchClickListener = onSearchClickListener;
        this.showAtView = view;
        initView();
        initEvent();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_view, null);
        mSearchBack = (ImageView) view.findViewById(R.id.search_back);
        mSearchClear = (ImageView) view.findViewById(R.id.search_clear);
        mSearchSure = (ImageView) view.findViewById(R.id.search_sure);
        mSearchInput = (EditText) view.findViewById(R.id.search_input);

        mPopupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(showAtView, Gravity.NO_GRAVITY, 0, MeasureUtil.getStatusBarHeight(mContext));

        final WindowManager.LayoutParams lp = ((Activity)mContext).getWindow().getAttributes();
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0.3f);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            lp.alpha = (float) animation.getAnimatedValue();
            lp.dimAmount = (float) animation.getAnimatedValue();
            ((Activity)mContext).getWindow().setAttributes(lp);
            ((Activity)mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        });
        animator.start();

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ValueAnimator animator = ValueAnimator.ofFloat(0.7f, 1f);
                animator.setDuration(500);
                animator.addUpdateListener(animation -> {
                    lp.alpha = (float) animation.getAnimatedValue();
                    lp.dimAmount = (float) animation.getAnimatedValue();
                    ((Activity)mContext).getWindow().setAttributes(lp);
                    ((Activity)mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                });
                animator.start();
            }
        });

        KeyBoardUtils.toggleSoftInput(mContext);

    }

    private void initEvent() {
        RxView.clicks(mSearchBack).subscribe(aVoid -> closeSearchView());
        RxView.clicks(mSearchClear).subscribe(aVoid -> mSearchInput.setText(""));
        RxView.clicks(mSearchSure).subscribe(aVoid -> OnSearch());
        RxTextView.textChanges(mSearchInput).subscribe(this::OntextChanges);

    }

    public interface OnSearchClickListener {
        void searchInput(String s);
    }

    private void closeSearchView() {
        mPopupWindow.dismiss();
    }

    private void OnSearch() {
        if(mOnSearchClickListener != null && mSearchInput.length() > 0) {
            String s = mSearchInput.getText().toString().trim();
            mOnSearchClickListener.searchInput(s);
        } else {

        }
    }

    private void OntextChanges(CharSequence s) {
        if(s.length() > 0) {
            mSearchClear.setVisibility(View.VISIBLE);
        } else {
            mSearchClear.setVisibility(View.GONE);
        }
    }
}
