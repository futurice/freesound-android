/*
 * Copyright 2016 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurice.freesound.feature.search;

import com.futurice.freesound.R;

import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import android.view.View;

import polanski.option.Option;

import static com.futurice.freesound.common.utils.Preconditions.checkNotNull;

/**
 * A helper class, which can display and dismiss a SnackBar
 */
class SearchSnackbar {

    private Snackbar snackbar;

    /**
     * Shows a snackbar  indefinitely in the first parent view found above the provided view.
     * Dismisses a previous SnackBar if one was shown before.
     *
     * @param view         The view supposed to show the SnackBar. If not a CoordinatorLayout,
     *                     SnackBar will find the first parent view which is suitable to hold a
     *                     SnackBar
     * @param charSequence The message to be displayed in the SnackBar
     */
    void showNewSnackbar(@NonNull View view, @NonNull final CharSequence charSequence) {
        checkNotNull(view);
        checkNotNull(charSequence);
        dismissSnackbar();

        snackbar = Snackbar.make(view, charSequence, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(android.R.string.ok, __ -> dismissSnackbar());
        snackbar.setActionTextColor(ContextCompat.getColor(view.getContext(),
                R.color.colorContrastAccent));
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
