/*
 * Copyright (c) 2021 Auxio Project
 * HomeListFragment.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oxycblt.auxio.home.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import me.zhanghai.android.fastscroll.PopupTextProvider
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.memberBinding
import org.oxycblt.auxio.util.applySpans
import org.oxycblt.auxio.util.resolveDrawable

/**
 * A Base [Fragment] implementing the base features shared across all detail fragments.
 *
 */
abstract class HomeListFragment : Fragment() {
    protected val binding: FragmentHomeListBinding by memberBinding(
        FragmentHomeListBinding::inflate
    )

    protected val homeModel: HomeViewModel by activityViewModels()
    protected val playbackModel: PlaybackViewModel by activityViewModels()

    abstract val popupProvider: PopupTextProvider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeRecycler.apply {
            FastScrollerBuilder(this)
                .useMd2Style()
                .setPopupTextProvider(popupProvider)
                .setTrackDrawable(R.drawable.ui_scroll_track.resolveDrawable(context))
                .build()
        }
    }

    protected fun <T : BaseModel, VH : RecyclerView.ViewHolder> setupRecycler(
        @IdRes uniqueId: Int,
        homeAdapter: HomeAdapter<T, VH>,
        homeData: LiveData<List<T>>,
    ) {
        binding.homeRecycler.apply {
            id = uniqueId
            adapter = homeAdapter
            setHasFixedSize(true)
            applySpans()
        }

        // Make sure that this RecyclerView has data before startup
        homeData.observe(viewLifecycleOwner) { data ->
            homeAdapter.updateData(data)
        }
    }

    abstract class HomeAdapter<T : BaseModel, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
        protected var data = listOf<T>()

        @SuppressLint("NotifyDataSetChanged")
        fun updateData(newData: List<T>) {
            data = newData

            // notifyDataSetChanged here is okay, as we have no idea how the layout changed when
            // we re-sort and ListAdapter causes the scroll position to get messed up
            notifyDataSetChanged()
        }
    }
}