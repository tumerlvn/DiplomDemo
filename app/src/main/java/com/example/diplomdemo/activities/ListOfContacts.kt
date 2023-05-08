package com.example.diplomdemo.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomdemo.DemoCommunity
import com.example.diplomdemo.R
import com.example.diplomdemo.adapters.ContactItemAdapter
import com.example.diplomdemo.contacts.Contact
import com.example.diplomdemo.contacts.ContactStore
import com.example.diplomdemo.items.ContactItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.tudelft.ipv8.android.IPv8Android
import kotlin.coroutines.CoroutineContext

class ListOfContacts() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_contacts)





        var contacts = emptyList<Contact>()
        val contactItemList = ArrayList<ContactItem>()
        contacts.forEach {
            contactItemList.add(ContactItem(it.name, it))
        }

        val community = IPv8Android.getInstance().getOverlay<DemoCommunity>()!!


        val recyclerView = findViewById<RecyclerView>(R.id.contacts_recycler_view)
        val adapter = ContactItemAdapter(contactItemList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val button = findViewById<Button>(R.id.btnUpdateListOfContacts)
        button.setOnClickListener{
            Log.d("Demo.button", "clicked")
            GlobalScope.async {
                contacts = ContactStore.getInstance(this@ListOfContacts).getContacts().first()
                withContext(Dispatchers.Main){
                    contactItemList.clear()
                    contacts.forEach {
                        contactItemList.add(ContactItem(it.name, it))
                    }
                }
            }

            adapter.notifyDataSetChanged()
        }
    }
}