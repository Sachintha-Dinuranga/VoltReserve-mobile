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
import com.example.voltreserve.models.ReservationDto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReservationListActivity : AppCompatActivity() {

    private lateinit var spinnerFilter: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ReservationAdapter
    private var allReservations: List<ReservationDto> = listOf()

    private val ownerService by lazy { RetrofitClient.ownerAuthed(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_list)

        spinnerFilter = findViewById(R.id.spinnerFilter)
        recyclerView = findViewById(R.id.recyclerReservations)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReservationAdapter(listOf(),
            onUpdate = { openUpdateDialog(it) },
            onCancel = { confirmCancel(it) }
        )
        recyclerView.adapter = adapter

        setupFilterDropdown()
        loadReservations()
    }

    // -----------------------------
    // Setup Dropdown Filter
    // -----------------------------
    private fun setupFilterDropdown() {
        val statuses = listOf("Pending", "Approved", "Cancelled", "Completed", "All")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                // Reload reservations each time the user switches filters
                loadReservations(statuses[pos])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    // -----------------------------
    // Load Reservations
    // -----------------------------
    private fun loadReservations(selectedStatus: String = "All") {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = ownerService.listReservations()
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    allReservations = response.body()!!
                    filterReservations(selectedStatus)
                } else {
                    Toast.makeText(this@ReservationListActivity, "Failed to load reservations", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                e.printStackTrace()
                Toast.makeText(this@ReservationListActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // -----------------------------
    // Filter Reservations
    // -----------------------------
    private fun filterReservations(status: String) {
        val filtered = if (status == "All") allReservations else allReservations.filter { it.status == status }
        adapter.updateList(filtered, readOnly = (status == "All"))
    }

    // -----------------------------
    // Update Reservation Dialog
    // -----------------------------
    private fun openUpdateDialog(res: ReservationDto) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_reservation, null)
        val dateBtn = dialogView.findViewById<Button>(R.id.btnSelectDate)
        val startBtn = dialogView.findViewById<Button>(R.id.btnSelectStart)
        val endBtn = dialogView.findViewById<Button>(R.id.btnSelectEnd)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val c = Calendar.getInstance()
        var selectedDate: Date? = null
        var selectedStart: String? = null
        var selectedEnd: String? = null

        dateBtn.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                c.set(y, m, d)
                selectedDate = c.time
                dateBtn.text = dateFormat.format(selectedDate!!)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        startBtn.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                selectedStart = String.format("%02d:%02d:00", h, m)
                startBtn.text = selectedStart
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        endBtn.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                selectedEnd = String.format("%02d:%02d:00", h, m)
                endBtn.text = selectedEnd
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        AlertDialog.Builder(this)
            .setTitle("Update Reservation")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                if (selectedDate == null || selectedStart == null || selectedEnd == null) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                AlertDialog.Builder(this)
                    .setTitle("Confirm Update")
                    .setMessage("Are you sure you want to update this reservation?")
                    .setPositiveButton("Yes") { _, _ ->
                        updateReservation(res, selectedDate!!, selectedStart!!, selectedEnd!!)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateReservation(res: ReservationDto, date: Date, start: String, end: String) {
        lifecycleScope.launch {
            try {
                val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(date)
                val req = CreateReservationRequest(
                    stationId = res.stationId,
                    stationName = res.stationName,
                    type = res.type,
                    reservationDate = formattedDate,
                    startTime = start,
                    endTime = end
                )

                val response = ownerService.updateReservation(res.id, req)
                if (response.isSuccessful) {
                    Toast.makeText(this@ReservationListActivity, "Updated successfully", Toast.LENGTH_SHORT).show()
                    loadReservations(spinnerFilter.selectedItem.toString())
                } else {
                    Toast.makeText(this@ReservationListActivity, "Failed to update", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReservationListActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // -----------------------------
    // Cancel Reservation
    // -----------------------------
    private fun confirmCancel(res: ReservationDto) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Reservation")
            .setMessage("Are you sure you want to cancel this reservation?")
            .setPositiveButton("Yes") { _, _ -> cancelReservation(res.id) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelReservation(id: String) {
        lifecycleScope.launch {
            try {
                val response = ownerService.cancelReservation(id)
                if (response.isSuccessful) {
                    Toast.makeText(this@ReservationListActivity, "Reservation cancelled", Toast.LENGTH_SHORT).show()
                    loadReservations(spinnerFilter.selectedItem.toString())
                } else {
                    Toast.makeText(this@ReservationListActivity, "Failed to cancel", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReservationListActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

// ----------------------------
// RecyclerView Adapter
// ----------------------------
class ReservationAdapter(
    private var reservations: List<ReservationDto>,
    private val onUpdate: (ReservationDto) -> Unit,
    private val onCancel: (ReservationDto) -> Unit,
    private var isReadOnly: Boolean = false
) : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val station: TextView = view.findViewById(R.id.tvStation)
        val date: TextView = view.findViewById(R.id.tvDate)
        val time: TextView = view.findViewById(R.id.tvTime)
        val status: TextView = view.findViewById(R.id.tvStatus)
        val btnUpdate: Button = view.findViewById(R.id.btnUpdate)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val v = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val res = reservations[position]
        val dateStr = res.reservationDate.substring(0, 10)
        holder.station.text = "${res.stationName} (${res.type})"
        holder.date.text = "Date: $dateStr"
        holder.time.text = "Time: ${res.startTime} - ${res.endTime}"
        holder.status.text = "Status: ${res.status}"

        if (isReadOnly) {
            holder.btnUpdate.visibility = View.GONE
            holder.btnCancel.visibility = View.GONE
        } else {
            holder.btnUpdate.visibility = if (res.status == "Pending") View.VISIBLE else View.GONE
            holder.btnCancel.visibility = if (res.status == "Pending" || res.status == "Approved") View.VISIBLE else View.GONE
        }

        holder.btnUpdate.setOnClickListener { onUpdate(res) }
        holder.btnCancel.setOnClickListener { onCancel(res) }
    }

    override fun getItemCount() = reservations.size

    fun updateList(newList: List<ReservationDto>, readOnly: Boolean = false) {
        reservations = newList
        isReadOnly = readOnly
        notifyDataSetChanged()
    }
}
