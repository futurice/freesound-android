package com.futurice.freesound.feature.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import polanski.option.Option;

public class SearchSnackbar {

    @Nullable
    private Snackbar snackbar;

    public void showNewSnackbar(@NonNull View view, @NonNull final CharSequence charSequence) {
        Option.ofObj(snackbar)
              .ifSome(Snackbar::dismiss);

        snackbar = Snackbar.make(view, charSequence, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    public void dismissSnackbar() {
        Option.ofObj(snackbar)
              .ifSome(Snackbar::dismiss);
    }
}
