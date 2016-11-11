package com.futurice.freesound.feature.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import polanski.option.Option;

import static com.futurice.freesound.utils.Preconditions.checkNotNull;

class SearchSnackbar {

    @Nullable
    private Snackbar snackbar;

    void showNewSnackbar(@NonNull View view, @NonNull final CharSequence charSequence) {
        checkNotNull(view);
        checkNotNull(charSequence);
        dismissSnackbar();

        snackbar = Snackbar.make(view, charSequence, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    void dismissSnackbar() {
        Option.ofObj(snackbar)
              .ifSome(Snackbar::dismiss);
    }
}
