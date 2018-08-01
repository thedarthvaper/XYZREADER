package com.example.xyzreader.ui;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;

import java.util.ArrayList;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment{
        //LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";
    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private CoordinatorLayout mCoordinatorLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private View mPhotoContainerView;
    private ImageView mPhotoView;
    private FloatingActionButton shareActionButton;
    private TextView bylineView;
    private TextView titleView;
    private TextView bodyView;
    private AppBarLayout appBarLayout;
    private LinearLayout metaBar;
    String title;
    ArrayList<String> arrayList=new ArrayList<>();

    public ArticleDetailFragment() {
    }

    public ArticleDetailFragment(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mCoordinatorLayout = (CoordinatorLayout)mRootView.findViewById(R.id.draw_insets_frame_layout);
        mPhotoView = (ImageView) mRootView.findViewById(R.id.photo);
        mPhotoContainerView = mRootView.findViewById(R.id.photo_container);
        appBarLayout=(AppBarLayout)mRootView.findViewById(R.id.toolbar_det);
        shareActionButton = (FloatingActionButton) mRootView.findViewById(R.id.share_fab);
        titleView = (TextView) mRootView.findViewById(R.id.article_title);
        bylineView = (TextView) mRootView.findViewById(R.id.article_byline);
        bodyView = (TextView) mRootView.findViewById(R.id.article_body);
        metaBar = (LinearLayout) mRootView.findViewById(R.id.meta_bar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) mRootView.findViewById(R.id.photo_container);
        mToolbar = (Toolbar) mRootView.findViewById(R.id.detail_toolbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbarLayout.setTitle(title);
                    isShow = true;
                } else if(isShow) {
                    mCollapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        mRootView.setVisibility(View.VISIBLE);
        title = arrayList.get(0);

        bylineView.setText(Html.fromHtml(
                DateUtils.getRelativeTimeSpanString(
                        Long.parseLong(arrayList.get(1)),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL).toString()
                        + " by <font color='#ffffff'>"
                        + arrayList.get(2)
                        + "</font>"));

        bodyView.setText(Html.fromHtml(arrayList.get(3)));
        if (mToolbar != null) {
            ((ArticleDetailActivity) getActivity()).setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            ((ArticleDetailActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }
        Typeface mainTypeface = Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf");
        titleView.setText(title);
        bodyView.setTypeface(mainTypeface);
        bylineView.setTypeface(mainTypeface);
        titleView.setTypeface(mainTypeface);
        Glide.with(getActivity())
                .load(arrayList.get(4))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                int defaultColor = 0xFF333333;
                                int color = palette.getDarkVibrantColor(defaultColor);
                                metaBar.setBackgroundColor(color);
                                if (mCollapsingToolbarLayout != null) {
                                    int scrimColor = palette.getDarkMutedColor(defaultColor);
                                    mCollapsingToolbarLayout.setStatusBarScrimColor(scrimColor);
                                    mCollapsingToolbarLayout.setContentScrimColor(scrimColor);
                                }
                            }
                        });


                        return false;
                    }
                })
                .into(mPhotoView);

        shareActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));

            }
        });
        return mRootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            shareActionButton.setAlpha(0f);
            shareActionButton.setScaleX(0f);
            shareActionButton.setScaleY(0f);
            shareActionButton.setTranslationZ(1f);
            shareActionButton.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationZ(25f)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setStartDelay(300)
                    .start();
        }
    }
}