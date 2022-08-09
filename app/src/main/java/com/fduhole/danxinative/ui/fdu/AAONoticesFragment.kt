package com.fduhole.danxinative.ui.fdu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fduhole.danxinative.BrowserActivity
import com.fduhole.danxinative.R
import com.fduhole.danxinative.databinding.FragmentAaoNoticesBinding
import com.fduhole.danxinative.databinding.ItemListTileBinding
import com.fduhole.danxinative.databinding.ItemLoadStateBinding
import com.fduhole.danxinative.model.AAONotice
import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.util.FDULoginUtils.Companion.uisLoginJavaScript
import com.fduhole.danxinative.util.lifecycle.watch
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class AAONoticesUiState(
    val flow: Flow<PagingData<AAONotice>>? = null,
)

class AAONoticesFragment : Fragment(), KoinComponent {

    private val viewModel: AAONoticesViewModel by viewModels()
    private val globalState: GlobalState by inject()
    private lateinit var binding: FragmentAaoNoticesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAaoNoticesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            (activity as AppCompatActivity).supportActionBar?.title = "教务处通知"
            viewModel.initModel()
            val pagingAdapter = AAONoticeAdapter({ _, notice ->
                notice?.let {
                    startActivity(Intent(activity, BrowserActivity::class.java)
                        .putExtra(BrowserActivity.KEY_URL, it.url)
                        .putExtra(BrowserActivity.KEY_JAVASCRIPT, uisLoginJavaScript(globalState.person!!))
                        .putExtra(BrowserActivity.KEY_EXECUTE_IF_START_WITH, "https://uis.fudan.edu.cn/authserver/login"))
                }
            }, AAONoticeComparator)

            // Why using the identical adapters here as header and footer?
            //
            // Header is to display a loading indicator (or error) during initial loading,
            // and footer is to display an indicator (or error) when loading following pages.
            binding.fragAaoNoticesList.adapter =
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

object AAONoticeComparator : DiffUtil.ItemCallback<AAONotice>() {
    override fun areItemsTheSame(oldItem: AAONotice, newItem: AAONotice): Boolean = oldItem.url == newItem.url

    override fun areContentsTheSame(oldItem: AAONotice, newItem: AAONotice): Boolean = oldItem == newItem
}

class AAONoticeAdapter(private val onItemClick: (View?, AAONotice?) -> Unit, diffCallback: DiffUtil.ItemCallback<AAONotice>) :
    PagingDataAdapter<AAONotice, AAONoticeAdapter.AAONoticeViewHolder>(diffCallback) {
    inner class AAONoticeViewHolder(private val binding: ItemListTileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AAONotice?) {
            binding.apply {
                mtrlListItemText.text = item?.title
                mtrlListItemSecondaryText.text = item?.time
                binding.root.setOnClickListener { onItemClick(it, item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AAONoticeViewHolder =
        AAONoticeViewHolder(ItemListTileBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: AAONoticeViewHolder, position: Int) = holder.bind(getItem(position))

}

class LoadStateViewHolder(
    parent: ViewGroup,
    retry: () -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_load_state, parent, false)
) {
    private val binding = ItemLoadStateBinding.bind(itemView)
    private val progressBar: CircularProgressIndicator = binding.itLoadStateProgress
    private val errorMsg: MaterialTextView = binding.itLoadStateErrorText
    private val retry: MaterialButton = binding.itLoadStateErrorRetryButton
        .also { it.setOnClickListener { retry() } }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            errorMsg.text = loadState.error.localizedMessage
        }

        progressBar.isVisible = loadState is LoadState.Loading
        retry.isVisible = loadState is LoadState.Error
        errorMsg.isVisible = loadState is LoadState.Error
    }
}

class GeneralLoadStateAdapter(
    private val retry: () -> Unit,
) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ) = LoadStateViewHolder(parent, retry)

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState,
    ) = holder.bind(loadState)
}

/**
 * Return a [ConcatAdapter] connecting [header], [this] paging adapter and [footer].
 * Produce [header] with refreshing states, [footer] with append states.
 */
fun <T : Any, V : RecyclerView.ViewHolder> PagingDataAdapter<T, V>.withLoadStateAdapters(
    header: LoadStateAdapter<*>,
    footer: LoadStateAdapter<*>
): ConcatAdapter {
    addLoadStateListener { loadStates ->
        header.loadState = loadStates.refresh
        footer.loadState = loadStates.append
    }

    return ConcatAdapter(header, this, footer)
}