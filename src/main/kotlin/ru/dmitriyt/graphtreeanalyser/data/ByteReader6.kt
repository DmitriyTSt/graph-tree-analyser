package ru.dmitriyt.graphtreeanalyser.data

internal class ByteReader6(s6: String) {
    private val mBytes: ByteArray
    private val mSize: Int
    private var mPos: Int
    private var mBit = 0

    init {
        mBytes = s6.toByteArray()
        mSize = s6.length
        mPos = mBit
    }

    // ! whether k bits are available
    fun haveBits(k: Int): Boolean {
        return mPos + (mBit + k - 1) / 6 < mSize
    }

    // ! return the next integer encoded in graph6
    fun getNumber(): Int {
        assert(mPos < mSize)
        var c = mBytes[mPos]
        assert(c >= 63)
        c = (c - 63).toByte()
        ++mPos
        if (c < 126) return c.toInt()
        assert(false)
        return 0
    }

    // ! return the next bit encoded in graph6
    fun getBit(): Int {
        assert(mPos < mSize)
        var c = mBytes[mPos]
        assert(c >= 63)
        c = (c - 63).toByte()
        c = (c.toInt() shr 5 - mBit).toByte()
        mBit++
        if (mBit == 6) {
            mPos++
            mBit = 0
        }
        return c.toInt() and 0x01
    }

    // ! return the next bits as an integer
    fun getBits(k: Int): Int {
        var v = 0
        for (i in 0 until k) {
            v *= 2
            v += getBit()
        }
        return v
    }
}
