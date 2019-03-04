package com.firstrobotics.scouting.scoutingappv2

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//stores a team that can be easily converted to json
@Parcelize
class Team(@SerializedName("name")                 var name: String? = null,
           @SerializedName("wheel_type")            var wheelType: String? = null,
           @SerializedName("number")               var number: Int = -1,
           @SerializedName("notes")                var notes: String? = null,
           @SerializedName("issues")               var issues: String? = null,
           @SerializedName("objective_score")      var objectiveScore: Int? = null,
           @SerializedName("consistency")          var consistency: Int? = null,
           @SerializedName("driver_skill")         var driverSkill: Int? = null,
           @SerializedName("autonomous")           var autonomous: Int? = null,
           @SerializedName("defence")              var defence: Int? = null,
           @SerializedName("move_cargo")            var moveCargo: Boolean? = null,
           @SerializedName("cargo_max")             var cargoMax: Int? = null,
           @SerializedName("cargo_pickup")          var cargoPickup: Int? = null,
           @SerializedName("move_hatch")            var moveHatch: Boolean? = null,
           @SerializedName("hatch_max")             var hatchMax: Int? = null,
           @SerializedName("hatch_pickup")          var hatchPickup: Int? = null,
           @SerializedName("climb_level")           var climbLevel: Int? = null,
           @SerializedName("climb_notes")           var climbNotes: String? = null,
           @SerializedName("sandstorm_mode")        var sandstormMode: Int? = null,
           @SerializedName("sandstorm_notes")       var sandstormNotes: String? = null): Parcelable

