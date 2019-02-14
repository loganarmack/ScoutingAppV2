package com.example.logan.scoutingappv2

import android.animation.Animator
import android.animation.ValueAnimator
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.animation.AnimationUtils
import com.example.logan.scoutingappv2.databinding.ActivityModifyTeamBinding
import kotlinx.android.synthetic.main.activity_modify_team.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast


//stores everything relating to modifying teams -- creating, editing, deleting
class ModifyTeamActivity : AppCompatActivity() {

    private var expandedState: Boolean = true
    private lateinit var binding: ActivityModifyTeamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_team)
    }

    //adds animation when returning to main activity
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    //makes team modification sections visible/invisible
    fun includeCheck(view: View) {
        when (view.id) {
            binding.includeAutoCheck.id -> {
                binding.autoScoreSlider.visibility = visibleOrGone(binding.includeAutoCheck.isChecked)
                binding.autonomousText.visibility =  visibleOrGone(binding.includeAutoCheck.isChecked)
            }
            binding.includeConsistencyCheck.id -> {
                binding.consistencyScoreSlider.visibility = visibleOrGone(binding.includeConsistencyCheck.isChecked)
                binding.consistencyText.visibility =  visibleOrGone(binding.includeConsistencyCheck.isChecked)
            }
            binding.includeDriverSkillCheck.id -> {
                binding.driverSkillScoreSlider.visibility = visibleOrGone(binding.includeDriverSkillCheck.isChecked)
                binding.driverSkillText.visibility =  visibleOrGone(binding.includeDriverSkillCheck.isChecked)
            }
            binding.includeObjectiveCheck.id -> {
                binding.objectiveScoreSlider.visibility = visibleOrGone(binding.includeObjectiveCheck.isChecked)
                binding.objectiveText.visibility =  visibleOrGone(binding.includeObjectiveCheck.isChecked)
            }
            binding.includeDefenceCheck.id -> {
                binding.defenceScoreSlider.visibility = visibleOrGone(binding.includeDefenceCheck.isChecked)
                binding.defenceText.visibility = visibleOrGone(binding.includeDefenceCheck.isChecked)
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
        }
    }

    private fun visibleOrGone(b: Boolean) = if (b) View.VISIBLE else View.GONE

    //makes include checkboxes smaller
    fun expandCollapseCheckboxes(view: View) {
        expandedState = !expandedState
        //TODO: Add animation
        binding.includeChecksGroup.visibility = visibleOrGone(expandedState)
        view.startAnimation(
            if (expandedState)
                AnimationUtils.loadAnimation(this, R.anim.rotate_expand)
            else
                AnimationUtils.loadAnimation(this, R.anim.rotate_collapse)
        )
    }

    companion object {
        const val MODE = "mode"
    }
}
