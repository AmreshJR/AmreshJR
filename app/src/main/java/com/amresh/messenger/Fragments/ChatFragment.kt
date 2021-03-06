package com.amresh.messenger.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amresh.messenger.AdaptorClasses.UserAdaptor
import com.amresh.messenger.ModelClasses.ChatList
import com.amresh.messenger.ModelClasses.Users
import com.amresh.messenger.Notifications.Token
import com.amresh.messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private var userAdapter:   UserAdaptor?= null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recycler_view_chat_list: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_chat, container, false)


        recycler_view_chat_list = view.findViewById(R.id.recycler_view_chat_list)
        recycler_view_chat_list.setHasFixedSize(true)
        recycler_view_chat_list.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref =  FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot)
            {
                (usersChatList as ArrayList).clear()

                for(dataSnapshot in p0.children)
                {
                    val chatlist = dataSnapshot.getValue(ChatList::class.java)
                    (usersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })


        updateToken(FirebaseInstanceId.getInstance().token)

        return view
    }

    private fun updateToken(token: String?)
    {

        var ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList(){

            mUsers = ArrayList()

            val ref = FirebaseDatabase.getInstance().reference.child("Users")
            ref!!.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot)
                {
                    (mUsers as ArrayList).clear()
                    for(dataSnapshot in p0.children)
                    {
                        val user = dataSnapshot.getValue(Users::class.java)

                        for (eachChatList in usersChatList!! )
                        {
                            if(user!!.getUID().equals(eachChatList.getId()))
                            {
                                (mUsers as ArrayList).add(user!!)
                            }
                        }
                    }
                    userAdapter = UserAdaptor(context!!, (mUsers as ArrayList<Users>), true)
                    recycler_view_chat_list.adapter = userAdapter
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

        }

}

