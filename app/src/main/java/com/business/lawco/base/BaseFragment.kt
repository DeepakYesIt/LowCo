package com.business.lawco.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.business.lawco.R
import java.util.regex.Matcher
import java.util.regex.Pattern

open class BaseFragment() : Fragment() {
    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    fun showMe(): Dialog? {
        dialog?.dismiss()
        val layoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        @SuppressLint("InflateParams") val view: View =
            layoutInflater.inflate(R.layout.my_progress, null)
//        val mProgressTv = view.findViewById<TextView>(R.id.mProgressTv_ids)
//        mProgressTv.text = "Please wait..."
        dialog = Dialog(requireActivity(), R.style.CustomProgressBarTheme)
        dialog?.setContentView(view)
        dialog?.setCancelable(false)
        dialog?.window?.setDimAmount(0f)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
        return dialog
    }

    fun dismissMe() {
        if (dialog != null) dialog?.dismiss()
    }

    open fun expiryValidator(email: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN = "(?:0[1-9]|1[0-2])/[0-9]{2}"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

}