package com.fduhole.danxinative.ui.fdu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fduhole.danxinative.databinding.FragmentEcardBinding
import com.fduhole.danxinative.databinding.ItemListTileBinding
import com.fduhole.danxinative.model.CardRecord
import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.util.lifecycle.watch
import com.fduhole.danxinative.util.toDateTimeString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class ECardUiState(
    val flow: Flow<PagingData<CardRecord>>? = null,
)

class ECardFragment : Fragment(), KoinComponent {

    private val viewModel: ECardViewModel by viewModels()
    private val globalState: GlobalState by inject()
    private lateinit var binding: FragmentEcardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEcardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            (activity as AppCompatActivity).supportActionBar?.title = "校园卡消费记录"
            val pagingAdapter = ECardAdapter(ECardComparator)
            viewModel.initModel()
            // Why using the identical adapters here as header and footer?
            //
            // Header is to display a loading indicator (or error) during initial loading,
            // and footer is to display an indicator (or error) when loading following pages.
            binding.fragEcardList.adapter =
                pagingAdapter.withLoadStateAdapters(header = GeneralLoadStateAdapter(pagingAdapter::retry),
                    footer = GeneralLoadStateAdapter(pagingAdapter::retry))

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.apply {
                    watch(this@repeatOnLifecycle, { it.flow }) {
                        lifecycleScope.launch { it?.collectLatest { pagingData -> pagingAdapter.submitData(pagingData) } }
                    }
                }
            }
        }
    }
}

object ECardComparator : DiffUtil.ItemCallback<CardRecord>() {
    override fun areItemsTheSame(oldItem: CardRecord, newItem: CardRecord): Boolean = oldItem.time == newItem.time

    override fun areContentsTheSame(oldItem: CardRecord, newItem: CardRecord): Boolean = oldItem == newItem
}

class ECardAdapter(diffCallback: DiffUtil.ItemCallback<CardRecord>) :
    PagingDataAdapter<CardRecord, ECardAdapter.ECardViewHolder>(diffCallback) {
    inner class ECardViewHolder(private val binding: ItemListTileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CardRecord?) {
            binding.apply {
                mtrlListItemText.text = item?.location
                mtrlListItemSecondaryText.text = item?.time?.toDateTimeString("yyyy-MM-dd hh:mm:ss")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ECardViewHolder =
        ECardViewHolder(ItemListTileBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ECardViewHolder, position: Int) = holder.bind(getItem(position))

}