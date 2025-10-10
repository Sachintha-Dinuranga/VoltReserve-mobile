package com.example.voltreserve

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voltreserve.client.RetrofitClient
import com.example.voltreserve.models.CreateReservationRequest
import com.example.voltreserve.models.OwnerStationSummary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReservationActivity : AppCompatActivity() {

    private lateinit var locationSpinner: Spinner
    private lateinit var stationRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar

    private var allStations: List<OwnerStationSummary> = listOf()
    private var selectedLocation: String? = null
    private var selectedStation: OwnerStationSummary? = null

    private var selectedDate: Date? = null
    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null

    private val ownerService by lazy { RetrofitClient.ownerAuthed(this) } // ✅ Token-aware instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        locationSpinner = findViewById(R.id.spinnerLocation)
        stationRecycler = findViewById(R.id.recyclerStations)
        progressBar = findViewById(R.id.progressBar)

        stationRecycler.layoutManager = LinearLayoutManager(this)

        loadStations()
    }

    private fun loadStations() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = ownerService.getActiveStations()
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    allStations = response.body()!!
                    val uniqueLocations = allStations.map { it.location }.distinct()
                    setupLocationDropdown(uniqueLocations)
                } else {
                    Toast.makeText(this@ReservationActivity, "Failed to load stations", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                e.printStackTrace()
                Toast.makeText(this@ReservationActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupLocationDropdown(locations: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = adapter

        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLocation = locations[position]
                val stations = allStations.filter { it.location == selectedLocation }
                showStations(stations)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showStations(stations: List<OwnerStationSummary>) {
        stationRecycler.adapter = StationAdapter(stations) { station ->
            if (station.availableSlots > 0) {
                selectedStation = station
                openReservationDialog(station)
            } else {
                Toast.makeText(this, "No available slots!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openReservationDialog(station: OwnerStationSummary) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reservation, null)
        val dateBtn = dialogView.findViewById<Button>(R.id.btnSelectDate)
        val startBtn = dialogView.findViewById<Button>(R.id.btnSelectStart)
        val endBtn = dialogView.findViewById<Button>(R.id.btnSelectEnd)
        val confirmBtn = dialogView.findViewById<Button>(R.id.btnConfirmReservation)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        dateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                c.set(y, m, d)
                selectedDate = c.time
                dateBtn.text = dateFormat.format(selectedDate!!)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        startBtn.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, h, m ->
                selectedStartTime = String.format("%02d:%02d:00", h, m)
                startBtn.text = selectedStartTime
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        endBtn.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, h, m ->
                selectedEndTime = String.format("%02d:%02d:00", h, m)
                endBtn.text = selectedEndTime
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Reserve ${station.name}")
            .setView(dialogView)
            .create()

        confirmBtn.setOnClickListener {
            if (selectedDate != null && selectedStartTime != null && selectedEndTime != null) {
                showSummaryAndConfirm(station)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please select date & time", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showSummaryAndConfirm(station: OwnerStationSummary) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate!!)
        val message = """
            Station: ${station.name}
            Location: ${station.location}
            Type: ${station.type}
            Date: $dateStr
            Start: $selectedStartTime
            End: $selectedEndTime
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Confirm Reservation")
            .setMessage(message)
            .setPositiveButton("Confirm") { _, _ ->
                createReservation(station)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createReservation(station: OwnerStationSummary) {
        lifecycleScope.launch {
            try {
                val dateStr = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(selectedDate!!)
                val req = CreateReservationRequest(
                    stationId = station.id,
                    stationName = station.name,
                    type = station.type,
                    reservationDate = dateStr,
                    startTime = selectedStartTime!!,
                    endTime = selectedEndTime!!
                )

                val response = ownerService.createReservation(req)
                if (response.isSuccessful) {
                    Toast.makeText(this@ReservationActivity, "Reservation created!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ReservationActivity, "Failed to reserve", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ReservationActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

// ---------------------------
// RecyclerView Adapter
// ---------------------------
class StationAdapter(
    private val stations: List<OwnerStationSummary>,
    private val onSelect: (OwnerStationSummary) -> Unit
) : RecyclerView.Adapter<StationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvStationName)
        val type: TextView = view.findViewById(R.id.tvStationType)
        val slots: TextView = view.findViewById(R.id.tvSlots)
        val btnSelect: Button = view.findViewById(R.id.btnSelectStation)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val v = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_station, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = stations[position]
        holder.name.text = s.name
        holder.type.text = "Type: ${s.type}"
        holder.slots.text = "Available Slots: ${s.availableSlots}"
        holder.btnSelect.isEnabled = s.availableSlots > 0
        holder.btnSelect.setOnClickListener { onSelect(s) }
    }

    override fun getItemCount() = stations.size
}
