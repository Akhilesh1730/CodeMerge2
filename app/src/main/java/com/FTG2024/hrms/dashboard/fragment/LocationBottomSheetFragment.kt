package com.FTG2024.hrms.dashboard.fragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import com.FTG2024.hrms.R
import com.FTG2024.hrms.databinding.FragmentLocationBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


open class LocationBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentLocationBottomSheetBinding
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0
    private var distance: Int = 0
    private var isRemarkRequired : Boolean = false
    private var onLocationFragmentListener: OnLocationClickListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (distance > 1000) {
            binding.textViewBottomLocationFragmentDistance.text = "Distance From Office is ${(distance/1000).toInt()} Km"
        } else {
            binding.textViewBottomLocationFragmentDistance.text = "Distance From Office is ${distance} M. "
        }

        if (isRemarkRequired) {
            showRemark()
        }
        map = view.findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setOnTouchListener(OnTouchListener { v, event -> true })
        setMap(latitude, longitude)
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    private fun setMap(lat : Double, long : Double) {
        val mapController = map.controller
        mapController.setZoom(18.5)
        val startPoint = GeoPoint(lat, long);
        val marker = Marker(map)
        marker.position = startPoint
        marker.icon = requireContext().getDrawable(R.drawable.baseline_location_on_24)
        map.overlays.add(marker)
        mapController.setCenter(startPoint);
    }

    private fun setListeners() {
        binding.buttonBottomLocationFragmentSubmit.setOnClickListener {
            if (!binding.editTextTextBottomLocationFragment.text.isNullOrEmpty()) {
                val remark = binding.editTextTextBottomLocationFragment.text
                onLocationFragmentListener!!.onSubmitClicked(remark.toString())
            } else {
                showToast("Please Enter Remark")
            }
        }

        binding.buttonBottomLocationFragmentRefresh.setOnClickListener {
            if (isDevModeOn()) {
                showToast("Please Turn Off Developer Mode")
            } else {
                onLocationFragmentListener!!.onRefreshClicked()
            }
        }
    }

    interface OnLocationClickListener {
        fun onRefreshClicked()
        fun onSubmitClicked(remark : String)
    }

    fun showRemark() {
        binding.labelBottomLocationFragment.text = "Explain the reason why you are performing day in activities from a distance more than 50M, from office"
        binding.editTextTextBottomLocationFragment.visibility = View.VISIBLE
    }

    companion object {

        @JvmStatic
        fun newInstance(lat : Double, long : Double,calcDistance : Int, remark : Boolean, listener: OnLocationClickListener) =
            LocationBottomSheetFragment().apply {
                arguments = Bundle().apply {
                   longitude = long
                    latitude = lat
                    onLocationFragmentListener = listener
                   distance = calcDistance
                    Log.d("###", "newInstance: $distance $calcDistance")
                    isRemarkRequired = remark
                }
            }
    }

    private fun showToast(msg : String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
    protected  fun isDevModeOn() : Boolean {
        val devoption = Settings.Secure.getInt(requireContext().contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0)
        return devoption == 1
    }

}