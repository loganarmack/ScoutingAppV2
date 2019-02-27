package com.example.logan.scoutingappv2

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.SimpleAdapter
import android.widget.Toast
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException

class ViewTeamActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: TeamAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var teams = ArrayList<Team>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_team)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }

        //sets up recyclerview
        viewManager = LinearLayoutManager(this)
        viewAdapter = TeamAdapter(teams, this) {
            onCollectionClick(it) //sets onClick function for each item in the list
        }
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                val adapter = recyclerView.adapter as TeamAdapter
                deleteTeam(p0.adapterPosition)
                adapter.removeAt(p0.adapterPosition)
            }
        }
        recyclerView = findViewById<RecyclerView>(R.id.team_recycler).apply {
            layoutManager = viewManager
            addItemDecoration(CustomDivider(this@ViewTeamActivity))
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val mode = intent.getIntExtra(MODE, 1)
        when (mode) {
            0 -> loadTeamsFromInternet()
            1 -> loadTeamsFromFile()
        }
    }

    //adds animation when returning to view activity
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun loadTeamsFromInternet() {
        val accessor = APIAccessor()
        //sends call to get collections from api
        val call = accessor.apiService.loadTeams()

        //starts loading icon
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        //receives response from server
        call.enqueue(object: Callback<APITeamResponse> {
            override fun onResponse(call: Call<APITeamResponse>, response: Response<APITeamResponse>) {
                if (response.isSuccessful) {
                    val apiResponse: List<Team>? = response.body()?.data
                    if (apiResponse == null) {
                        //Api returned no data but received request
                        progressBar.visibility = View.GONE
                        toast("Failed to access the API!", this@ViewTeamActivity)
                    }
                    //data is loaded successfully
                    else {
                        progressBar.visibility = View.GONE

                        //adds collections into recyclerview
                        for (i in apiResponse) {
                            teams.add(i)
                        }
                        viewAdapter.notifyDataSetChanged()
                    }
                }
                else {
                    //server doesn't accept request
                    progressBar.visibility = View.GONE
                    toast("An unknown error has occurred.", this@ViewTeamActivity)
                }
            }
            //no internet
            override fun onFailure(call: Call<APITeamResponse>, t: Throwable) {
                //TODO: Proper no internet page
                toast("No internet connection!", this@ViewTeamActivity)
            }
        })
    }

    //loads teams into recyclerview from files
    private fun loadTeamsFromFile() {
        //initializes file directory
        val path: File = filesDir
        val teamNumbers: MutableList<Int> = mutableListOf()
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()
        try {
            val file = File(letDirectory, "teams.txt")
            val gson = Gson()

            //adds team to upload to upload list
            file.forEachLine {
                teamNumbers.add(it.toInt())
            }
            for (i in teamNumbers) {
                try {
                    val teamFile = File(letDirectory, "team$i.json")

                    val team: Team = gson.fromJson(teamFile.readText(), Team::class.java)
                    teams.add(team)
                } catch (e: FileNotFoundException) {
                    Log.d("MISSING_TEAM_FILE", "Missing file for team $i!")
                }
            }
            viewAdapter.notifyDataSetChanged()
        } catch (e: FileNotFoundException) {
            toast("There is no data stored!", this@ViewTeamActivity)
        }
    }

    //starts new activity when collection is clicked
    private fun onCollectionClick(team: Team) {
        //sets intent for new collection, passing the collection to the new activity
        val modifyTeamIntent = Intent(this, ModifyTeamActivity::class.java)
        modifyTeamIntent.putExtra(ModifyTeamActivity.TEAM, team)
        modifyTeamIntent.putExtra(ModifyTeamActivity.MODE, 1)
        startActivity(modifyTeamIntent)
        //adds transition animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    private fun deleteTeam(i: Int) {
        if (intent.getIntExtra(MODE, 1) == 0) {
            val accessor = APIAccessor()
            val call: Call<Team> = accessor.apiService.deleteTeam(teams[i].number)

            call.enqueue(object : Callback<Team> {
                override fun onResponse(call: Call<Team>, response: Response<Team>) {
                    if (response.isSuccessful) {
                        //Worked!
                        Log.d("DELETE_TEAM", "Successfully deleted team $i")
                    } else {
                        //Error
                        Log.d("DELETE_TEAM", "Failed to delete team $i")
                        toast("Failed to delete team $i", this@ViewTeamActivity)
                    }
                }

                override fun onFailure(call: Call<Team>, t: Throwable) {
                    //no response received for delete request
                    Log.d("DELETE_TEAM", "Failed to delete team $i")
                    toast("Failed to delete team $i. Check your internet and try again!", this@ViewTeamActivity)
                }
            })
        }
        else {
            val path: File = filesDir
            val teamNumbers: MutableList<Int> = mutableListOf()
            val letDirectory = File(path, "LET")
            letDirectory.mkdirs()

            try {
                val file = File(letDirectory, "teams.txt")

                //adds team to upload to upload list
                file.forEachLine {
                    teamNumbers.add(it.toInt())
                }
                if (teamNumbers.contains(teams[i].number)) {
                    teamNumbers.removeAt(teamNumbers.indexOf(teams[i].number))

                    File(letDirectory, "team${teams[i].number}.json").delete()
                    file.delete()

                    //create new file of teams
                    val teamListFile = File(letDirectory, "teams.txt")
                    for (teamNum in teamNumbers) {
                        teamListFile.appendText("$teamNum\n")
                    }
                }
                else {
                    Log.d("TEAM_FILE_DELETION","Attempt to delete non-existent team")
                }
            } catch (e: FileNotFoundException) {
                Log.d("TEAM_FILE_DELETION","Attempt to delete non-existent team")
            }
        }
    }

    companion object {
        fun toast(text: String, context: Context) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
        const val MODE = "mode" //0: View online teams  1: View saved teams
    }
}