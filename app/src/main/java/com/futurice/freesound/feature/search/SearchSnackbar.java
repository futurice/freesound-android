package com.futurice.freesound.feature.search;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by mast on 25/09/16.
 */
public class SearchSnackbar {
    private Snackbar snackbar;

    public SearchSnackbar() {
    }
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
