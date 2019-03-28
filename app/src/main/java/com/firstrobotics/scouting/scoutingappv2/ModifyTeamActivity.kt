package com.firstrobotics.scouting.scoutingappv2

import android.animation.LayoutTransition
import android.content.Context
import android.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import com.firstrobotics.scouting.scoutingappv2.databinding.ActivityModifyTeamBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_modify_team.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.reflect.Field


//stores everything relating to creating/editing teams
class ModifyTeamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifyTeamBinding
    private var mode = 0
    private var expandedState: Boolean = false
    private val team: Team = Team()
    private val accessor: APIAccessor = APIAccessor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_team)

        //makes expand/collapse animation work without fading out of checkboxes
        val layoutTransition = LayoutTransition()
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING)
        layoutTransition.disableTransitionType(LayoutTransition.APPEARING)
        binding.parentLayout.layoutTransition = layoutTransition
        binding.inputScrollLayout.layoutTransition = layoutTransition

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

    private fun newTeamModeInit() {
        binding.newTeamTitleText.text = getString(R.string.new_team)
        binding.teamNumberEdit.visibility = View.VISIBLE
    }

    private fun modifyTeamModeInit() {
        val oldTeam: Team = intent.getParcelableExtra(TEAM) ?: Team() //set team from previously selected stuff
        binding.teamNumberEdit.visibility = View.GONE
        binding.newTeamTitleText.text = getString(R.string.modify_team_with_number, oldTeam.number)

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
            else {
                issuesEdit.setText(oldTeam.issues)
            }

            //sets checkboxes to enabled/disabled
            if (oldTeam.driverSkill == null) {
                binding.includeDriverSkillCheck.isChecked = false
                binding.driverExperienceEdit.visibility =  visibleOrGone(binding.includeDriverSkillCheck.isChecked)
            }
            if (oldTeam.name == null) {
                binding.includeNameCheck.isChecked = false
                binding.teamNameEdit.visibility = visibleOrGone(binding.includeNameCheck.isChecked)
            }
            if (oldTeam.issues == null) {
                binding.includeIssuesCheck.isChecked = false
                binding.issuesGroup.visibility = visibleOrGone(binding.includeIssuesCheck.isChecked)
            }
            if (oldTeam.notes == null) {
                binding.includeNotesCheck.isChecked = false
                binding.notesEdit.visibility = visibleOrGone(binding.includeNotesCheck.isChecked)
            }
            if (oldTeam.moveCargo == null && oldTeam.cargoMax == null && oldTeam.cargoPickup == null) {
                binding.includeCargoCheck.isChecked = false
                binding.cargoGroup.visibility = visibleOrGone(binding.includeCargoCheck.isChecked)
            }
            if (oldTeam.moveHatch == null && oldTeam.hatchMax == null && oldTeam.hatchPickup == null) {
                binding.includeHatchCheck.isChecked = false
                binding.hatchGroup.visibility = visibleOrGone(binding.includeHatchCheck.isChecked)
            }
            if (oldTeam.climbLevel == null && oldTeam.climbNotes == null) {
                binding.includeClimbCheck.isChecked = false
                binding.climbGroup.visibility = visibleOrGone(binding.includeClimbCheck.isChecked)
            }
            if (oldTeam.sandstormMode == null && oldTeam.sandstormNotes == null) {
                binding.includeSandstormCheck.isChecked = false
                binding.sandstormNotesGroup.visibility = visibleOrGone(binding.includeSandstormCheck.isChecked)
            }
            if (oldTeam.wheelType == null) {
                binding.includeWheelsCheck.isChecked = false
                binding.wheelsGroup.visibility = visibleOrGone(binding.includeWheelsCheck.isChecked)
            }

            //sets edit text boxes to team data
            teamNameEdit.setText(oldTeam.name)
            notesEdit.setText(oldTeam.notes)
            sandstormNotesEdit.setText(oldTeam.sandstormNotes)
            climbNotesEdit.setText(oldTeam.climbNotes)

            //checks sandstorm mode, climb level, and cargo/hatch max level and wheel type
            when (oldTeam.wheelType) {
                getString(R.string.wheel_type_1) -> binding.wheelType1Radio.isChecked = true
                getString(R.string.wheel_type_2) -> binding.wheelType2Radio.isChecked = true
                getString(R.string.wheel_type_3) -> binding.wheelType3Radio.isChecked = true
                getString(R.string.wheel_type_4) -> binding.wheelType4Radio.isChecked = true
                getString(R.string.wheel_type_5) -> binding.wheelType5Radio.isChecked = true
                else -> {
                    if (oldTeam.wheelType != "" && oldTeam.wheelType != null) {
                        binding.otherWheelTypeRadio.isChecked = true
                        binding.wheelsEdit.setText(oldTeam.wheelType)
                        binding.wheelsEdit.isEnabled = true
                    }
                }
            }
            when (oldTeam.sandstormMode) {
                1 -> sandstormBlindModeRadio.isChecked = true
                2 -> sandstormCameraModeRadio.isChecked = true
                3 -> sandstormAutoModeRadio.isChecked = true
            }
            when (oldTeam.climbLevel) {
                1 -> climbLevel1Radio.isChecked = true
                2 -> climbLevel2Radio.isChecked = true
                3 -> climbLevel3Radio.isChecked = true
            }
            when (oldTeam.cargoMax) {
                1 -> cargoMax1Radio.isChecked = true
                2 -> cargoMax2Radio.isChecked = true
                3 -> cargoMax3Radio.isChecked = true
            }
            when (oldTeam.hatchMax) {
                1 -> hatchMax1Radio.isChecked = true
                2 -> hatchMax2Radio.isChecked = true
                3 -> hatchMax3Radio.isChecked = true
            }

            //sets pickup locations of hatch and cargo
            when (oldTeam.cargoPickup) {
                1 -> cargoPickupFloorCheck.isChecked = true
                2 -> cargoPickupHumanCheck.isChecked = true
                3 -> {
                    cargoPickupFloorCheck.isChecked = true
                    cargoPickupHumanCheck.isChecked = true
                }
            }
            when (oldTeam.hatchPickup) {
                1 -> hatchPickupFloorCheck.isChecked = true
                2 -> hatchPickupHumanCheck.isChecked = true
                3 -> {
                    hatchPickupFloorCheck.isChecked = true
                    hatchPickupHumanCheck.isChecked = true
                }
            }

            binding.driverExperienceEdit.setText(oldTeam.driverSkill.toString())

            //sets whether or not robot can pick up hatch/cargo
            moveCargoCheck.isChecked = oldTeam.moveCargo ?: false
            moveHatchCheck.isChecked = oldTeam.moveHatch ?: false

            inputScroll.visibility = View.VISIBLE
        }
        team.number = oldTeam.number
    }

    //makes team modification sections visible/invisible
    fun includeCheck(view: View) {
        when (view.id) {
            binding.includeDriverSkillCheck.id -> {
                binding.driverExperienceEdit.visibility =  visibleOrGone(binding.includeDriverSkillCheck.isChecked)
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
            binding.includeCargoCheck.id -> {
                binding.cargoGroup.visibility = visibleOrGone(binding.includeCargoCheck.isChecked)
            }
            binding.includeHatchCheck.id -> {
                binding.hatchGroup.visibility = visibleOrGone(binding.includeHatchCheck.isChecked)
            }
            binding.includeClimbCheck.id -> {
                binding.climbGroup.visibility = visibleOrGone(binding.includeClimbCheck.isChecked)
            }
            binding.includeSandstormCheck.id -> {
                binding.sandstormNotesGroup.visibility = visibleOrGone(binding.includeSandstormCheck.isChecked)
            }
            binding.includeWheelsCheck.id -> {
                binding.wheelsGroup.visibility = visibleOrGone(binding.includeWheelsCheck.isChecked)
            }
        }
    }

    //makes "other" wheel type editable if other radio button is clicked
    fun onWheelTypeClick(view: View) {
        when (view.id) {
            binding.otherWheelTypeRadio.id -> binding.wheelsEdit.isEnabled = true
            else -> binding.wheelsEdit.isEnabled = false
        }
    }

    private fun visibleOrGone(b: Boolean) = if (b) View.VISIBLE else View.GONE

    //hides/shows include checkboxes
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
                    getString(R.string.invalid_team_number),
                    Toast.LENGTH_SHORT
                ).show()
                return 1
            }
            if (team.number < 0) {
                Toast.makeText(
                    this@ModifyTeamActivity,
                    getString(R.string.invalid_team_number),
                    Toast.LENGTH_SHORT
                ).show()
                return 1
            }
        }
        if (binding.includeNameCheck.isChecked) {
            team.name = binding.teamNameEdit.text.toString()
        }
        if (binding.includeIssuesCheck.isChecked) {
            //sets up issues string based on text + checkboxes
            if (!binding.hardwareIssueCheckbox.isChecked && !binding.softwareIssueCheckbox.isChecked && binding.issuesEdit.text.toString().isEmpty()) {
                team.issues = "None"
            } else {
                if (binding.hardwareIssueCheckbox.isChecked) {
                    team.issues = "Hardware"
                }
                if (binding.softwareIssueCheckbox.isChecked) {
                    if (team.issues != "") {
                        team.issues += ", Software"
                    } else {
                        team.issues = "Software"
                    }
                }
                if (binding.otherIssueCheckbox.isChecked) {
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
        if (binding.includeWheelsCheck.isChecked) {
            team.wheelType = when {
                binding.wheelType1Radio.isChecked -> getString(R.string.wheel_type_1)
                binding.wheelType2Radio.isChecked -> getString(R.string.wheel_type_2)
                binding.wheelType3Radio.isChecked -> getString(R.string.wheel_type_3)
                binding.wheelType4Radio.isChecked -> getString(R.string.wheel_type_4)
                binding.wheelType5Radio.isChecked -> getString(R.string.wheel_type_5)
                else -> binding.wheelsEdit.text.toString()
            }
        }
        if (binding.includeDriverSkillCheck.isChecked) {
            try {
                team.driverSkill = binding.driverExperienceEdit.text.toString().toInt()
            } catch (e: NumberFormatException) { //if left blank
                team.driverSkill = null
            }
            if (team.number < 0) {
                team.driverSkill = null
            }
        }
        if (binding.includeCargoCheck.isChecked) {
            team.moveCargo = binding.moveCargoCheck.isChecked
            team.cargoPickup = when {
                binding.cargoPickupFloorCheck.isChecked && binding.cargoPickupHumanCheck.isChecked -> 3
                binding.cargoPickupHumanCheck.isChecked -> 2
                binding.cargoPickupFloorCheck.isChecked -> 1
                else -> 0
            }
            team.cargoMax = when {
                binding.cargoMax3Radio.isChecked -> 3
                binding.cargoMax2Radio.isChecked -> 2
                binding.cargoMax1Radio.isChecked -> 1
                else -> 0
            }
        }
        if (binding.includeHatchCheck.isChecked) {
            team.moveHatch = binding.moveHatchCheck.isChecked
            team.hatchPickup = when {
                binding.hatchPickupFloorCheck.isChecked && binding.hatchPickupHumanCheck.isChecked -> 3
                binding.hatchPickupHumanCheck.isChecked -> 2
                binding.hatchPickupFloorCheck.isChecked -> 1
                else -> 0
            }
            team.hatchMax = when {
                binding.hatchMax3Radio.isChecked -> 3
                binding.hatchMax2Radio.isChecked -> 2
                binding.hatchMax1Radio.isChecked -> 1
                else -> 0
            }
        }
        if (binding.includeClimbCheck.isChecked) {
            team.climbNotes = binding.climbNotesEdit.text.toString()
            team.climbLevel = when {
                binding.climbLevel1Radio.isChecked -> 1
                binding.climbLevel2Radio.isChecked -> 2
                binding.climbLevel3Radio.isChecked -> 3
                else -> 0
            }
        }
        if (binding.includeSandstormCheck.isChecked) {
            team.sandstormNotes = binding.sandstormNotesEdit.text.toString()
            team.sandstormMode = when {
                binding.sandstormBlindModeRadio.isChecked -> 1
                binding.sandstormCameraModeRadio.isChecked -> 2
                binding.sandstormAutoModeRadio.isChecked -> 3
                else -> null
            }
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
                getString(R.string.invalid_team_number),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (mode == 0) {
            //clears all text input
            binding.teamNameEdit.text.clear()
            binding.teamNumberEdit.text.clear()
            binding.issuesEdit.text.clear()
            binding.notesEdit.text.clear()
            binding.sandstormNotesEdit.text.clear()
            binding.climbNotesEdit.text.clear()
            binding.wheelsEdit.text.clear()
            binding.driverExperienceEdit.text.clear()

            //reset check boxes
            binding.hardwareIssueCheckbox.isChecked = false
            binding.softwareIssueCheckbox.isChecked = false
            binding.otherIssueCheckbox.isChecked = false
        }
        if (isNetworkAvailable()) {
            //attempts to upload data to server; if it fails, adds it to text file instead
            val call: Call<Team> = accessor.apiService.createTeam(team)
            call.enqueue(object : Callback<Team> {
                override fun onResponse(call: Call<Team>, response: Response<Team>) {
                    if (response.isSuccessful) {
                        //server accepts team data
                        Toast.makeText(
                            this@ModifyTeamActivity, getString(R.string.successful_upload),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //server rejects team data
                        Toast.makeText(
                            this@ModifyTeamActivity, getString(R.string.invalid_team_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Team>, t: Throwable) {
                    Toast.makeText(
                        this@ModifyTeamActivity,
                        getString(R.string.data_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                    val path: File = filesDir
                    val letDirectory = File(path, "LET")
                    letDirectory.mkdirs()
                    val outFile = File(letDirectory, "teams.txt")
                    val teamFile = File(letDirectory, "team${team.number}.json")
                    val gson = Gson()

                    //only appends team number onto list if teamfile doesn't already exist
                    if (!teamFile.exists()) {
                        outFile.appendText("${team.number}\n")
                        val output: String = gson.toJson(team)
                        teamFile.writeText(output)
                    }
                    else {
                        val storedTeam: Team = gson.fromJson(teamFile.readText(), Team::class.java)

                        //new edit overrides previous ones, but null are replaced by previously stored ones
                        for (field: Field in team.javaClass.declaredFields) {
                            if (field.get(team) == null && field.get(storedTeam) != null) {
                                field.set(team, field.get(storedTeam))
                            }
                        }

                        //adds to file
                        val output: String = gson.toJson(team)
                        teamFile.writeText(output)
                    }
                }
            })
        }
        //no internet; adds to file instead
        else {
            Toast.makeText(
                this@ModifyTeamActivity,
                getString(R.string.data_saved),
                Toast.LENGTH_SHORT
            ).show()
            val path: File = filesDir
            val letDirectory = File(path, "LET")
            letDirectory.mkdirs()
            val outFile = File(letDirectory, "teams.txt")
            val teamFile = File(letDirectory, "team${team.number}.json")
            val gson = Gson()

            //only appends team number onto list if team file doesn't already exist
            if (!teamFile.exists()) {
                outFile.appendText("${team.number}\n")
                val output: String = gson.toJson(team)
                teamFile.writeText(output)
            }
            else {
                val storedTeam: Team = gson.fromJson(teamFile.readText(), Team::class.java)

                //new edit overrides previous ones, but null are replaced by previously stored ones
                for (field: Field in team.javaClass.declaredFields) {
                    field.isAccessible = true
                    if (field.get(team) == null && field.get(storedTeam) != null) {
                        field.set(team, field.get(storedTeam))
                    }
                }

                //adds to file
                val output: String = gson.toJson(team)
                teamFile.writeText(output)
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    companion object {
        const val MODE = "mode" //0: New Team 1: Modify existing team
        const val TEAM = "team" //stores team passed into function by modify team
    }
}
