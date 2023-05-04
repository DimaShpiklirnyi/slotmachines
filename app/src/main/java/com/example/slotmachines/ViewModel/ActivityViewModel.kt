package com.example.slotmachines.ViewModel




import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.slotmachines.Models.PictureModel

class ActivityViewModel : ViewModel() {
    var startAnim = MutableLiveData(true)
    var balance = MutableLiveData(10000)
    var bet = MutableLiveData(100)
    var betStart = MutableLiveData(0)
    private val slotOneTime = MutableLiveData(7000)
    private val slotTwoTime = MutableLiveData(8000)
    private val slotThreeTime = MutableLiveData(9500)
    var pic = MutableLiveData<PictureModel>()

    init {
        pic.value = PictureModel()
    }

    fun setAnim(bol : Boolean){
        startAnim.value = bol
    }
    fun getAnim():Boolean{
        return startAnim.value!!
    }

    fun setBet(bet:Int){
        if (this.bet.value!! < 0){
            this.bet.value = 0
        }
        else
        this.bet.value = this.bet.value?.plus(bet)
    }
    fun setRandomTime(){
        val array = arrayOf(7000, 8000, 9500)
        array.shuffle()
        slotOneTime.value = array[0]
        slotTwoTime.value = array[1]
        slotThreeTime.value = array[2]

    }
    fun getSlotOneTime():Int{
        return slotOneTime.value!!
    }
    fun getSlotTwoTime():Int{
        return slotTwoTime.value!!
    }
    fun getSlotThreeTime():Int{
        return slotThreeTime.value!!
    }

    fun getBet():Int{
        return this.bet.value!!
    }

    fun bet(){
        balance.value = balance.value?.minus(bet.value!!)
        betStart.value = bet.value
    }

    fun getBetStart():Int{
        return betStart.value!!
    }

    fun getBalance():Int{
        return this.balance.value!!
    }
    fun updateBalance(win : Int){
        balance.value = balance.value?.plus(win)
    }

    fun getArrayIsWin(): Array<Int?> {
        val array = arrayOf(pic.value?.pic2, pic.value?.pic5, pic.value?.pic8)
        return array
    }

    fun setPic1(pic : String, pic2 : String, pic3:String){
        this.pic.value?.pic1 = pic.toInt()
        this.pic.value?.pic2 = pic2.toInt()
        this.pic.value?.pic3 = pic3.toInt()
    }
    fun setPic2(pic : String, pic2 : String, pic3:String){
        this.pic.value?.pic4 = pic.toInt()
        this.pic.value?.pic5 = pic2.toInt()
        this.pic.value?.pic6 = pic3.toInt()
    }
    fun setPic3(pic : String, pic2 : String, pic3:String){
        this.pic.value?.pic7 = pic.toInt()
        this.pic.value?.pic8 = pic2.toInt()
        this.pic.value?.pic9 = pic3.toInt()
    }

}