package com.locatocam.app.utility

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener
/**
 * Supporting only LinearLayoutManager for now.
 *
 * @param layoutManager
 */
    (var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading() && !isLastPage()) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0&&totalItemCount >= PaginationListener.PAGE_SIZE) {
                loadMoreItems()
            }//                    && totalItemCount >= ClothesFragment.itemsCount
        }
    }
    abstract fun loadMoreItems()
    companion object {
        const val PAGE_START = 1
        private const val PAGE_SIZE = 10
    }
}