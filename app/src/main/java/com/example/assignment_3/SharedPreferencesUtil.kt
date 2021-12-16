package com.example.assignment_3

import android.content.SharedPreferences

class SharedPreferencesUtil {
     companion object{
         fun addList(sharedPreferences: SharedPreferences, list:List<String>, key: String){
             var editor:SharedPreferences.Editor = sharedPreferences.edit()
             editor.putInt(key+"_size", list.size)

             for(i in 0 until list.size){
                 editor.remove(key+i)
                 editor.putString(key+i, list[i])
             }
             editor.apply()
         }

         fun retrieveSharedList(sharedPreferences: SharedPreferences, key: String): ArrayList<String> {
             var result= arrayListOf<String>()
             var size:Int = sharedPreferences.getInt(key + "_size", 0)

             for(i in 0..size){
                 result.add(sharedPreferences.getString(key + i, null).toString())
             }
             return result
         }

         fun clearList(sharedPreferences: SharedPreferences){
             sharedPreferences.edit().clear().apply()
         }

         fun getSize(sharedPreferences: SharedPreferences): Int{
             return sharedPreferences.all.size
         }
     }

}