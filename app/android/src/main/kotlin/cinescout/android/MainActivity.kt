package cinescout.android

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import cinescout.auth.tmdb.domain.usecase.NotifyTmdbAppAuthorized
import cinescout.auth.trakt.domain.model.TraktAuthorizationCode
import cinescout.auth.trakt.domain.usecase.NotifyTraktAppAuthorized
import cinescout.design.theme.CineScoutTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val notifyTmdbAppAuthorized: NotifyTmdbAppAuthorized by inject()
    private val notifyTraktAppAuthorized: NotifyTraktAppAuthorized by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestSendNotificationsPermission()
        setContent {
            CineScoutTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    App(onFinish = this::finish)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val dataString = intent?.dataString
        if (dataString != null) {
            if (dataString == "cinescout://tmdb") {
                lifecycleScope.launchWhenCreated {
                    notifyTmdbAppAuthorized()
                }
            }
            if ("cinescout://trakt" in dataString) {
                lifecycleScope.launchWhenCreated {
                    val code = TraktAuthorizationCode(dataString.substringAfter("code="))
                    notifyTraktAppAuthorized(code)
                }
            }
        }
    }

    private fun requestSendNotificationsPermission() {
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
    }
}
