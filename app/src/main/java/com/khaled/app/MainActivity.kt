package com.khaled.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.khaled.app.databinding.ActivityMainBinding
import com.khaled.creditcard.CreditCard
import com.khaled.creditcard.CreditCardFragment
import com.khaled.creditcard.OnCreditCardSubmitListener
import com.khaled.creditcard.OnCardNumberFilledListener

class MainActivity : AppCompatActivity(), OnCreditCardSubmitListener, OnCardNumberFilledListener {
    override fun onCardNumberFilled(cardNumber: String?) {
        Log.d(MainActivity::class.java.simpleName, cardNumber)
    }

    private lateinit var binding: ActivityMainBinding
    private val creditCardFragment: CreditCardFragment = CreditCardFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        creditCardFragment.registerOnCreditCardSubmitListener(this)
        creditCardFragment.registerOnCardNumberFilledListener(this)
        addFragment()

        binding.get.setOnClickListener {
            Log.d(MainActivity::class.java.simpleName, creditCardFragment.creditCard.toString())
        }

        binding.reset.setOnClickListener {
            creditCardFragment.reset()
        }
    }

    override fun onSubmit(creditCard: CreditCard?) {
        Log.d(MainActivity::class.java.simpleName, creditCardFragment.creditCard.toString())
    }

    fun addFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, creditCardFragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        creditCardFragment.unRegisterOnCreditCardSubmitListener(this)
        creditCardFragment.unRegisterOnCardNumberFilledListener(this)
    }
}
