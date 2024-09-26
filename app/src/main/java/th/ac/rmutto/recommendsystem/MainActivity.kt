package th.ac.rmutto.recommendsystem

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    private var data = ArrayList<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPrefer = this.getSharedPreferences(
            "appPrefer", Context.MODE_PRIVATE)
        val custID = 1 //sharedPrefer?.getString("custIDPref", null)?.toInt()

        //For an synchronous task
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        //List data
        recyclerView = findViewById(R.id.recyclerView)
        showDataList(custID)
    }


    //show a data list
    private fun showDataList(custID: Int) {
        val url: String = getString(R.string.root_url) + getString(R.string.product_url) + custID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).get().build()
        val response = okHttpClient.newCall(request).execute()

        if (response.isSuccessful) {
            val res = JSONArray(response.body!!.string())
            if (res.length() > 0) {
                for (i in 0 until res.length()) {
                    val item: JSONObject = res.getJSONObject(i)
                    data.add(
                        Data(
                            item.getString("productID"),
                            item.getString("productName"),
                            item.getString("price"),
                            item.getString("quantity"),
                            item.getString("imageFile")
                        )
                    )
                }

                recyclerView!!.adapter = DataAdapter(data)
            } else {
                Toast.makeText(this, "ไม่สามารถแสดงข้อมูลได้", Toast.LENGTH_LONG).show()
            }
        }
    }


    internal class Data(
        var productID: String, var productName: String, var price: String,
        var quantity: String, var imageViewFile: String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_product,
                parent, false
            )
            return ViewHolder(view)
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var imageViewFile: ImageView = itemView.findViewById(R.id.imageViewFile)
            var productName: TextView = itemView.findViewById(R.id.textViewProductName)
            var price: TextView = itemView.findViewById(R.id.textViewPrice)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            val url = getString(R.string.root_url) +
                    getString(R.string.product_image_url) + data.imageViewFile

            Picasso.get().load(url).into(holder.imageViewFile)
            holder.productName.text = data.productName
            holder.price.text = "฿" + data.price

//            holder.imageViewFile.setOnClickListener {
//                val intent = Intent(context, ProductActivity::class.java)
//                intent.putExtra("productID", data.productID)
//                startActivity(intent)
//            }

        }
    }

}