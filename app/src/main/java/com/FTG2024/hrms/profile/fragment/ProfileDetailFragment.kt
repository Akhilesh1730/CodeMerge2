package com.FTG2024.hrms.profile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.FTG2024.hrms.R
import com.FTG2024.hrms.databinding.FragmentProfileDetailBinding
import com.FTG2024.hrms.profile.model.MyAccount

private const val ARG_MY_ACCOUNT = "myAccount"
class ProfileDetailFragment : Fragment() {
    private lateinit var myacc :MyAccount
    private lateinit var binding: FragmentProfileDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myacc = it.getParcelable(ARG_MY_ACCOUNT)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileDetailBinding.inflate(layoutInflater, container, false)
        binding.profileDetailsUserName.setText(myacc.username)
        binding.profileDetailsEmail.setText(myacc.email)
        binding.profileDetailsMobNo.setText(myacc.mobNo)
        binding.profileDetailsAddress.setText(myacc.address)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(myacc : MyAccount) =
            ProfileDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_MY_ACCOUNT, myacc)
                }
            }
    }
}