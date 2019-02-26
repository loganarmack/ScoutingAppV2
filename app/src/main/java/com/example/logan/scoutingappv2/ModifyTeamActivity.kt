package com.example.logan.scoutingappv2

import android.animation.LayoutTransition
import android.content.Context
import android.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import com.example.logan.scoutingappv2.databinding.ActivityModifyTeamBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_modify_team.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


//stores everything relating to creating/editing teams
class ModifyTeamActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityModifyTeamBinding
    private var mode = 0
    private var expandedState: Boolean = true
    private val team: Team = Team()
    private val accessor: APIAccessor = APIAccessor()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("LOADING-TIME-GAMER", System.currentTimeMillis().toString())
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_team)
        Log.d("LOADING-TIME-GAMER", System.currentTimeMillis().toString())

        //makes expand/collapse animation work without fading out of checkboxes
        val layoutTransition = LayoutTransition()
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING)
        layoutTransition.disableTransitionType(LayoutTransition.APPEARING)
        binding.parentLayout.layoutTransition = layoutTransition
        binding.inputScrollLayout.layoutTransition = layoutTransition

        //sets up update event for when slider moves
        binding.autoScoreSlider.setOnSeekBarChangeListener(this)
        binding.consistencyScoreSlider.setOnSeekBarChangeListener(this)
        binding.driverSkillScoreSlider.setOnSeekBarChangeListener(this)
        binding.objectiveScoreSlider.setOnSeekBarChangeListener(this)

        //runs additional setup based on mode
        mode = intent.getIntExtra(MODE, 0)
        when (mode) {
            0 -> newTeamModeInit()
            1 -> modifyTeamModeInit()
        }
    }

    //adds animation when returning to main activity
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    //changes slider text when they are moved
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (seekBar != null) {
            when (seekBar.id){
                binding.autoScoreSlider.id -> {
                    binding.autonomousText.text = getString(R.string.autonomous, seekBar.progress)
                }
                binding.consistencyScoreSlider.id -> {
                    binding.consistencyText.text = getString(R.string.consistency, seekBar.progress)
                }
                binding.defenceScoreSlider.id -> {
                    binding.defenceText.text = getString(R.string.defence, seekBar.progress)
                }
                binding.driverSkillScoreSlider.id -> {
                    binding.driverSkillText.text = getString(R.string.driver_skill, seekBar.progress)
                }
                binding.objectiveScoreSlider.id -> {
                    binding.objectiveText.text = getString(R.string.objectives, seekBar.progress)
                }
            }
        }
    }

    private fun newTeamModeInit() {
        binding.newTeamTitleText.text = getString(R.string.new_team)
        binding.teamNumberEdit.visibility = View.VISIBLE

        //sets all sliders to 5
        resetSliders(5)
    }

    //resets all sliders to i
    private fun resetSliders(i: Int) {
        binding.apply {
            autonomousText.text = getString(R.string.autonomous, i)
            autoScoreSlider.progress = i
            consistencyText.text = getString(R.string.consistency, i)
            consistencyScoreSlider.progress = i
            defenceText.text = getString(R.string.defence, i)
            defenceScoreSlider.progress = i
            driverSkillText.text = getString(R.string.driver_skill, i)
            driverSkillScoreSlider.progress = i
            objectiveText.text = getString(R.string.objectives, i)
            objectiveScoreSlider.progress = i
        }
    }

    private fun modifyTeamModeInit() {
        val oldTeam: Team = Team() //set team from previously selected stuff
        binding.teamNumberEdit.visibility = View.GONE

        //set all data to match team
        binding.apply {
            //parses issue text to find which boxes to check
            if (oldTeam.issues != null && oldTeam.issues!!.contains(": ")) {
                val issueSplit = oldTeam.issues!!.split(": ", limit = 2)
                hardwareIssueCheckbox.isChecked = issueSplit[0].contains("hardware", true)
                softwareIssueCheckbox.isChecked = issueSplit[0].contains("software", true)
                otherIssueCheckbox.isChecked = issueSplit[0].contains("other", true)
                issuesEdit.setText(issueSplit[1])
            }

            //sets edit text boxes to team data
            teamNameEdit.setText(oldTeam.name)
            notesEdit.setText(oldTeam.notes)
            locationEdit.setText(oldTeam.location)

            //sets sliders to correct position for team
            autoScoreSlider.progress = oldTeam.autonomous ?: 5
            autonomousText.text = getString(R.string.autonomous, autoScoreSlider.progress)
            consistencyScoreSlider.progress = oldTeam.consistency ?: 5
            consistencyText.text = getString(R.string.consistency, consistencyScoreSlider.progress)
            defenceScoreSlider.progress = oldTeam.defence ?: 5
            defenceText.text = getString(R.string.defence, defenceScoreSlider.progress)
            driverSkillScoreSlider.progress = oldTeam.driverSkill ?: 5
            driverSkillText.text = getString(R.string.driver_skill, driverSkillScoreSlider.progress)
            objectiveScoreSlider.progress = oldTeam.objectiveScore ?: 5
            objectiveText.text = getString(R.string.objectives, objectiveScoreSlider.progress)

            inputScroll.visibility = View.VISIBLE
        }
    }

    //makes team modification sections visible/invisible
    fun includeCheck(view: View) {
        when (view.id) {
            binding.includeAutoCheck.id -> {
                binding.autoScoreSlider.visibility = visibleOrGone(binding.includeAutoCheck.isChecked)
                binding.autonomousText.visibility =  visibleOrGone(binding.includeAutoCheck.isChecked)
            }
            binding.includeConsistencyCheck.id -> {
                binding.consistencyScoreSlider.visibility = visibleOrGone(binding.includeConsistencyCheck.isChecked)
                binding.consistencyText.visibility =  visibleOrGone(binding.includeConsistencyCheck.isChecked)
            }
            binding.includeDriverSkillCheck.id -> {
                binding.driverSkillScoreSlider.visibility = visibleOrGone(binding.includeDriverSkillCheck.isChecked)
                binding.driverSkillText.visibility =  visibleOrGone(binding.includeDriverSkillCheck.isChecked)
            }
            binding.includeObjectiveCheck.id -> {
                binding.objectiveScoreSlider.visibility = visibleOrGone(binding.includeObjectiveCheck.isChecked)
                binding.objectiveText.visibility =  visibleOrGone(binding.includeObjectiveCheck.isChecked)
            }
            binding.includeDefenceCheck.id -> {
                binding.defenceScoreSlider.visibility = visibleOrGone(binding.includeDefenceCheck.isChecked)
                binding.defenceText.visibility = visibleOrGone(binding.includeDefenceCheck.isChecked)
            }
            binding.includeNameCheck.id -> {
                binding.teamNameEdit.visibility = visibleOrGone(binding.includeNameCheck.isChecked)
            }
            binding.includeIssuesCheck.id -> {
                binding.inputScroll.issues_group.visibility = visibleOrGone(binding.includeIssuesCheck.isChecked)
            }
            binding.includeNotesCheck.id -> {
                binding.notesEdit.visibility = visibleOrGone(binding.includeNotesCheck.isChecked)
            }
            binding.includeLocationCheck.id -> {
                binding.locationEdit.visibility = visibleOrGone(binding.includeLocationCheck.isChecked)
            }
        }
    }

    private fun visibleOrGone(b: Boolean) = if (b) View.VISIBLE else View.GONE

    //makes include checkboxes smaller
    fun expandCollapseCheckboxes(view: View) {
        expandedState = !expandedState
        binding.includeChecksGroup.visibility = visibleOrGone(expandedState)
        view.startAnimation(
            if (expandedState)
                AnimationUtils.loadAnimation(this, R.anim.rotate_expand)
            else
                AnimationUtils.loadAnimation(this, R.anim.rotate_collapse)
        )
    }

    //updates the selected team to match user inputted data
    private fun updateTeam(): Int {
        if (binding.teamNumberEdit.visibility == View.VISIBLE) {
        //sets team number
            try {
                team.number = binding.teamNumberEdit.text.toString().toInt()
            } catch (e: NumberFormatException) { //Shouldn't be possible, but just in case
                Toast.makeText(
                    this@ModifyTeamActivity,
                    "Error: Invalid team number! ${binding.teamNumberEdit.text}",
                    Toast.LENGTH_SHORT
                ).show()
                return 1
            }
            if (team.number < 0) {
                Toast.makeText(
                    this@ModifyTeamActivity,
                    "Error: Invalid team number! ${binding.teamNumberEdit.text.toString().toInt()}",
                    Toast.LENGTH_SHORT
                ).show()
                return 1
            }
        }
        if (binding.includeNameCheck.isChecked) {
            team.name = binding.teamNameEdit.text.toString()
        }
        if (binding.includeLocationCheck.isChecked) {
            team.location = binding.locationEdit.text.toString()
        }
        if (binding.includeIssuesCheck.isChecked) {
            //sets up issues string based on text + checkboxes
            if (!binding.hardwareIssueCheckbox.isChecked && !binding.softwareIssueCheckbox.isChecked && !binding.otherIssueCheckbox.isChecked) {
                team.issues = "None"
            } else {
                if (binding.hardwareIssueCheckbox.isChecked) {
                    team.issues = "Hardware"
                    binding.hardwareIssueCheckbox.isChecked = false
                }
                if (binding.softwareIssueCheckbox.isChecked) {
                    binding.softwareIssueCheckbox.isChecked = false
                    if (team.issues != "") {
                        team.issues += ", Software"
                    } else {
                        team.issues = "Software"
                    }
                }
                if (binding.otherIssueCheckbox.isChecked) {
                    binding.otherIssueCheckbox.isChecked = false
                    if (team.issues != "") {
                        team.issues += ", Other"
                    } else {
                        team.issues = "Other"
                    }
                }
                team.issues += ": ${binding.issuesEdit.text}"
            }
        }
        if (binding.includeNotesCheck.isChecked) {
            team.notes = binding.notesEdit.text.toString()
        }
        if (binding.includeAutoCheck.isChecked) {
            team.autonomous = binding.autoScoreSlider.progress
        }
        if (binding.includeConsistencyCheck.isChecked) {
            team.consistency = binding.consistencyScoreSlider.progress
        }
        if (binding.includeDefenceCheck.isChecked) {
            team.defence = binding.defenceScoreSlider.progress
        }
        if (binding.includeDriverSkillCheck.isChecked) {
            team.driverSkill = binding.driverSkillScoreSlider.progress
        }
        if (binding.includeObjectiveCheck.isChecked) {
            team.objectiveScore = binding.objectiveScoreSlider.progress
        }
        return 0
    }

    //uploads new team/edit or stores it to text file
    fun onClickSaveButton(view: View) {
        if (updateTeam() == 1) {
            return
        }
        if (team.number < 1) { //shouldn't be possible unless selected team already had id < 1, but just in case
            Toast.makeText(
                this@ModifyTeamActivity,
                "Error: Invalid team number! ${binding.teamNumberEdit.text.toString().toInt()}",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (isNetworkAvailable()) {
            if (mode == 0) {
                //clears all text input
                binding.teamNameEdit.text.clear()
                binding.teamNumberEdit.text.clear()
                binding.issuesEdit.text.clear()
                binding.notesEdit.text.clear()
                binding.locationEdit.text.clear()
                //reset check boxes
                binding.hardwareIssueCheckbox.isChecked = false
                binding.softwareIssueCheckbox.isChecked = false
                binding.otherIssueCheckbox.isChecked = false
                //reset slider
                resetSliders(5)
            }
            //attempts to upload data to server; if it fails, adds it to text file instead
            val call: Call<Team> = accessor.apiService.createTeam(team)
            call.enqueue(object : Callback<Team> {
                override fun onResponse(call: Call<Team>, response: Response<Team>) {
                    if (response.isSuccessful) {
                        //server accepts team data
                        Toast.makeText(
                            this@ModifyTeamActivity, "Successfully uploaded team data",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //server rejects team data
                        Toast.makeText(
                            this@ModifyTeamActivity, "Error: Team data invalid",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Team>, t: Throwable) {
                    Toast.makeText(
                        this@ModifyTeamActivity,
                        "Data saved to text file.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //no internet; adds team to file to be uploaded later
                    val path: File = filesDir
                    val letDirectory = File(path, "LET")
                    letDirectory.mkdirs()
                    val outFile = File(letDirectory, "teams.txt")
                    outFile.appendText("${team.number}\n")
                    val teamFile = File(letDirectory, "team${team.number}.json")
                    val gson = Gson()
                    val output: String = gson.toJson(team)
                    teamFile.writeText(output)
                }
            })
        }
        //no internet; adds to file instead
        else {
            Toast.makeText(
                this@ModifyTeamActivity,
                "Data saved to text file.",
                Toast.LENGTH_SHORT
            ).show()
            val path: File = filesDir
            val letDirectory = File(path, "LET")
            letDirectory.mkdirs()
            val outFile = File(letDirectory, "teams.txt")
            outFile.appendText("${team.number}\n")
            val teamFile = File(letDirectory, "team${team.number}.json")
            val gson = Gson()
            val output: String = gson.toJson(team)
            teamFile.writeText(output)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    companion object {
        const val MODE = "mode" //0: New Team 1: Modify existing team
    }
}
