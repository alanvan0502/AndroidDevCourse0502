/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.materialme

import android.media.MediaRouter
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import java.util.*

/***
 * Main Activity for the Material Me app, a mock sports news application
 * with poor design choices.
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val SPORT_DATA_KEY = "sport_data_key"
    }
    // Member variables.
    private var mRecyclerView: RecyclerView? = null
    private var mSportsData: ArrayList<Sport>? = null
    private var mAdapter: SportsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.recyclerView)

        // Set the Layout Manager.
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)

        // Initialize the ArrayList that will contain the data.
        mSportsData = ArrayList()

        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = SportsAdapter(this, mSportsData!!)
        mRecyclerView!!.adapter = mAdapter

        // Get the data.
        initializeData()

        // ItemTouchHelper
        val helper = ItemTouchHelper(
                object: ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.LEFT or
                                ItemTouchHelper.RIGHT or
                        ItemTouchHelper.UP or
                        ItemTouchHelper.DOWN
                        , ItemTouchHelper.LEFT or
                        ItemTouchHelper.RIGHT) {
            override fun onMove(
                    recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                     target: RecyclerView.ViewHolder)
                    : Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                Collections.swap(mSportsData, from, to)
                mAdapter?.notifyItemMoved(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                mSportsData?.removeAt(viewHolder.adapterPosition)
                mAdapter?.notifyItemRemoved(viewHolder.adapterPosition)
            }
        })

        helper.attachToRecyclerView(mRecyclerView)
    }


    /**
     * Initialize the sports data from resources.
     */
    private fun initializeData() {
        // Get the resources from the XML file.
        val sportsList = resources
                .getStringArray(R.array.sports_titles)
        val sportsInfo = resources
                .getStringArray(R.array.sports_info)
        val sportsImageResources = resources.obtainTypedArray(R.array.sports_images)

        // Clear the existing data (to avoid duplication).
        mSportsData!!.clear()

        // Create the ArrayList of Sports objects with titles and
        // information about each sport.
        for (i in sportsList.indices) {
            mSportsData!!.add(Sport
            (sportsList[i], sportsInfo[i], sportsImageResources.getResourceId(i, 0)))
        }

        // Clean up the sportsIageResources TypedArray to avoid duplications
        sportsImageResources.recycle()

        // Notify the adapter of the change.
        mAdapter!!.notifyDataSetChanged()
    }

    fun resetSports(view: View) {
        initializeData()
    }

    // Restores the scrolling position of the Recycler List
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        // This ensure that mAdapter does not lose reference of the mSportsData, otherwise
        // notifyDataSetChanged would not work
        if (savedInstanceState != null) {
           val restoreSportsData: ArrayList<Sport>? =
                    savedInstanceState.getParcelableArrayList(SPORT_DATA_KEY)
            mSportsData?.clear()
            for (s in restoreSportsData!!)
                mSportsData?.add(s)
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelableArrayList(SPORT_DATA_KEY, mSportsData)
    }
}

