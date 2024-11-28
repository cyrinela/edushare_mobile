package iset.dsi.myapplication
import ResourceApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://172.20.10.6:8100"
        //"http://172.20.10.6:8100"

    val api: ResourceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ResourceApi::class.java)
    }
}
