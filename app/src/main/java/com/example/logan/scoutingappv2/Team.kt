package com.example.logan.scoutingappv2

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//stores a team that can be easily converted to json
@Parcelize
class Team(@SerializedName("name")                 var name: String? = null,
           @SerializedName("location")             var location: String? = null,
           @SerializedName("number")               var number: Int? = null,
           @SerializedName("notes")                var notes: String? = null,
           @SerializedName("issues")               var issues: String? = null,
           @SerializedName("objective_score")      var objectiveScore: Int? = null,
           @SerializedName("consistency")          var consistency: Int? = null,
           @SerializedName("driver_skill")         var driverSkill: Int? = null,
           @SerializedName("sandstorm")            var autonomous: Int? = null,
           @SerializedName("defense")              var defense: Int? = null): Parcelable
