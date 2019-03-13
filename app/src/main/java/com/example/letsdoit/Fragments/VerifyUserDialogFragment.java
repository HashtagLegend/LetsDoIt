package com.example.letsdoit.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.letsdoit.Authentication.RegisterUserActivity;
import com.example.letsdoit.MainActivity;
import com.example.letsdoit.R;



public class VerifyUserDialogFragment extends DialogFragment {
    private static final String TITLE = "Bekræft venligs email!";
    private static final String RESEND_BTN_TEXT = "Gensend bekræftelses mail";
    private static final String LOGIN_BUTTON_TEXT = "Login";
    private static final String MESSAGE = "Message";

    public static VerifyUserDialogFragment newInstance(String title, String message, String resendVerifyMailButtonText, String loginButtonText) {
        VerifyUserDialogFragment fragment = new VerifyUserDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(MESSAGE, message);
        bundle.putString(RESEND_BTN_TEXT, resendVerifyMailButtonText);
        bundle.putString(LOGIN_BUTTON_TEXT, loginButtonText);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        String message = getArguments().getString(MESSAGE);
        String resendVerifyMailButtonText = getArguments().getString(RESEND_BTN_TEXT);
        String loginButtonText = getArguments().getString(LOGIN_BUTTON_TEXT);
        final RegisterUserActivity enclosingActivity = (RegisterUserActivity) getActivity();
        // Builder pattern used!
        // http://www.javaworld.com/article/2074938/core-java/too-many-parameters-in-java-methods-part-3-builder-pattern.html
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(resendVerifyMailButtonText, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int whichWasClicked) {
                                enclosingActivity.resendVerificationEmail();
                            }
                        }
                )
                .setNegativeButton(loginButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int whichWasClicked) {
                        enclosingActivity.loginWhenVerified();
                    }
                });
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }
}

