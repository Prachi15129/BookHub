package com.prachi.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.media.audiofx.BassBoost
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.prachi.bookhub.R
import com.prachi.bookhub.adapter.DashboardRecyclerAdapter
import com.prachi.bookhub.model.Book
import com.prachi.bookhub.util.ConnectionManager
import org.json.JSONException


class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard : RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager

    lateinit var btnCheckInternet:Button


    lateinit var recyclerAdapter:DashboardRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        btnCheckInternet = view.findViewById(R.id.btnCheckInternet)
        btnCheckInternet.setOnClickListener {

            if (ConnectionManager().checkConnectivity(activity as Context)) {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection Found")
                dialog.setPositiveButton("ok") { text, listener -> }
                dialog.setNegativeButton("cancel") { text, listener -> }
                dialog.create()
                dialog.show()
            }
            else
            {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Failure")
                dialog.setMessage("Internet  Not Connection Found")
                dialog.setPositiveButton("ok") { text, listener -> }
                dialog.setNegativeButton("cancel") { text, listener -> }
                dialog.create()
                dialog.show()

            }

        }





        layoutManager = LinearLayoutManager(activity)
        //recyclerAdapter = DashboardRecyclerAdapter(activity as Context, bookList)
        recyclerDashboard.adapter = recyclerAdapter
        recyclerDashboard.layoutManager = layoutManager
         val bookInfoList= arrayListOf<Book>()

        val queue= Volley.newRequestQueue(activity as Context)
        val url ="http://13.235.250.119/v1/book/fetch_books/"
        if(ConnectionManager().checkConnectivity(activity as Context))
        {
            val jsonObjectRequest=object : JsonObjectRequest(Request.Method.GET,url,null, Response.Listener {
                try
                {
                    val success =it.getBoolean("success")
                    if(success)
                    {
                        val data=it.getJSONArray("data")
                        for(i in 0 until data.length())
                        {
                            val bookJsonObject=data.getJSONObject(i)
                            val bookObject= Book(
                                bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image")


                            )
                            bookInfoList.add(bookObject)
                            recyclerAdapter=DashboardRecyclerAdapter(activity as Context,bookInfoList)
                            recyclerDashboard.adapter=recyclerAdapter
                            recyclerDashboard.layoutManager=layoutManager
                            recyclerDashboard.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerDashboard.context,(layoutManager as LinearLayoutManager).orientation
                                )


                            )
                        }

                    }
                    else
                    {
                        android.widget.Toast.makeText(activity as Context,"some error has occured!!",
                            android.widget.Toast.LENGTH_LONG).show()
                    }


                }
                catch (e: JSONException)
                {
                    Toast.makeText(activity as Context,"some unexpected error occurred!!!",Toast.LENGTH_SHORT).show()

                }



            },Response.ErrorListener {
                Toast.makeText(activity as Context,"Volley error occurred!!!!",Toast.LENGTH_SHORT).show()

            })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="9bf534118365f1"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else
        {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failure")
            dialog.setMessage("Internet  Not Connection Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent =Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }



        return view
    }
    }


