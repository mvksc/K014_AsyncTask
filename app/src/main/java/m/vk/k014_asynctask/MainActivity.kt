package m.vk.k014_asynctask

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var someTask: SomeTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onLoad()

        tvLoading.setOnClickListener {
            onLoad()
        }

    }

    private fun onLoad() {
        //AsyncTask ไม่เหมาะกับงานที่ทำนานเกินกว่า 5 วินาที
        someTask = SomeTask()
        someTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun onStreamToString(inputStream: InputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var result = ""
        try {
            do {
                line = bufferedReader.readLine()
                if (line != null) {
                    result += line
                }
            } while (line != null)
            inputStream.close()
        } catch (e: Exception) {
        }
        return result
    }

    inner class SomeTask : AsyncTask<Void, Float, String>() {
        private val url = URL("http://www.aemcup.com/sc/service/member.php")
        var result: String = ""
        override fun onPreExecute() {
            super.onPreExecute()
            pgLoading.visibility = View.VISIBLE
            tvLoading.text = "กำลังโหลด..."
        }

        override fun doInBackground(vararg p0: Void?): String {
            try {
                val connect = url.openConnection() as HttpURLConnection
                connect.readTimeout = 8000
                connect.connectTimeout = 8000
                connect.requestMethod = "GET"
                connect.connect()

                val responseCode: Int = connect.responseCode
                if (responseCode == 200) {
                    result = onStreamToString(connect.inputStream)
                }
            } catch (e: Exception) {
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            pgLoading.visibility = View.GONE
            tvLoading.text = result
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        someTask.cancel(true)
    }
}
