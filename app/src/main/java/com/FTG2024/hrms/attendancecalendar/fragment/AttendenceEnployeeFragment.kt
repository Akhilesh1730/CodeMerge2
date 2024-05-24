package com.FTG2024.hrms.attendancecalendar.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.attendancecalendar.adapter.EmployeeAdapter
import com.FTG2024.hrms.attendancecalendar.model.AttendenceEmployeeModel
import com.FTG2024.hrms.attendancecalendar.viewmodel.AttendanceViewModel
import com.FTG2024.hrms.databinding.FragmentAttendenceEnployeeBinding
import com.FTG2024.hrms.dialog.ProgressDialog
import com.FTG2024.hrms.leaves.adapter.LeavesApprovalPendingAdapter
import com.FTG2024.hrms.leaves.adapter.LeavesApprovalRejectedApprovedAdapter
import com.FTG2024.hrms.profile.model.Data
import com.FTG2024.hrms.profile.model.ProfileEmployeeDetailResponse
import com.FTG2024.hrms.uidata.Response
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AttendenceEnployeeFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAttendenceEnployeeBinding
    private val viewModel : AttendanceViewModel by activityViewModels()
    private lateinit var progressDialog : ProgressDialog
    private lateinit var recyclerView : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        progressDialog = ProgressDialog(requireActivity(),"Loading")
        progressDialog.show()
        binding = FragmentAttendenceEnployeeBinding.inflate(layoutInflater, container, false)
        viewModel.getEmployeeProfile()
        viewModel.getUserLiveData().observe(this, Observer {event->
            event.getContentIfNotHandled().let { response ->
                when(response) {
                    is Response.Success -> {
                        val profileEmployeeDetailResponse = response.data as ProfileEmployeeDetailResponse
                        if (profileEmployeeDetailResponse.code == 200) {
                            Log.d("####", "onCreateView: ${profileEmployeeDetailResponse.data}")
                            setRecylerView(profileEmployeeDetailResponse.data)
                        } else if (profileEmployeeDetailResponse.code == 304) {
                            /*showToast(profileEmployeeDetailResponse.message)*/
                        } else {
                            showToast("Unable To Reach Server. Retry")
                        }
                        progressDialog.dismiss()
                    }
                    is Response.Exception -> {
                        showToast(response.message.toString())
                        progressDialog.dismiss()
                    }
                    else-> return@Observer
                }
            }
        })
        return binding.root
    }

    public interface OnEmployeeSelectedListener {
        fun onEmployeeSelected(model: AttendenceEmployeeModel)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    }

    private fun setRecylerView(data: List<Data>) {
        var list = mutableListOf<AttendenceEmployeeModel>()
        for (profile in data) {
            val name = "${profile.FIRST_NAME} ${profile.LAST_NAME}"
            list.add(AttendenceEmployeeModel(name, profile.ID))
        }
        Log.d("###", "setRecylerView: ${list.size}")
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewAttendenceEmployee.layoutManager = linearLayoutManager
        val adapter = EmployeeAdapter(list)
        adapter.setOnClickListener(object  : EmployeeAdapter.OnClickListener {
            override fun onClicked(model: AttendenceEmployeeModel) {
                onEmployeeSelectedListeners!!.onEmployeeSelected(model)
                dismiss()
            }

        })
        binding.recyclerViewAttendenceEmployee.adapter = adapter
        progressDialog.dismiss()
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: AttendenceEnployeeFragment.OnEmployeeSelectedListener): AttendenceEnployeeFragment {
            val fragment = AttendenceEnployeeFragment()
            fragment.onEmployeeSelectedListeners = listener
            return fragment
        }
    }
    private var onEmployeeSelectedListeners: AttendenceEnployeeFragment.OnEmployeeSelectedListener? = null
}