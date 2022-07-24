package com.fduhole.danxinative.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fduhole.danxinative.R
import com.fduhole.danxinative.databinding.FragmentLoginBinding
import com.fduhole.danxinative.util.ErrorUtils
import com.fduhole.danxinative.util.lifecycle.watch
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

data class LoginUiState(
    val idErrorId: Int? = null,
    val passwordErrorId: Int? = null,
    val loginError: Throwable? = null,
    val loggingIn: Boolean = false,
    val logged: Boolean = false
)

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            binding.fragLoginIdLayout.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) = viewModel.onIdChanged(s.toString())
            })
            binding.fragLoginPasswordLayout.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) = viewModel.onPasswordChanged(s.toString())
            })
            binding.fragLoginLoginButton.setOnClickListener {
                viewModel.logIn(
                    binding.fragLoginIdLayout.editText?.text?.toString(),
                    binding.fragLoginPasswordLayout.editText?.text?.toString()
                )
            }
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.apply {
                    val life = this@repeatOnLifecycle
                    watch(life, { it.idErrorId }) { binding.fragLoginIdLayout.error = it?.let { it1 -> getString(it1) } }
                    watch(life, { it.passwordErrorId }) { binding.fragLoginPasswordLayout.error = it?.let { it1 -> getString(it1) } }
                    watch(life, { it.loggingIn }) {
                        binding.fragLoginLoginButton.apply {
                            text = if (it) getString(R.string.logging_in) else getString(R.string.login_title)
                            isEnabled = !it
                        }
                    }
                    watch(life, { it.loginError }) {
                        if (it != null) {
                            Snackbar.make(binding.root, ErrorUtils.describeError(this@LoginFragment, it), Snackbar.LENGTH_LONG).show()
                        }
                    }
                    watch(life, { it.logged }) {
                        if (it) {
                            activity?.finish()
                        }
                    }
                }
            }
        }
    }
}