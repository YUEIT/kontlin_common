package cn.yue.test

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


/**
 * Description :
 * Created by yue on 2022/11/16
 */

class ComposeActivity : ComponentActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            contentView()
        }
    }

    @Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun contentView() {
        MaterialTheme {
            Greeting(name = "compose")
        }
    }

    @Composable
    fun Greeting(name: String) {
        Column {
            Text(text = "Hello $name!")
            Text(text = "Hello $name!")
            Text(text = "Hello $name!")
            Text(text = "Hello $name!")
        }
    }

}