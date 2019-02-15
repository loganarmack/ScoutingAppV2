package com.example.logan.scoutingappv2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.TextView

//TODO: Make buttons play ripple animation when clicked
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newTeamButton = findViewById<ConstraintLayout>(R.id.new_team_button)
        val modifyTeamButton = findViewById<ConstraintLayout>(R.id.modify_team_button)
        val viewSavedButton = findViewById<ConstraintLayout>(R.id.view_saved_button)
        val pushButton = findViewById<ConstraintLayout>(R.id.push_button)

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
        //TODO: FIX THIS! THIS SHOULD BE VIEW TEAM RATHER THAN DIRECTLY RUNNING MODIFY TEAM
        val modifyTeamIntent = Intent(this, ModifyTeamActivity::class.java)
        modifyTeamIntent.putExtra(ModifyTeamActivity.MODE, 1)
        startActivity(modifyTeamIntent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    fun viewSavedTeams(view: View) {
        val viewSavedTeamsIntent = Intent(this, ModifyTeamActivity::class.java)
        viewSavedTeamsIntent.putExtra("mode", 2)
        startActivity(viewSavedTeamsIntent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    fun pushData(view: View) {
        //retrofit stuff
    }
}
