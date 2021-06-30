package com.techyourchance.dagger2course.screens.common.viewsmvc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.techyourchance.dagger2course.R

open class BaseViewMvc<LISTENER_TYPE>(
    private val layoutInflater: LayoutInflater,
    private val parent: ViewGroup?,
    @LayoutRes private val layoutid: Int) {

    /**
     * removing duplicates from activities into one source. using generic type
     */


    /**
     * each [rootview] has its own layout, therefore we can't initialize it here.
     * but we will make it lateinit and initialize it in its proper place, or use the
     * constructors in the client class to initialize it here.
     * also. instead of making a unique [layoutInflater] for each layout. we create
     * [layoutid] inside constructor to get the id from the class for each layout.
     */
    val rootView: View =
    layoutInflater.inflate(layoutid, parent, false)

    /**
     * the get() = method for [context] allow us to instantiate the context
     * before the initialization of [rootView]
     */
    protected val context : Context get() =  rootView.context



    protected val listeners = HashSet<LISTENER_TYPE>()

    fun registerListener(listener: LISTENER_TYPE){
        listeners.add(listener)
    }

    fun unregisterListener(listener: LISTENER_TYPE){
        listeners.remove(listener)
    }


    /**
     * creating an extension for [findViewById]
     */
    protected fun <T : View?> findViewById(@IdRes id: Int): T {
        return rootView.findViewById<T>(id)
    }

}