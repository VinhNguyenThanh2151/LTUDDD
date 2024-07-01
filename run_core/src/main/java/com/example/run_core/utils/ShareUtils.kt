package com.example.run_core.utils


import android.content.Context
import android.content.SharedPreferences



object ShareUtils {
    private var mPref: SharedPreferences? = null
    const val NAME = "Running";

    fun init(context: Context) {
        mPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun setString(_key: String?, _value: String?) {
        if (_key == null) {
            return
        }
        if (mPref != null) {
            val edit = mPref!!.edit()
            edit.putString(_key, _value)
            edit.commit()
        }
    }

    fun getString(_key: String?): String? {
        if (mPref == null || !mPref!!.contains(_key)) {
            val edit = mPref!!.edit()
            edit.putString(_key, null)
            edit.commit()
        }
        return mPref!!.getString(_key, null)
    }

    fun getString(_key: String?, _value: String?): String? {
        if (mPref == null || !mPref!!.contains(_key)) {
            val edit = mPref!!.edit()
            edit.putString(_key, _value)
            edit.commit()
        }
        return mPref!!.getString(_key, _value)
    }

}