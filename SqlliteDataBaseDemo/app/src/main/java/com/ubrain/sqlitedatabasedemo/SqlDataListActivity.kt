package com.ubrain.sqlitedatabasedemo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.ubrain.sqlitedatabasedemo.ModelMaster.DBModel
import kotlinx.android.synthetic.main.activity_sql_data_list.*

@SuppressLint("InflateParams")
class SqlDataListActivity : AppCompatActivity() {

    private var rcySqlAdapter: RcySqlAdapter? = null
    private val dbDataList: MutableList<DBModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sql_data_list)
        setSupportActionBar(toolbar_rcy_sql!!)

        rcySql!!.hasFixedSize()
        rcySql!!.layoutManager = LinearLayoutManager(this)
        dbDataList.clear()
        dbDataList.addAll(DbQueryClass(this).viewAllData())
        println("mnb: ${DbQueryClass(this).viewTotalCount()}")
        rcySqlAdapter = RcySqlAdapter(this, dbDataList)
        rcySql!!.adapter = rcySqlAdapter

        swipe_refresh_layout!!.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        swipe_refresh_layout!!.isRefreshing = true
        dbDataList.clear()
        dbDataList.addAll(DbQueryClass(this).viewAllData())
        rcySqlAdapter!!.notifyDataSetChanged()
        swipe_refresh_layout!!.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.rcy_sql_menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.menu_add) {
            insertData()
            return true
        } else if (item.itemId == R.id.menu_delete) {
            deleteData()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteData() {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_sql, null)
        val builder = AlertDialog.Builder(this)
        val dialog = builder.create()
        dialog.setView(view)
        val etSqlUserId: EditText = view.findViewById(R.id.et_sql_id)
        val etSqlTitle: EditText = view.findViewById(R.id.et_sql_title)
        val etSqlSubTitle: EditText = view.findViewById(R.id.et_sql_subtitle)
        etSqlTitle.visibility = View.GONE
        etSqlSubTitle.visibility = View.GONE
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete") { dialogInterface, _ ->
            if (etSqlUserId.text.trim().isNotEmpty()) {
                val status = DbQueryClass(this).deleteData(DBModel(Integer.parseInt(etSqlUserId.text.toString()),
                        etSqlTitle.text.toString(), etSqlSubTitle.text.toString()))
                if (status > -1) {
                    Toast.makeText(applicationContext, "record delete", Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                    loadData()
                }
            } else {
                Toast.makeText(applicationContext, "enter details", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun insertData() {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_sql, null)
        val builder = AlertDialog.Builder(this)
        val dialog = builder.create()
        dialog.setView(view)
        val etSqlUserId: EditText = view.findViewById(R.id.et_sql_id)
        val etSqlTitle: EditText = view.findViewById(R.id.et_sql_title)
        val etSqlSubTitle: EditText = view.findViewById(R.id.et_sql_subtitle)
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Insert") { dialogInterface, _ ->
            if (etSqlUserId.text.trim().isNotEmpty() && etSqlTitle.text.trim().isNotEmpty() && etSqlSubTitle.text.trim().isNotEmpty()) {
                val status = DbQueryClass(this).insertRow(DBModel(Integer.parseInt(etSqlUserId.text.toString()),
                        etSqlTitle.text.toString(), etSqlSubTitle.text.toString()))
                if (status > -1) {
                    Toast.makeText(applicationContext, "record save", Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                    loadData()
                }
            } else {
                Toast.makeText(applicationContext, "enter details", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    fun editData(dbModel: DBModel) {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_sql, null)
        val builder = AlertDialog.Builder(this)
        val dialog = builder.create()
        dialog.setView(view)
        val etSqlUserId: EditText = view.findViewById(R.id.et_sql_id)
        val etSqlTitle: EditText = view.findViewById(R.id.et_sql_title)
        val etSqlSubTitle: EditText = view.findViewById(R.id.et_sql_subtitle)
        etSqlUserId.text = Editable.Factory.getInstance().newEditable(dbModel.userId.toString())
        etSqlUserId.isFocusable = false
        etSqlUserId.isClickable = false
        etSqlTitle.text = Editable.Factory.getInstance().newEditable(dbModel.title)
        etSqlSubTitle.text = Editable.Factory.getInstance().newEditable(dbModel.subTitle)

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update") { dialogInterface, _ ->
            if (etSqlUserId.text.trim().isNotEmpty() && etSqlTitle.text.trim().isNotEmpty() && etSqlSubTitle.text.trim().isNotEmpty()) {
                val status = DbQueryClass(this).updateData(DBModel(Integer.parseInt(etSqlUserId.text.toString()),
                        etSqlTitle.text.toString(), etSqlSubTitle.text.toString()))
                if (status > -1) {
                    Toast.makeText(applicationContext, "record update", Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                    loadData()
                }
            } else {
                Toast.makeText(applicationContext, "enter details", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    class RcySqlAdapter(mContext1: Context, dbModelList1: List<DBModel>) : RecyclerView.Adapter<RcySqlAdapter.Tempo>() {
        private var mContext: Context? = null
        private var dbModelList: List<DBModel>? = null

        init {
            this.mContext = mContext1
            this.dbModelList = dbModelList1
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Tempo {
            return Tempo(LayoutInflater.from(mContext!!).inflate(R.layout.adapter_rcy_sql, p0, false))
        }

        override fun getItemCount(): Int {
            return dbModelList!!.size
        }

        override fun onBindViewHolder(p0: Tempo, p1: Int) {
            p0.txtAdapterUserId!!.text = dbModelList!![p1].userId.toString()
            p0.txtAdapterTitle!!.text = dbModelList!![p1].title
            p0.txtAdapterSubTitle!!.text = dbModelList!![p1].subTitle

            p0.itemView.setOnClickListener {
                (mContext as SqlDataListActivity).editData(dbModelList!![p1])
            }
        }

        class Tempo(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var txtAdapterUserId: TextView? = null
            var txtAdapterTitle: TextView? = null
            var txtAdapterSubTitle: TextView? = null

            init {
                txtAdapterUserId = itemView.findViewById(R.id.txt_adapter_user_id)
                txtAdapterTitle = itemView.findViewById(R.id.txt_adapter_title)
                txtAdapterSubTitle = itemView.findViewById(R.id.txt_adapter_subtitle)
            }

        }
    }
}
