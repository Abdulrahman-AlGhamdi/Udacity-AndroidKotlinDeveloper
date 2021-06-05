package com.ss.shoestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ss.shoestore.models.ShoeModel

class ShoesViewModel : ViewModel() {

    private var _shoesList = MutableLiveData<MutableList<ShoeModel>>()
    val shoesList: LiveData<MutableList<ShoeModel>> get() = _shoesList

    private var _shoesState = MutableLiveData<Boolean>()
    val shoesState: LiveData<Boolean> get() = _shoesState

    init {
        _shoesList.value = mutableListOf()
        _shoesState.value = false
    }

    fun addNewShow(name: String, size: String, company: String, description: String) {
        val shoe = ShoeModel(name, size, company, description)
        _shoesList.value?.add(shoe)
        _shoesState.value = true
    }

    fun onEventComplete() {
        _shoesState.value = false
    }
}