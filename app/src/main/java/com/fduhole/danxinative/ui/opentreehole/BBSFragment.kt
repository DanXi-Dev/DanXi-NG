package com.fduhole.danxinative.ui.opentreehole

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fduhole.danxinative.R

class BBSFragment : Fragment() {

    companion object {
        fun newInstance() = BBSFragment()
    }

    private lateinit var viewModel: BBSViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_bbs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BBSViewModel::class.java)
        // TODO: Use the ViewModel
    }

}