package com.khaled.creditcard;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.khaled.creditcard.databinding.FragmentCreditCardBinding;

import java.util.ArrayList;
import java.util.List;

public class CreditCardFragment extends Fragment {

    private FragmentCreditCardBinding mBinding;
    private boolean showingGray = true;
    private AnimatorSet inSet;
    private AnimatorSet outSet;
    private CreditCard creditCard;
    private List<CreditCardSubmitListener> creditCardSubmitListeners = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        creditCard = new CreditCard();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_credit_card, container, false);
        initViews();
        return mBinding.getRoot();
    }

    public void reset() {
        creditCard = new CreditCard();
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBinding.inputLayoutCvvCode.setVisibility(View.VISIBLE);
        flipToGray();
        mBinding.viewPager.setCurrentItem(0);
        mBinding.inputEditCardNumber.setText("");
        mBinding.inputEditExpiredDate.setText("");
        mBinding.inputEditCardHolder.setText("");
        mBinding.inputEditCvvCode.setText("");
        mBinding.inputEditCardNumber.requestFocus();
        showKeyboard(mBinding.inputEditCardNumber);
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void registerCreditCardSubmiListener(CreditCardSubmitListener creditCardSubmitListener) {
        creditCardSubmitListeners.add(creditCardSubmitListener);
    }

    public void unRegisterCreditCardSubmiListener(CreditCardSubmitListener creditCardSubmitListener) {
        creditCardSubmitListeners.remove(creditCardSubmitListener);
    }

    private void initViews() {
        View.OnClickListener onHelpClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "The CVV Number (\"CreditCard Verification Value\") is a 3 or 4 digit number on your credit and debit cards", Toast.LENGTH_LONG).show();
            }
        };

        mBinding.iconHelpGray.setOnClickListener(onHelpClickListener);
        mBinding.iconHelpBlue.setOnClickListener(onHelpClickListener);

        mBinding.inputEditCardNumber.addTextChangedListener(new TextWatcher() {

            private boolean lock;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    flipToBlue();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (lock || s.length() > 16) {
                    return;
                }
                lock = true;
                for (int i = 4; i < s.length(); i += 5) {
                    if (s.toString().charAt(i) != ' ') {
                        s.insert(i, " ");
                    }
                }
                lock = false;
            }
        });

        mBinding.textExpiredDate.addTextChangedListener(new TextWatcher() {

            private boolean lock;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (lock || s.length() > 4) {
                    return;
                }
                lock = true;
                if (s.length() > 2 && s.toString().charAt(2) != '/') {
                    s.insert(2, "/");
                }
                lock = false;
            }
        });

        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        PagerAdapter adapter = new MyPagerAdapter();
        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setClipToPadding(false);
        mBinding.viewPager.setPadding(width / 4, 0, width / 4, 0);
        mBinding.viewPager.setPageMargin(width / 14);
        mBinding.viewPager.setPagingEnabled(false);
        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        updateProgressBar(25);
                        mBinding.inputEditCardNumber.setFocusableInTouchMode(true);
                        mBinding.inputEditExpiredDate.setFocusable(false);
                        mBinding.inputEditCardHolder.setFocusable(false);
                        mBinding.inputEditCvvCode.setFocusable(false);
                        mBinding.inputEditCardNumber.requestFocus();
                        return;
                    case 1:
                        updateProgressBar(50);
                        mBinding.inputEditCardNumber.setFocusable(false);
                        mBinding.inputEditExpiredDate.setFocusableInTouchMode(true);
                        mBinding.inputEditCardHolder.setFocusable(false);
                        mBinding.inputEditCvvCode.setFocusable(false);
                        mBinding.inputEditExpiredDate.requestFocus();
                        return;
                    case 2:
                        updateProgressBar(75);
                        mBinding.inputEditCardNumber.setFocusable(false);
                        mBinding.inputEditExpiredDate.setFocusable(false);
                        mBinding.inputEditCardHolder.setFocusableInTouchMode(true);
                        mBinding.inputEditCvvCode.setFocusable(false);
                        mBinding.inputEditCardHolder.requestFocus();
                        return;
                    case 3:
                        updateProgressBar(100);
                        mBinding.inputEditCardNumber.setFocusable(false);
                        mBinding.inputEditExpiredDate.setFocusable(false);
                        mBinding.inputEditCardHolder.setFocusable(false);
                        mBinding.inputEditCvvCode.setFocusableInTouchMode(true);
                        mBinding.inputEditCvvCode.requestFocus();
                        return;
                    case 4:
                        mBinding.inputEditCardNumber.setFocusable(false);
                        mBinding.inputEditExpiredDate.setFocusable(false);
                        mBinding.inputEditCardHolder.setFocusable(false);
                        mBinding.inputEditCvvCode.setFocusable(false);
                        return;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mBinding.viewPager.setCurrentItem(mBinding.viewPager.getCurrentItem() + 1);
                    handled = true;
                }
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit();
                    handled = true;
                }
                return handled;
            }
        };

        mBinding.inputEditCardNumber.setOnEditorActionListener(onEditorActionListener);
        mBinding.inputEditExpiredDate.setOnEditorActionListener(onEditorActionListener);
        mBinding.inputEditCardHolder.setOnEditorActionListener(onEditorActionListener);
        mBinding.inputEditCvvCode.setOnEditorActionListener(onEditorActionListener);

        mBinding.inputEditCardNumber.requestFocus();

        inSet = (AnimatorSet) AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_in);
        outSet = (AnimatorSet) AnimatorInflater.loadAnimator(requireContext(), R.animator.card_flip_out);
    }


    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.input_layout_card_number;
                    break;
                case 1:
                    resId = R.id.input_layout_expired_date;
                    break;
                case 2:
                    resId = R.id.input_layout_card_holder;
                    break;
                case 3:
                    resId = R.id.input_layout_cvv_code;
                    break;
                case 4:
                    resId = R.id.space;
                    break;

            }
            return requireActivity().findViewById(resId);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }


        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    private void updateProgressBar(int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(mBinding.progressHorizontal, "progress", progress);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void submit() {
        mBinding.viewPager.setCurrentItem(4);
        creditCard.setCardNumber(mBinding.inputEditCardNumber.getText().toString());
        creditCard.setExpiredDate(mBinding.inputEditExpiredDate.getText().toString());
        creditCard.setCardHolder(mBinding.inputEditCardHolder.getText().toString());
        creditCard.setCvvCode(mBinding.inputEditCvvCode.getText().toString());
        notifyCreditCardListeners();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.inputLayoutCvvCode.setVisibility(View.INVISIBLE);
                requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                hideKeyboard(mBinding.inputEditCvvCode);
            }
        }, 300);
    }

    private void notifyCreditCardListeners() {
        for (CreditCardSubmitListener creditCardSubmitListener :
                creditCardSubmitListeners) {
            creditCardSubmitListener.onSubmit(creditCard);
        }
    }

    private void flipToGray() {
        if (!showingGray && !outSet.isRunning() && !inSet.isRunning()) {
            showingGray = true;

            mBinding.cardBlue.setCardElevation(0);
            mBinding.cardGray.setCardElevation(0);

            outSet.setTarget(mBinding.cardBlue);
            outSet.start();

            inSet.setTarget(mBinding.cardGray);
            inSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mBinding.cardGray.setCardElevation(convertDpToPixel(12, requireContext()));
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            inSet.start();
        }
    }

    private void flipToBlue() {
        if (showingGray && !outSet.isRunning() && !inSet.isRunning()) {
            showingGray = false;

            mBinding.cardGray.setCardElevation(0);
            mBinding.cardBlue.setCardElevation(0);

            outSet.setTarget(mBinding.cardGray);
            outSet.start();

            inSet.setTarget(mBinding.cardBlue);
            inSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mBinding.cardBlue.setCardElevation(convertDpToPixel(12, requireContext()));
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            inSet.start();
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }


}
