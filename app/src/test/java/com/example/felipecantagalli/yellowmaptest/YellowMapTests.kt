package com.example.felipecantagalli.yellowmaptest

import com.example.felipecantagalli.VehiclesServiceApi
import com.example.felipecantagalli.model.ApiResponse
import io.reactivex.observers.TestObserver
import org.junit.Assert
import org.junit.Test

class YellowMapTests {

    @Test
    fun testDataParser_ReturnAllObjects() {
        val observer = TestObserver<ApiResponse>()

        val service = VehiclesServiceApi()
        service.getVehicles().subscribe(observer)

        observer.assertComplete()
        observer.assertNoErrors()
        observer.assertValueCount(1)
        val apiResponse = observer.values()[0]

        Assert.assertEquals(10, apiResponse.vehicles.size)
        Assert.assertEquals(2, apiResponse.imagesInfo.size)
    }

}