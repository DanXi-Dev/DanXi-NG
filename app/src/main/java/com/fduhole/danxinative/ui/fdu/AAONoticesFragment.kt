package com.fduhole.danxinative.ui.fdu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fduhole.danxinative.BrowserActivity
import com.fduhole.danxinative.databinding.FragmentAaoNoticesBinding
import com.fduhole.danxinative.databinding.ItemListTileBinding
import com.fduhole.danxinative.model.AAONotice
import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.util.FDULoginUtils.Companion.uisLoginJavaScript
import com.fduhole.danxinative.util.lifecycle.watch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class AAONoticesUiState(
    val flow: Flow<PagingData<AAONotice>>? = null,
)

class AAONoticesFragment : Fragment(),KoinComponent {

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
            viewModel.initModel()
            val pagingAdapter = AAONoticeAdapter({ _, notice -> notice?.let {
                startActivity(Intent(activity,BrowserActivity::class.java)
                    .putExtra(BrowserActivity.KEY_URL, it.url)
                    .putExtra(BrowserActivity.KEY_JAVASCRIPT, uisLoginJavaScript(globalState.person!!))
                    .putExtra(BrowserActivity.KEY_EXECUTE_IF_START_WITH, "https://uis.fudan.edu.cn/authserver/login"))
            } }, AAONoticeComparator)
            binding.fragAaoNoticesList.adapter = pagingAdapter
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.apply {
                    watch(this@repeatOnLifecycle, { it.flow }) {
                        lifecycleScope.launch {
                            it?.collectLatest { pagingData ->
                                pagingAdapter.submitData(pagingData)
                                println("Submit data!")
                            }
                        }
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