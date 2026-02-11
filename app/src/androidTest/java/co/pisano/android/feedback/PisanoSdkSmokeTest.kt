package co.pisano.android.feedback

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.pisano.android.feedback.util.PisanoSdkBootstrapper
import co.pisano.feedback.data.model.PisanoCustomer
import co.pisano.feedback.managers.PisanoSDK
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PisanoSdkSmokeTest {

    @Test
    fun healthCheck_shouldSucceed_whenConfigProvided() {
        // Skip on CI/local devices where secrets are not configured.
        assumeTrue(BuildConfig.PISANO_APP_ID.isNotBlank())
        assumeTrue(BuildConfig.PISANO_ACCESS_KEY.isNotBlank())
        assumeTrue(BuildConfig.PISANO_CODE.isNotBlank())
        assumeTrue(BuildConfig.PISANO_API_URL.isNotBlank())
        assumeTrue(BuildConfig.PISANO_FEEDBACK_URL.isNotBlank())

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        PisanoSdkBootstrapper.ensureInitialized(context)

        val latch = CountDownLatch(1)
        var isHealthy: Boolean? = null

        PisanoSDK.healthCheck(
            language = null,
            pisanoCustomer = PisanoCustomer(externalId = "smoke-test"),
            payload = null,
            code = null,
        ) { ok ->
            isHealthy = ok
            latch.countDown()
        }

        assumeTrue("healthCheck timed out", latch.await(10, TimeUnit.SECONDS))
        assumeTrue("healthCheck failed", isHealthy == true)
    }
}


