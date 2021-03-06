package com.qegame.bottomappbarcustom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.qegame.animsimple.Anim;

import java.util.ArrayList;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class BottomAppBarCustom extends LinearLayout {
    private final String TAG = "BottomAppBarCustom-ИНФ";

    /** Максимальное значение прогресса у ProgressBar */
    private final int MAX_PB = 360;

    public interface FABSettings {
        Drawable getImage();
        OnClickListener getClickListener();
        Anim getAnimation(Anim animDefault);
    }
    public interface IconSettings {
        Drawable getImage();
        OnClickListener getClickListener();
    }

    private FloatingActionButton fab;
    private BottomAppBar bottomAppBar;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;

    private LinearLayout icons_all_left;
    private AppCompatImageView[] images_all_left;
    private LinearLayout icons_left;
    private AppCompatImageView[] images_left;
    private LinearLayout icons_right;
    private AppCompatImageView[] images_right;

    private FABSettings fabSettings;

    private int colorPrimary;
    private int colorAccent;

    private Construction construction;

    private Anim anim_fabSwitchImage;

    public BottomAppBarCustom(Context context) {
        super(context);
        init(context, null);
    }
    public BottomAppBarCustom(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public BottomAppBarCustom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    public BottomAppBarCustom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.view_bottom_app_bar_custom,this);

        fab = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bab);
        coordinatorLayout = findViewById(R.id.coordinator);
        icons_all_left = findViewById(R.id.icons_all_left);
        icons_left = findViewById(R.id.icons_left);
        icons_right = findViewById(R.id.icons_right);

        fab.setElevation(getResources().getDimension(R.dimen.elevation_fab));
        bottomAppBar.setElevation(getResources().getDimension(R.dimen.elevation));

        this.anim_fabSwitchImage = Anim.animate(fab).scale(0f, 1f, Anim.DURATION_NORMAL, new OvershootInterpolator());

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFabSettings().getClickListener() == null) {
                    fab.setEnabled(false);
                    fab.setClickable(false);
                } else {
                    fab.setEnabled(true);
                    fab.setClickable(true);
                    getFabSettings().getClickListener().onClick(v);
                }
            }
        });

        images_all_left = new AppCompatImageView[icons_all_left.getChildCount()];
        for (int i = 0; i < icons_all_left.getChildCount(); i++) {
            images_all_left[i] = (AppCompatImageView) icons_all_left.getChildAt(i);
        }
        images_left = new AppCompatImageView[icons_left.getChildCount()];
        for (int i = 0; i < icons_left.getChildCount(); i++) {
            images_left[i] = (AppCompatImageView) icons_left.getChildAt(i);
        }
        images_right = new AppCompatImageView[icons_right.getChildCount()];
        for (int i = 0; i < icons_right.getChildCount(); i++) {
            images_right[i] = (AppCompatImageView) icons_right.getChildAt(i);
        }

        FABSettings fab = new FABSettings() {
            @Override
            public Drawable getImage() {
                return null;
            }

            @Override
            public OnClickListener getClickListener() {
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                };
            }

            @Override
            public Anim getAnimation(Anim animDefault) {
                return BottomAppBarCustom.this.anim_fabSwitchImage;
            }
        };

        setConstruction(new Construction.FABCenter(fab, null, null));


        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary, R.attr.colorAccent });

        this.colorPrimary = a.getColor(0, 0);
        this.colorAccent = a.getColor(1, 0);

        setColorPanel(colorPrimary);
        getFab().setBackgroundTintList(ColorStateList.valueOf(colorAccent));
    }

    //region Getters/Setters

    public FABSettings getFabSettings() {
        return fabSettings;
    }
    public void setFabSettings(FABSettings fabSettings) {
        this.fabSettings = fabSettings;
        if (fabSettings != null) {

            getFab().setImageDrawable(fabSettings.getImage());

            Anim anim = fabSettings.getAnimation(this.anim_fabSwitchImage);
            if (anim != null) anim.start();

            if (fabSettings.getClickListener() == null) {
                getFab().setEnabled(false);
                getFab().setClickable(false);
            } else {
                getFab().setEnabled(true);
                getFab().setClickable(true);
            }
        }
    }

    //endregion

    public FloatingActionButton getFab() {
        return fab;
    }
    public BottomAppBar getBottomAppBar() {
        return bottomAppBar;
    }

    public void setConstruction(Construction construction) {
        this.construction = construction;

        if (construction instanceof Construction.FABEnd) {
            Construction.FABEnd construct = (Construction.FABEnd) construction;
            construct(construct.fabSettings, BottomAppBar.FAB_ALIGNMENT_MODE_END);

            for (int i = 0; i < construct.iconSettings.length; i++) {
                images_all_left[i].setVisibility(VISIBLE);
                images_all_left[i].setImageDrawable(construct.iconSettings[i].getImage());
                final IconSettings iconSettings = construct.iconSettings[i];
                images_all_left[i].setOnClickListener(iconSettings.getClickListener());
            }
            Anim anim = new Anim.MoveLeft(icons_all_left, Anim.DURATION_NORMAL, new OvershootInterpolator());
            anim.start();
        }

        if (construction instanceof Construction.FABCenter) {
            Construction.FABCenter construct = (Construction.FABCenter) construction;
            construct(construct.fabSettings, BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);

            if (construct.iconSettings_left != null) {
                for (int i = 0; i < construct.iconSettings_left.length; i++) {
                    images_left[i].setVisibility(VISIBLE);
                    images_left[i].setImageDrawable(construct.iconSettings_left[i].getImage());
                    final IconSettings iconSettings = construct.iconSettings_left[i];
                    images_left[i].setOnClickListener(iconSettings.getClickListener());
                }
            }
            if (construct.iconSettings_right != null) {
                for (int i = 0; i < construct.iconSettings_right.length; i++) {
                    images_right[i].setVisibility(VISIBLE);
                    images_right[i].setImageDrawable(construct.iconSettings_right[i].getImage());
                    final IconSettings iconSettings = construct.iconSettings_right[i];
                    images_left[i].setOnClickListener(iconSettings.getClickListener());
                }
            }

            Anim anim_left = new Anim.MoveLeft(icons_left, Anim.DURATION_NORMAL, new OvershootInterpolator());
            anim_left.start();

            Anim anim_right = new Anim.MoveRight(icons_right, Anim.DURATION_NORMAL, new OvershootInterpolator());
            anim_right.start();
        }
    }
    public void showSnackBar(String text, int duration) {
        int marginSide = (int) getResources().getDimension(R.dimen.margin_side_snackbar);

        final Snackbar snackbar = Snackbar.make(this, text, duration);

        final View snackBarView = snackbar.getView();

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackBarView.getLayoutParams();
        params.setAnchorId(R.id.coordinator);
        params.anchorGravity = Gravity.TOP;
        params.gravity = Gravity.TOP;
        params.setMargins(marginSide, 0, marginSide, 0);
        snackBarView.setLayoutParams(params);

        // TODO: 21.04.2019 Add Animation

        snackbar.setAction("Ok", new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
        snackbar.setActionTextColor(colorAccent);

        snackbar.show();

    }
    public void showSnackBar(String text) {
        showSnackBar(text, 2000);
    }
    public void showProgressBar() {
        inflate(getContext(), R.layout.progress_bar_fab, (ViewGroup) findViewById(R.id.coordinator));

        progressBar = (ProgressBar) coordinatorLayout.getChildAt(coordinatorLayout.getChildCount() - 1);
        progressBar.setX(fab.getX());
        progressBar.setY(fab.getY());
        progressBar.getLayoutParams().height = fab.getHeight();
        progressBar.getLayoutParams().width = fab.getWidth();
        progressBar.setMax(MAX_PB);
    }
    public void removeProgressBar() {
        if (progressBar != null) {

            Anim anim = new Anim(progressBar);
            anim.scale(1f, 1.5f).alpha(0f).start();

           coordinatorLayout.removeView(progressBar);
        }
    }
    public void refreshProgressBar(int progress) {
        if (progressBar != null) {
            progressBar.setX(fab.getX());
            progressBar.setY(fab.getY());
            progressBar.getLayoutParams().height = fab.getHeight();
            progressBar.getLayoutParams().width = fab.getWidth();
            progressBar.setMax(MAX_PB);
            progressBar.setProgress(progress);
        }
    }
    public void setProgress(@IntRange(from = 0, to = MAX_PB) int value) {
        if (progressBar != null) {
            Anim.ViewAnimation.ProgressBarAnim.progressAnimation(progressBar, value);
        }
    }
    public void setProgressPercent(@FloatRange(from = 0.0, to = 100.0) float percent) {
        if (progressBar != null) {
            int step = (int) ((MAX_PB / 100.0) * percent);
            Anim.ViewAnimation.ProgressBarAnim.progressAnimation(progressBar, step);
        }
    }
    public void setColorPanel(int color) {
        icons_all_left.setBackgroundColor(color);
        icons_left.setBackgroundColor(color);
        icons_right.setBackgroundColor(color);
    }

    public void performClickIcon(int position) {
        if (this.construction instanceof Construction.FABEnd) {
            if (icons_all_left.getChildCount() > position) {
                if (icons_all_left.getChildAt(position).getVisibility() == VISIBLE) {
                    icons_all_left.getChildAt(position).performClick();
                }
            }
        }
        if (this.construction instanceof Construction.FABCenter) {
            if (icons_left.getChildCount() + icons_right.getChildCount() > position) {
                ArrayList<View> icons = new ArrayList<>();
                for (int i = 0; i < icons_left.getChildCount(); i++) {
                    icons.add(icons_left.getChildAt(i));
                }
                for (int i = 0; i < icons_right.getChildCount(); i++) {
                    icons.add(icons_right.getChildAt(i));
                }
                if (icons.get(position).getVisibility() == VISIBLE) {
                    icons.get(position).performClick();
                }
            }
        }
    }

    private void construct(final FABSettings fabSettings, int fabAlignment) {

        icons_all_left.setVisibility(GONE);
        icons_left.setVisibility(GONE);
        icons_right.setVisibility(GONE);

        setFabSettings(fabSettings);

        if (bottomAppBar.getFabAlignmentMode() != fabAlignment) {
            bottomAppBar.setFabAlignmentMode(fabAlignment);
        }
        for (AppCompatImageView anImages : images_all_left) {
            anImages.setVisibility(GONE);
        }
        for (AppCompatImageView anImages : images_left) {
            anImages.setVisibility(GONE);
        }
        for (AppCompatImageView anImages : images_right) {
            anImages.setVisibility(GONE);
        }
    }

    public static abstract class Construction {

        private Construction() {

        }

        public static final class FABEnd extends Construction {

            private FABSettings fabSettings;
            private IconSettings[] iconSettings;

            /** Конструкция BottomAppBar при котрой FloatingActionButton находится справа.
             * Слева находятся View.
             * @param fabSettings Изменения в FloatingActionButton
             * @param iconSettings Массив View (от 0 да 4) которые будут расположены слева.
             * У View будут установленны нужные размеры и отступы.*/
            public FABEnd(FABSettings fabSettings, @Size(min = 0, max = 4) IconSettings...iconSettings)
            {
                this.fabSettings = fabSettings;
                this.iconSettings = iconSettings;
            }
        }
        public static final class FABCenter extends Construction {

            private FABSettings fabSettings;
            private IconSettings[] iconSettings_left;
            private IconSettings[] iconSettings_right;

            /** Конструкция BottomAppBar при котрой FloatingActionButton находится в центре.
             * Слева и Справа нахлдсят View.
             * @param fabSettings Изменения в FloatingActionButton
             * @param iconSettings_left Массив View (от 0 да 3) которые будут расположены слева.
             * @param iconSettings_right Массив View (от 0 да 3) которые будут расположены справа.
             * У View будут установленны нужные размеры и отступы.*/
            public FABCenter(FABSettings fabSettings,
                             @Size(min = 0, max = 2) IconSettings[] iconSettings_left,
                             @Size(min = 0, max = 2) IconSettings[] iconSettings_right)
            {
                this.fabSettings = fabSettings;
                this.iconSettings_left = iconSettings_left;
                this.iconSettings_right = iconSettings_right;
            }
        }
    }
}
