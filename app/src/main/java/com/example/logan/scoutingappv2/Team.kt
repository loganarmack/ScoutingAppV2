package com.example.logan.scoutingappv2

import com.google.gson.annotations.SerializedName

//stores a team that can be easily converted to json
class Team {
    @SerializedName("name")                 var name: String = ""
    @SerializedName("number")               var number: Int = -1
    @SerializedName("notes")                var notes: String = ""
    @SerializedName("issues")               var issues: String = ""
    @SerializedName("objective_score")      var objectiveScore: Int = 5
    @SerializedName("consistency")          var consistency: Int = 5
    @SerializedName("driver_skill")         var driverSkill: Int = 5
    @SerializedName("sandstorm")            var autonomous: Int = 5
    @SerializedName("defense")              var defense: Int = 5
}
