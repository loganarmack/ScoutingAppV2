package com.example.logan.scoutingappv2

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//stores a team that can be easily converted to json
@Parcelize
class Team(@SerializedName("name")                 var name: String? = null,
           @SerializedName("wheelType")            var wheelType: String? = null,
           @SerializedName("number")               var number: Int = -1,
           @SerializedName("notes")                var notes: String? = null,
           @SerializedName("issues")               var issues: String? = null,
           @SerializedName("objective_score")      var objectiveScore: Int? = null,
           @SerializedName("consistency")          var consistency: Int? = null,
           @SerializedName("driver_skill")         var driverSkill: Int? = null,
           @SerializedName("autonomous")           var autonomous: Int? = null,
           @SerializedName("defence")              var defence: Int? = null,
           @SerializedName("moveCargo")            var moveCargo: Boolean? = null,
           @SerializedName("cargoMax")             var cargoMax: Int? = null,
           @SerializedName("cargoPickup")          var cargoPickup: Int? = null,
           @SerializedName("moveHatch")            var moveHatch: Boolean? = null,
           @SerializedName("hatchMax")             var hatchMax: Int? = null,
           @SerializedName("hatchPickup")          var hatchPickup: Int? = null,
           @SerializedName("climbLevel")           var climbLevel: Int? = null,
           @SerializedName("climbNotes")           var climbNotes: String? = null,
           @SerializedName("sandstormMode")        var sandstormMode: Int? = null,
           @SerializedName("sandstormNotes")       var sandstormNotes: Int? = null): Parcelable

