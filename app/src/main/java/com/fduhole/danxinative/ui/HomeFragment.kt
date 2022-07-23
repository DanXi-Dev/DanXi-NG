package com.fduhole.danxinative.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fduhole.danxinative.base.Feature
import com.fduhole.danxinative.databinding.FragmentHomeBinding
import com.fduhole.danxinative.databinding.ItemFeatureCardBinding
import com.fduhole.danxinative.util.lifecycle.watch
import kotlinx.coroutines.launch

data class HomeUiState(
    val features: List<Feature> = listOf()
)

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.initModel { (binding.fragHomeFeatureList.adapter as BaseAdapter).notifyDataSetChanged() }

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.apply {
                    watch(this@repeatOnLifecycle, { it.features }) {
                        binding.fragHomeFeatureList.adapter = context?.let { cxt -> FeatureAdapter(cxt, it) }
                    }
                }
            }
        }
    }

    class FeatureAdapter(private val context: Context, private val features: List<Feature>) : BaseAdapter() {
        override fun getCount(): Int = features.size

        override fun getItem(position: Int): Any = features[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // If we do not reuse old views, the animation on the old view (i.e. tap ripple effect) will be discarded at once.
            val item = if (convertView != null)
                ItemFeatureCardBinding.bind(convertView)
            else
                ItemFeatureCardBinding.inflate(LayoutInflater.from(context), parent, false)
            item.itFeatureCardCardView.isEnabled = features[position].getClickable()
            item.itFeatureCardProgressBar.visibility = if (features[position].inProgress()) View.VISIBLE else View.INVISIBLE
            item.itFeatureCardCardView.setOnClickListener { features[position].onClick() }
            item.itFeatureCardTitle.text = features[position].getTitle()
            item.itFeatureCardSubtitle.text = features[position].getSubTitle()
            item.itFeatureCardTertiaryTitle.visibility = View.GONE
            return item.root
        }

    }
}