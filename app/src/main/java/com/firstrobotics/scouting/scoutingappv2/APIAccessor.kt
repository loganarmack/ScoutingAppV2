package com.firstrobotics.scouting.scoutingappv2

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "https://frscout.herokuapp.com/api/v1/"

//Requests that can be sent to the api
interface LisgarFRScoutAPI {
    @POST("teams/")
    fun createTeam(@Body team: Team): Call<Team>

    @DELETE("teams/{teamNumber}")
    fun deleteTeam(@Path("teamNumber") teamNumber: Int): Call<Team>

    @GET("teams/")
    fun loadTeams(): Call<APITeamResponse>
}

//initializes the standard api accessor that is used throughout the code
class APIAccessor {
    var apiService: LisgarFRScoutAPI
    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        apiService = retrofit.create(LisgarFRScoutAPI::class.java)
    }
}

//stores response from API
class APITeamResponse {
    var data: MutableList<Team> = mutableListOf()
    var status: String = ""
    var message: String = ""
}