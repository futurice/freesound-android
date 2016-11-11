package com.futurice.freesound.feature.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import polanski.option.Option;

import static com.futurice.freesound.utils.Preconditions.checkNotNull;

/**
 * A helper class, which can display and dismiss a SnackBar
 */
class SearchSnackbar {

    @Nullable
    private Snackbar snackbar;

    /**
     * Shows a snackbar  indefinitely in the first parent view found above the provided view.
     * Dismisses a previous SnackBar if one was shown before.
     *
     * @param view The view supposed to show the SnackBar. If not a CoordinatorLayout, SnackBar will find the first parent view which is suitable to hold a SnackBar
     * @param charSequence The message to be displayed in the SnackBar
     */
    void showNewSnackbar(@NonNull View view, @NonNull final CharSequence charSequence) {
        checkNotNull(view);
        checkNotNull(charSequence);
        dismissSnackbar();

        snackbar = Snackbar.make(view, charSequence, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    /**
     * Dismisses a previously shown SnackBar if it exists
     */
    void dismissSnackbar() {
        Option.ofObj(snackbar)
              .ifSome(Snackbar::dismiss);
    }
}
