package com.example.foodtruck.shared

import androidx.lifecycle.ViewModel
import com.example.foodtruck.profile.UserMetaData


class SharedViewModel : ViewModel() {
    var userMetaData: UserMetaData = UserMetaData(fullName = "",
        email = "", profilePhoto = "")
}