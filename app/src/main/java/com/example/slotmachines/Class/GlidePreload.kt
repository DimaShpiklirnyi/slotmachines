package com.example.slotmachines.Class

import android.content.Context
import com.bumptech.glide.Glide

class GlidePreload(val context: Context,val array: Array<Int>) {
    fun preload(){
        Glide.with(context)
            .load(array[0])
            .preload()

        Glide.with(context)
            .load(array[1])
            .preload()

        Glide.with(context)
            .load(array[2])
            .preload()

        Glide.with(context)
            .load(array[3])
            .preload()

        Glide.with(context)
            .load(array[4])
            .preload()
    }
}