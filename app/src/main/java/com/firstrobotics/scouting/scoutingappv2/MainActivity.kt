package com.firstrobotics.scouting.scoutingappv2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import com.firstrobotics.scouting.scoutingappv2.ViewTeamActivity.Companion.toast


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newTeamButton = findViewById<ConstraintLayout>(R.id.new_team_button)
        val modifyTeamButton = findViewById<ConstraintLayout>(R.id.modify_team_button)
        val viewSavedButton = findViewById<ConstraintLayout>(R.id.view_saved_button)
        val pushButton = findViewById<ConstraintLayout>(R.id.push_button)

        val window = this.window

        //sets color of status bar if possible
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }

        newTeamButton.apply {
            setOnClickListener {
                createNewTeam(it)
            }
            findViewById<TextView>(R.id.button_text).text = getString(R.string.new_team)
        }
        modifyTeamButton.apply {
            setOnClickListener {
                modifyTeam(it)
            }
            findViewById<TextView>(R.id.button_text).text = getString(R.string.modify_team)
        }
        viewSavedButton.apply {
            setOnClickListener {
                viewSavedTeams(it)
            }
            findViewById<TextView>(R.id.button_text).text = getString(R.string.view_saved)
        }
        pushButton.apply {
            setOnClickListener {
                pushData(it)
            }
            findViewById<TextView>(R.id.button_text).text = getString(R.string.push)
        }
    }

    fun createNewTeam(view: View) {
        val newTeamIntent = Intent(this, ModifyTeamActivity::class.java)
        newTeamIntent.putExtra(ModifyTeamActivity.MODE, 0)
        startActivity(newTeamIntent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    fun modifyTeam(view: View) {
        val modifyTeamIntent = Intent(this, ViewTeamActivity::class.java)
        modifyTeamIntent.putExtra(ViewTeamActivity.MODE, 0)
        startActivity(modifyTeamIntent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    fun viewSavedTeams(view: View) {
        val viewSavedTeamsIntent = Intent(this, ViewTeamActivity::class.java)
        viewSavedTeamsIntent.putExtra(ViewTeamActivity.MODE, 1)
        startActivity(viewSavedTeamsIntent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    private fun pushData(view: View) {
        //initializes file directory
        val path: File = filesDir
        val teams: MutableList<Int> = mutableListOf()
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()
        val accessor = APIAccessor()

        val file = File(letDirectory, "teams.txt")
        if (!file.exists()) {
            toast(getString(R.string.no_data), this@MainActivity)
            return
        }

        val gson = Gson()

        //adds team to upload to upload list
        var counter = 0 //stores number of expected callbacks
        file.forEachLine {
            teams.add(it.toInt())
            counter++
        }
        var callbackCount = 0 //stores current number of received callbacks
        var displayedNoInternet = false


        //loops through each team in the teams.txt file
        for (i in teams) {
            try {
                val teamFile = File(letDirectory, "team$i.json")

                //create new team
                //sets request for team
                val team: Team = gson.fromJson(teamFile.readText(), Team::class.java)
                val call: Call<Team> = accessor.apiService.createTeam(team)

                //receives response
                call.enqueue(object : Callback<Team> {
                    override fun onResponse(call: Call<Team>, response: Response<Team>) {
                        callbackCount++
                        if (response.isSuccessful) {
                            //uploaded successfully; team file deleted
                            teamFile.delete()
                        } else {
                            //shouldn't occur, but exists just in case
                            toast(getString(R.string.invalid_team_data), this@MainActivity)
                        }
                        //all responses sent out have been received
                        if (callbackCount == counter && !displayedNoInternet) {
                            toast(getString(R.string.successful_upload), this@MainActivity)
                            //deletes teams to upload file
                            file.delete()
                        }
                    }

                    override fun onFailure(call: Call<Team>, t: Throwable) {
                        if (!displayedNoInternet) {
                            toast(getString(R.string.failed_to_upload).plus(getString(R.string.check_internet)), this@MainActivity)
                            displayedNoInternet = true
                        }
                    }
                })
            }
            catch (e: FileNotFoundException) {
                //Log.d("MISSING_TEAM_FILE", "Missing file for team $i!")
            }
        }
    }
}
