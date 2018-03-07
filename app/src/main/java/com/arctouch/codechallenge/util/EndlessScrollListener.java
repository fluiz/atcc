package com.arctouch.codechallenge.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by Fabio on 07/03/2018.
 */

public abstract class EndlessScrollListener extends OnScrollListener {
    private int remainingItemsBeforeNewLoad = 5;
    private int currentPage = 1;
    private int previousTotalCount = 0;
    private boolean loading = true;
    private int startingPage = 1;

    RecyclerView.LayoutManager layoutManager;

    public EndlessScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public int getLastVisibleItem(int[] itemPositions) {
        int maxSize = 0;
        for (int i = 0; i< itemPositions.length; i++) {
            if (i == 0) {
                maxSize = itemPositions[i];
            } else if (itemPositions[i] > maxSize) {
                maxSize = itemPositions[i];
            }
        }
        return maxSize;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = layoutManager.getItemCount();

        if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }

        if (totalItemCount < previousTotalCount) {
            currentPage = startingPage;
            previousTotalCount = totalItemCount;
            if (totalItemCount == 0) {
                loading = true;
            }
        }

        if (loading && totalItemCount > previousTotalCount) {
            loading = false;
            previousTotalCount = totalItemCount;
        }

        if (!loading && (lastVisibleItemPosition + remainingItemsBeforeNewLoad) > totalItemCount) {
            currentPage++;
            loadMoreItems(currentPage);
            loading = true;
        }
    }

    public void resetState() {
        currentPage = startingPage;
        previousTotalCount = 0;
        loading = true;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        currentPage = page;
    }

    public abstract void loadMoreItems(int desiredPage);
}
