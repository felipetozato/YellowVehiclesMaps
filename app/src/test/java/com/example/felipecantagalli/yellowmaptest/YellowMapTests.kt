package com.example.felipecantagalli.yellowmaptest

import com.example.felipecantagalli.VehiclesServiceApi
import org.junit.Assert
import org.junit.Test
import java.net.URL

class YellowMapTests {

    @Test
    fun testDataParser_ReturnAllObjects() {
        val service = VehiclesServiceApi()
        val apiResponse = service.getVehicles()
        println(apiResponse)

        Assert.assertEquals(10, apiResponse.vehicles.size)
        Assert.assertEquals(2, apiResponse.imagesInfo.size)
    }

}