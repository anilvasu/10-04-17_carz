package com.moyo.carzrideon.Fragments;

/**
 * Created by nikhi on 10/20/2016.
 */

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.TextView;

import com.moyo.carzrideon.Activitites.ContactUsActivity;
import com.moyo.carzrideon.R;


/**
 * Created by MG on 17-07-2016.
 */
public class BottomSheetFragment_ChooseImage extends BottomSheetDialogFragment {

    String mString;
    private static BottomSheetBehavior mBottomSheetBehavior;


    public static BottomSheetFragment_ChooseImage newInstance(String string) {
        BottomSheetFragment_ChooseImage f = new BottomSheetFragment_ChooseImage();
        Bundle args = new Bundle();
        args.putString("string", string);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mString = getArguments().getString("string");
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View v = View.inflate(getContext(), R.layout.layout_fragment_bottomsheet_chooseimage, null);
        dialog.setContentView(v);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) v.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            mBottomSheetBehavior = (BottomSheetBehavior) behavior;
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });

            TextView camera = (TextView) v.findViewById(R.id.camera);
            TextView gallery = (TextView) v.findViewById(R.id.gallery);

            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    getDialog().cancel();
                    ((ContactUsActivity)getActivity()).takePicture();

                }
            });

            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().cancel();
                    ((ContactUsActivity)getActivity()).selectgImageFromGallery();
                }
            });
        }


    }



   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_fragment_locationpreferences, container, false);




        return v;
    }*/
}