package vcmsa.projects.thedoghouse_prototype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager // Required for layout parameters
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class FilterDialogFragment : DialogFragment() {

    private lateinit var editBreed: EditText
    private lateinit var editAge: EditText
    private lateinit var checkboxVaccinated: CheckBox
    private lateinit var checkboxSterilized: CheckBox
    private lateinit var buttonApply: Button
    private lateinit var buttonReset: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Use the layout you created
        return inflater.inflate(R.layout.fragment_filter_dialog, container, false)
    }

    // 🔥 FIX FOR THE SQUISHED LOOK 🔥
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            // Use MATCH_PARENT to make the dialog width span the screen width
            // This is the common fix for DialogFragments that appear too narrow.
            WindowManager.LayoutParams.MATCH_PARENT,
            // Keep the height as WRAP_CONTENT, as defined in your XML
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
    // ------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize Views
        editBreed = view.findViewById(R.id.edit_breed)
        editAge = view.findViewById(R.id.edit_age)
        checkboxVaccinated = view.findViewById(R.id.checkbox_vaccinated)
        checkboxSterilized = view.findViewById(R.id.checkbox_sterilized)
        buttonApply = view.findViewById(R.id.button_apply)
        buttonReset = view.findViewById(R.id.button_reset)

        // 2. Set Listeners

        // When APPLY is clicked, gather data and call the Activity
        buttonApply.setOnClickListener {
            applyFilters()
        }

        // When RESET is clicked, clear filters and call the Activity
        buttonReset.setOnClickListener {
            resetFilters()
        }
    }

    // Helper function to gather input and update the activity
    private fun applyFilters() {
        // A. Gather Data from UI elements
        val breed = editBreed.text.toString().trim().takeIf { it.isNotEmpty() }
        val age = editAge.text.toString().toIntOrNull() // Converts text to Int?, returns null if invalid

        val isVaccinated = checkboxVaccinated.isChecked
        val isSterilized = checkboxSterilized.isChecked

        // B. Call the Host Activity Method
        // The host activity (ViewAdoptionActivity) MUST implement this function
        (activity as? ViewAdoptionActivity)?.updateFiltersAndFetch(
            age = age,
            breed = breed,
            isVaccinated = if (isVaccinated) true else null, // Send true if checked, null otherwise
            isSterilized = if (isSterilized) true else null  // Send true if checked, null otherwise
        )

        dismiss() // Close the dialog
    }

    private fun resetFilters() {
        // Call the host activity with all nulls to reset all filters
        (activity as? ViewAdoptionActivity)?.updateFiltersAndFetch(
            age = null,
            breed = null,
            isVaccinated = null,
            isSterilized = null
        )
        dismiss()
    }
}