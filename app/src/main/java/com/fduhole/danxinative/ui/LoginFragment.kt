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
import com.fduhole.danxinative.databinding.FragmentLoginBinding
import com.fduhole.danxinative.util.lifecycle.watch
import kotlinx.coroutines.launch

data class LoginUiState(
    val idErrorText: String? = null,
    val passwordErrorText: String? = null,
    val loggingIn: Boolean = false
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
                viewModel.onLogin(
                    binding.fragLoginIdLayout.editText?.text.toString(),
                    binding.fragLoginPasswordLayout.editText?.text.toString()
                )
            }
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.apply {
                    val life = this@repeatOnLifecycle
                    watch(life, { it.idErrorText }) { binding.fragLoginIdLayout.error = it }
                    watch(life, { it.passwordErrorText }) { binding.fragLoginPasswordLayout.error = it }
                    watch(life, { it.loggingIn }) {
                        binding.fragLoginLoginButton.apply {
                            text = if (it) "登录中" else "登录"
                            isEnabled = !it
                        }
                    }
                }
            }
        }
    }
}