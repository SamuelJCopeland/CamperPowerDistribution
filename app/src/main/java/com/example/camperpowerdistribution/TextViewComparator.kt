package com.example.camperpowerdistribution

import android.widget.TextView

internal class TextViewComparator<T : TextView> : Comparator<T> {
    override fun compare(x: T, y: T): Int {
        return alphabetLessThan(x, y)
    }

    fun alphabetLessThan(x: TextView, y: TextView): Int{
        var i = 0
        var tempX = (x.text as String).toLowerCase()
        var tempY = (y.text as String).toLowerCase()
        while(i < tempX.length && i < tempY.length){
            if(tempX[i] < tempY[i]){
                return -1
            }
            else if(tempX[i] > tempY[i]){
                return 1
            }
            i++
        }
        if(tempX.length < tempY.length){
            return -1
        }
        else if(tempX.length > tempY.length){
            return 1
        }
        else{
            return 0
        }
    }
}

