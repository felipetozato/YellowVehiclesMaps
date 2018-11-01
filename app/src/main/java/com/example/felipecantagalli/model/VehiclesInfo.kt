package com.example.felipecantagalli.model

import android.graphics.Bitmap

data class VehiclesInfo(val vehicles: Collection<Vehicle>,
                        val bikeBitmap: Bitmap,
                        val scooterBitmap: Bitmap
) {}