package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.feature.common.ui.adapter.ItemComparator;

public class SearchResultItemComparator implements ItemComparator {

    @Override
    public boolean areItemsTheSame(final DisplayableItem item1, final DisplayableItem item2) {
        return item1.equals(item2);
    }

    @Override
    public boolean areContentsTheSame(final DisplayableItem item1, final DisplayableItem item2) {
        // There is no state in any of the items shown in this list.
        return true;
    }
}
