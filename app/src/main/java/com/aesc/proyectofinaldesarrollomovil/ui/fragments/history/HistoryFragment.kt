package com.aesc.proyectofinaldesarrollomovil.ui.fragments.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.HistoryFragmentBinding
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.LocationDao
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.Locations
import com.aesc.proyectofinaldesarrollomovil.ui.adapters.IPostAdapter
import com.aesc.proyectofinaldesarrollomovil.ui.adapters.LocationAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class HistoryFragment : Fragment(), IPostAdapter {

    private lateinit var adapter: LocationAdapter
    private lateinit var locationDao: LocationDao
    private lateinit var viewModel: HistoryViewModel
    private var _binding: HistoryFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        _binding = HistoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        locationDao = LocationDao()
        val locationCollections = locationDao.locationCollection
        val query = locationCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOtions =
            FirestoreRecyclerOptions.Builder<Locations>().setQuery(query, Locations::class.java)
                .build()
        adapter = LocationAdapter(recyclerViewOtions, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onShareClicked(latitude: String, longitude: String) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
        var sAux = "\nDo you wanna see me?\n\n"
        sAux = "$sAux https://www.google.com/maps?q=loc:$latitude,$longitude\n\n"
        i.putExtra(Intent.EXTRA_TEXT, sAux)
        startActivity(Intent.createChooser(i, "choose one"))
    }

    override fun onDeleteClicked(id: String) {
        locationDao.deleteLocation(id)
    }
}