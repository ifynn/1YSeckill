package com.fynn.oyseckill.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.widget.Checkable;
import android.widget.ImageView;

import com.fynn.oyseckill.R;

/**
 * Created by Fynn on 2016/3/9.
 */
public class Switcher extends ImageView implements Checkable {

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };
    private boolean isChecked;
    private OnCheckedChangeListener onCheckedChangeListener;
    private boolean mBroadcasting;

    public Switcher(Context context) {
        super(context);
    }

    public Switcher(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Switcher);
        isChecked = a.getBoolean(R.styleable.Switcher_checked, false);
        a.recycle();

        setChecked(isChecked);
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            isChecked = checked;
            refreshDrawableState();

            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, isChecked);
            }
            mBroadcasting = false;
        }
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + CHECKED_STATE_SET.length);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (getDrawable() != null) {
            int[] myDrawableState = getDrawableState();

            // Set the state of the Drawable
            getDrawable().setState(myDrawableState);

            invalidate();
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == getDrawable();
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (getDrawable() != null) getDrawable().jumpToCurrentState();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        ss.checked = isChecked();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a Checkable Switcher has changed.
         *
         * @param switcher  The switcher whose state has changed.
         * @param isChecked The new checked state of switcher.
         */
        void onCheckedChanged(Switcher switcher, boolean isChecked);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        boolean checked;

        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean) in.readValue(null);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
        }
    }
}
