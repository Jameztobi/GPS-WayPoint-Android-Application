package com.example.assignment_3

import android.os.Message

class UpdateThread(handler: ThreadHandler) : Thread() {
    // private fields of the class
    private var _cycles: Int = 0
    private var _handler: ThreadHandler = handler

    override fun run() {
        val m: Message = _handler.obtainMessage(0xDEADBEEF.toInt(), _cycles, 0)
        _handler.sendMessage(m)
        _handler.callMethod()
        _handler.postDelayed(this, 5000)
    }
}