package com.example.slotmachines

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.MediaPlayer.create
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.slotmachines.Class.GlidePreload
import com.example.slotmachines.ViewModel.ActivityViewModel
import com.example.slotmachines.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var viewModel: ActivityViewModel
    private val arrayPic =
        arrayOf(R.drawable.b, R.drawable.g, R.drawable.r, R.drawable.q, R.drawable.k)
    private var isSpin = true
    private var bet = 1
    private var job: Job? = null
    private var isSpinCurrent = false
    private var countForStop = 0
    private var handler: Handler? = null
    private var runnableAnim: Runnable? = null
    private var mediaSpin: MediaPlayer? = null
    private var mediaStopSpin: MediaPlayer? = null
    private var mediaWin: MediaPlayer? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        viewModel = ViewModelProvider(this)[ActivityViewModel::class.java]
        handler = Handler(Looper.getMainLooper())
        mediaSpin = create(this, R.raw.spin)
        mediaStopSpin = create(this, R.raw.stopspin)
        mediaWin = create(this, R.raw.win)

        val runnablePlus = object : Runnable {
            override fun run() {
                bet += 1
                viewModel.setBet(bet)
                mBinding.betTV.text = "Rate ${viewModel.getBet()}"
                handler?.postDelayed(this, 150)
            }
        }
        val runnableMinus = object : Runnable {
            override fun run() {
                if (viewModel.getBet() > 0)
                    bet -= 1
                else bet = 0
                viewModel.setBet(bet)
                if (viewModel.getBet() > 0) mBinding.betTV.text = "Rate ${viewModel.getBet()}"
                else mBinding.betTV.text = "Rate 0"
                handler?.postDelayed(this, 150)
            }
        }
        runnableAnim = object : Runnable {
            override fun run() {
                mBinding.activityMotion.transitionToEnd()
                handler?.postDelayed(this, 150)
            }
        }

        viewModel.balance.observe(this) {
            mBinding.balanceTV.text = "Balance $it"
        }
        viewModel.bet.observe(this) {
            mBinding.betTV.text = "Rate $it"
        }
        viewModel.pic.observe(this) {
            if (it.pic1 != 0) Glide.with(this).load(it.pic1).into(mBinding.win1)
            if (it.pic2 != 0) Glide.with(this).load(it.pic2).into(mBinding.win2)
            if (it.pic3 != 0) Glide.with(this).load(it.pic3).into(mBinding.win3)
            if (it.pic4 != 0) Glide.with(this).load(it.pic4).into(mBinding.win4)
            if (it.pic5 != 0) Glide.with(this).load(it.pic5).into(mBinding.win5)
            if (it.pic6 != 0) Glide.with(this).load(it.pic6).into(mBinding.win6)
            if (it.pic7 != 0) Glide.with(this).load(it.pic7).into(mBinding.win7)
            if (it.pic8 != 0) Glide.with(this).load(it.pic8).into(mBinding.win8)
            if (it.pic9 != 0) Glide.with(this).load(it.pic9).into(mBinding.win9)
        }
        GlidePreload(this, arrayPic).preload()

        if (viewModel.getAnim()) {
            handler?.postDelayed(runnableAnim!!, 500)
            viewModel.setAnim(false)
        } else mBinding.activityMotion.jumpToState(R.id.end)

        mBinding.spinBut.setOnClickListener {
            if (!isSpinCurrent) spin()
        }
        mBinding.plusBut.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler?.postDelayed(runnablePlus, 500)
                    viewModel.setBet(1)
                    mBinding.betTV.text = "Rate ${viewModel.getBet()}"
                    true
                }
                MotionEvent.ACTION_UP -> {
                    bet = 0
                    handler?.removeCallbacks(runnablePlus)
                    true
                }
                else -> false
            }
        }
        mBinding.minusBut.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler?.postDelayed(runnableMinus, 500)
                    if (viewModel.getBet() > 0)
                        bet -= 1
                    else bet = 0
                    viewModel.setBet(bet)
                    mBinding.betTV.text = "Rate ${viewModel.getBet().toString()}"
                    true
                }
                MotionEvent.ACTION_UP -> {
                    bet = 0
                    handler?.removeCallbacks(runnableMinus)
                    true
                }
                else -> false
            }
        }
    }

    private fun spin() {
        job = CoroutineScope(Dispatchers.Main).launch {

            if (!isSpinCurrent) {
                mediaSpin?.start()
                viewModel.bet()
            }
            isSpinCurrent = true
            mBinding.balanceTV.text = "Balance ${viewModel.getBalance().toString()}"
            spinImg(
                mBinding.win1,
                mBinding.win2,
                mBinding.win3,
                mBinding.win4,
                mBinding.win5,
                mBinding.win6,
                mBinding.win7,
                mBinding.win8,
                mBinding.win9
            ) {
                val arrayWin = viewModel.getArrayIsWin()
                var count = 0
                for (i in 0 until arrayWin.size - 1) {
                    for (j in i + 1 until arrayWin.size) {
                        if (arrayWin[i] == arrayWin[j]) count++
                    }
                }

                when (count) {
                    1 -> {
                        mediaWin?.start()
                        viewModel.updateBalance(viewModel.getBetStart() * 2)
                        mBinding.balanceTV.text = "Win x2 ${viewModel.getBetStart() * 2}"
                    }
                    3 -> {
                        mediaWin?.start()
                        viewModel.updateBalance(viewModel.getBetStart() * 5)
                        mBinding.balanceTV.text = "Win x5 ${viewModel.getBetStart() * 5}"
                    }
                }
            }
            isSpinCurrent = false
            delay(2500)
            mBinding.balanceTV.text = "Balance ${viewModel.getBalance()}"
        }
    }


    suspend fun spinImg(
        win1: ImageView,
        win2: ImageView,
        win3: ImageView,
        win4: ImageView,
        win5: ImageView,
        win6: ImageView,
        win7: ImageView,
        win8: ImageView,
        win9: ImageView,
        onSuccess: () -> Unit
    ) {
        val arrayOne = arrayPic.copyOf()
        val arrayTwo = arrayPic.copyOf()
        val arrayThree = arrayPic.copyOf()
        arrayOne.shuffle()
        arrayTwo.shuffle()
        arrayThree.shuffle()
        var winOneStop = false
        var winTwoStop = false
        var winThreeStop = false
        var countOne = 2
        var countOne2 = 1
        var countOne3 = 0
        var countTwo = 2
        var countTwo2 = 1
        var countTwo3 = 0
        var countTree = 2
        var countTree2 = 1
        var countTree3 = 0
        win1.rotationX = 50f
        win3.rotationX = -50f
        win4.rotationX = 50f
        win6.rotationX = -50f
        win7.rotationX = 50f
        win9.rotationX = -50f


        while (isSpin) {
            if (countForStop <= viewModel.getSlotOneTime()) {
                win3.tag = arrayOne[countOne]
                Glide.with(this).load(arrayOne[countOne++]).into(win3)
                if (countOne > 4) countOne = 0
                win2.tag = arrayOne[countOne2]
                Glide.with(this).load(arrayOne[countOne2++]).into(win2)
                if (countOne2 > 4) countOne2 = 0
                win1.tag = arrayOne[countOne3]
                Glide.with(this).load(arrayOne[countOne3++]).into(win1)
                if (countOne3 > 4) countOne3 = 0
            } else if (!winOneStop) {
                mediaStopSpin?.start()
                winOneStop = true
                if (win3.tag != null && win2.tag != null && win1.tag != null)
                    viewModel.setPic1(win1.tag.toString(), win2.tag.toString(), win3.tag.toString())
                win1.rotationX = 0f
                win3.rotationX = -0f
            }
            if (countForStop <= viewModel.getSlotTwoTime()) {
                if (countTwo > 4) countTwo = 0
                win6.tag = arrayTwo[countTwo]
                Glide.with(this).load(arrayTwo[countTwo++]).into(win6)
                if (countTwo2 > 4) countTwo2 = 0
                win5.tag = arrayTwo[countTwo2]
                Glide.with(this).load(arrayTwo[countTwo2++]).into(win5)
                if (countTwo3 > 4) countTwo3 = 0
                win4.tag = arrayTwo[countTwo3]
                Glide.with(this).load(arrayTwo[countTwo3++]).into(win4)
            } else if (!winTwoStop) {
                mediaStopSpin?.start()
                winTwoStop = true
                if (win6.tag != null && win5.tag != null && win4.tag != null)
                    viewModel.setPic2(win4.tag.toString(), win5.tag.toString(), win6.tag.toString())
                win4.rotationX = 0f
                win6.rotationX = -0f
            }
            if (countForStop <= viewModel.getSlotThreeTime()) {
                if (countTree > 4) countTree = 0
                win9.tag = arrayThree[countTree]
                Glide.with(this).load(arrayThree[countTree++]).into(win9)
                if (countTree2 > 4) countTree2 = 0
                win8.tag = arrayThree[countTree2]
                Glide.with(this).load(arrayThree[countTree2++]).into(win8)
                if (countTree3 > 4) countTree3 = 0
                win7.tag = arrayThree[countTree3]
                Glide.with(this).load(arrayThree[countTree3++]).into(win7)
            } else if (!winThreeStop) {
                winThreeStop = true
                mediaStopSpin?.start()
                if (win9.tag != null && win8.tag != null && win7.tag != null)
                    viewModel.setPic3(win7.tag.toString(), win8.tag.toString(), win9.tag.toString())
                win7.rotationX = 0f
                win9.rotationX = -0f
            }
            delay(50)
            countForStop += 50
            if (countForStop >= 10000) isSpin = false
        }
        countForStop = 0
        isSpin = true
        viewModel.setRandomTime()
        onSuccess()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (job?.isActive == true) job?.cancel()
        outState.putBoolean("isSpinCurrent", isSpinCurrent)
        outState.putInt("countForStop", countForStop)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isSpinCurrent = savedInstanceState.getBoolean("isSpinCurrent")
        countForStop = savedInstanceState.getInt("countForStop")
        if (isSpinCurrent) spin()
    }

    override fun onStop() {
        super.onStop()
        runnableAnim?.let { handler?.removeCallbacks(it) }
        if (job?.isActive == true) job?.cancel()
    }

    override fun onStart() {
        super.onStart()
        if (isSpinCurrent) spin()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSpin?.release()
        mediaWin?.release()
        mediaStopSpin?.release()
    }

}