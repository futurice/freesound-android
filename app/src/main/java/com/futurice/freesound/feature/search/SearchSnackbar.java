package com.futurice.freesound.feature.search;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SearchSnackbar {
    private Snackbar snackbar;

    public void showNewSnackbar(View view, CharSequence charSequence){
        if(snackbar != null){
            snackbar.dismiss();
        }
        snackbar = Snackbar.make(view, charSequence, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }
    public void dismissSnackbar(){
        if(snackbar != null){
            snackbar.dismiss();
        }
    }
}
