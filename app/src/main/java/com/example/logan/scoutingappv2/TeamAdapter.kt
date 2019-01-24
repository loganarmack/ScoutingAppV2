package com.example.logan.scoutingappv2

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.team_list_element.view.*

//adapter for the collection list recyclerview
class TeamAdapter(private val collectionsDataset: List<Team>,
                  private val context: Context,
                  private val clickListener: (Team) -> Unit): RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    //sets content of view
    inner class TeamViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(team: Team, clickListener: (Team) -> Unit) = with(itemView) {
            team_name.text = context.getString(R.string.team_name_number, team.number, team.name)
            setOnClickListener { clickListener(team) }
        }
    }

    //create new view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamAdapter.TeamViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.team_list_element, parent, false) as View

        return TeamViewHolder(itemView)
    }

    //replace contents of view
    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(collectionsDataset[position], clickListener)
    }

    //return size of dataset
    override fun getItemCount() = collectionsDataset.size
}