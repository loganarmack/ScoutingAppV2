package com.example.logan.scoutingappv2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun createNewTeam(view: View) {
        val newTeamIntent = Intent(this, ModifyTeamActivity::class.java)
        newTeamIntent.putExtra("mode", 0)
        startActivity(newTeamIntent)
    }

    fun modifyTeam(view: View) {
        //TODO: FIX THIS! THIS SHOULD BE VIEW TEAM RATHER THAN DIRECTLY RUNNING MODIFY TEAM
        val modifyTeamIntent = Intent(this, ModifyTeamActivity::class.java)
        modifyTeamIntent.putExtra("mode", 1)
        startActivity(modifyTeamIntent)
    }

    fun viewSavedTeams(view: View) {
        val viewSavedTeamsIntent = Intent(this, ModifyTeamActivity::class.java)
        viewSavedTeamsIntent.putExtra("mode", 2)
        startActivity(viewSavedTeamsIntent)
    }

    fun pushData(view: View) {
        //retrofit stuff
    }
}
