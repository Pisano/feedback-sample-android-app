package co.pisano.android.feedback

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import co.pisano.android.feedback.util.AppConfig
import co.pisano.android.feedback.util.AppLogger
import co.pisano.android.feedback.util.AppUtil
import co.pisano.android.feedback.util.FeedbackUtil
import co.pisano.android.feedback.util.PisanoSdkBootstrapper
import co.pisano.android.feedback.util.PisanoSdkState
import co.pisano.android.feedback.util.SdkError
import co.pisano.feedback.data.helper.ViewMode
import co.pisano.feedback.data.model.PisanoCustomer
import co.pisano.feedback.data.model.Title
import co.pisano.feedback.managers.PisanoSDK

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private var bgIndex = 0
    private var bgColors: IntArray = intArrayOf()

    private var selectedTitleColor: Int = android.graphics.Color.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseDeepLink()

        setContentView(R.layout.activity_main)

        bgColors = AppUtil.backgroundColors(this)
        startBackgroundAnimation()

        val welcomeContainer = findViewById<View>(R.id.welcomeContainer)
        val formScroll = findViewById<View>(R.id.formScroll)

        val btnGettingStarted = findViewById<MaterialButton>(R.id.btnGettingStarted)
        val btnBack = findViewById<MaterialButton>(R.id.btnBack)
        val btnGetFeedback = findViewById<MaterialButton>(R.id.btnGetFeedback)
        val btnHealthCheck = findViewById<MaterialButton>(R.id.btnHealthCheck)
        val btnClear = findViewById<MaterialButton>(R.id.btnClear)

        val txtConfigMissing = findViewById<TextView>(R.id.txtConfigMissing)
        val txtActionStatus = findViewById<TextView>(R.id.txtActionStatus)
        val txtPreflight = findViewById<TextView>(R.id.txtPreflight)

        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        val etExternalId = findViewById<TextInputEditText>(R.id.etExternalId)
        val etCustomTitle = findViewById<TextInputEditText>(R.id.etCustomTitle)

        val rgViewMode = findViewById<RadioGroup>(R.id.rgViewMode)
        val rgFontSize = findViewById<RadioGroup>(R.id.rgFontSize)
        val rgFontStyle = findViewById<RadioGroup>(R.id.rgFontStyle)

        val colorRow = findViewById<LinearLayout>(R.id.colorRow)
        setupColorRow(colorRow)

        // SDK init already runs in Application.onCreate; ensure here as well (idempotent).
        PisanoSdkBootstrapper.ensureInitialized(this)

        val config = AppConfig.fromBuildConfig()
        val configMissing = !config.isValid
        txtConfigMissing.isVisible = configMissing
        btnGetFeedback.isEnabled = !configMissing

        if (BuildConfig.DEBUG) {
            txtActionStatus.isVisible = true
            txtPreflight.isVisible = true
        }

        PisanoSdkState.lastAction.observe(this) { action ->
            if (BuildConfig.DEBUG) {
                txtActionStatus.text = getString(R.string.action_status, action?.name ?: "")
            }
        }
        PisanoSdkState.lastError.observe(this) { err ->
            if (err == SdkError.ConfigMissing) {
                txtConfigMissing.isVisible = true
                btnGetFeedback.isEnabled = false
            }
        }

        fun showWelcome() {
            welcomeContainer.isVisible = true
            formScroll.isVisible = false
        }

        fun showForm() {
            welcomeContainer.isVisible = false
            formScroll.isVisible = true
        }

        showWelcome()

        btnGettingStarted.setOnClickListener { showForm() }
        btnBack.setOnClickListener { showWelcome() }

        btnClear.setOnClickListener {
            txtPreflight.text = ""
            PisanoSdkState.setLastAction(null)
            PisanoSDK.clearAction()
        }

        btnHealthCheck.setOnClickListener {
            val codeOverride = FeedbackUtil.code
            val customer = PisanoCustomer(
                externalId = etExternalId.text?.toString().orEmpty().trim()
            )
            txtPreflight.text = getString(R.string.healthcheck_running)
            txtPreflight.isVisible = true
            AppLogger.i("healthCheck start (code=${codeOverride ?: "<init>"})")

            PisanoSDK.healthCheck(
                language = null,
                pisanoCustomer = customer,
                payload = null,
                code = codeOverride
            ) { ok ->
                runOnUiThread {
                    if (ok) {
                        txtPreflight.text = getString(R.string.healthcheck_ok)
                        AppLogger.i("healthCheck OK")
                    } else {
                        txtPreflight.text = getString(R.string.healthcheck_failed)
                        AppLogger.w("healthCheck failed")
                    }
                }
            }
        }

        btnGetFeedback.setOnClickListener {
            tilEmail.error = null

            val email = etEmail.text?.toString().orEmpty().trim()
            if (email.isNotEmpty() && !isValidEmailAddress(email)) {
                tilEmail.error = getString(R.string.email_address_is_not_valid)
                return@setOnClickListener
            }

            val codeOverride = FeedbackUtil.code
            val customer = PisanoCustomer(
                name = etName.text?.toString().orEmpty().trim(),
                email = email,
                phoneNumber = etPhone.text?.toString().orEmpty().trim(),
                externalId = etExternalId.text?.toString().orEmpty().trim(),
            )

            val viewMode = when (rgViewMode.checkedRadioButtonId) {
                R.id.rbViewModeBottomSheet -> ViewMode.BOTTOM_SHEET
                else -> ViewMode.DEFAULT
            }

            val titleText = etCustomTitle.text?.toString().orEmpty().trim()
            val title: Title? = titleText.takeIf { it.isNotBlank() }?.let {
                val size = when (rgFontSize.checkedRadioButtonId) {
                    R.id.rbFontLarge -> 24f
                    R.id.rbFontBody -> 16f
                    else -> 20f
                }
                val style = when (rgFontStyle.checkedRadioButtonId) {
                    R.id.rbRegular -> Typeface.NORMAL
                    else -> Typeface.BOLD
                }
                Title(
                    text = it,
                    textSize = size,
                    textColor = selectedTitleColor,
                    textStyle = style,
                    backgroundColor = android.graphics.Color.WHITE
                )
            }

            AppLogger.i("show requested (mode=$viewMode, code=${codeOverride ?: "<init>"})")

            PisanoSDK.show(
                viewMode = viewMode,
                title = title,
                language = null,
                payload = null,
                pisanoCustomer = customer,
                code = codeOverride
            )
        }
    }

    private fun parseDeepLink() {
        intent.data?.let { data ->
            if (data.host == "show") {
                FeedbackUtil.code = data.getQueryParameter("code")
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            setIntent(intent)
            parseDeepLink()
        }
    }

    private fun startBackgroundAnimation() {
        val root = findViewById<View>(R.id.backgroundRoot)
        if (bgColors.isEmpty()) return

        val tick = object : Runnable {
            override fun run() {
                root.setBackgroundColor(bgColors[bgIndex])
                bgIndex = AppUtil.changeBackgroundColor(bgIndex)
                handler.postDelayed(this, 1000L)
            }
        }
        handler.post(tick)
    }

    private fun setupColorRow(container: LinearLayout) {
        val colors = intArrayOf(
            ContextCompat.getColor(this, R.color.colorPrimary),
            android.graphics.Color.parseColor("#2ECC71"),
            android.graphics.Color.parseColor("#F1C40F"),
            android.graphics.Color.parseColor("#2C3E50"),
            android.graphics.Color.parseColor("#7F8C8D"),
            android.graphics.Color.BLACK,
            android.graphics.Color.GRAY,
            android.graphics.Color.parseColor("#E67E22"),
            android.graphics.Color.parseColor("#FF5DA2"),
            android.graphics.Color.parseColor("#9B59B6"),
            android.graphics.Color.parseColor("#E74C3C"),
        )
        selectedTitleColor = colors.first()

        container.removeAllViews()
        colors.forEach { c ->
            val v = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    resources.displayMetrics.density.times(40).toInt(),
                    resources.displayMetrics.density.times(50).toInt()
                ).apply {
                    marginEnd = resources.displayMetrics.density.times(10).toInt()
                }
                setBackgroundColor(c)
                alpha = if (c == selectedTitleColor) 0.2f else 1f
                setOnClickListener {
                    selectedTitleColor = c
                    // update alphas
                    for (i in 0 until container.childCount) {
                        val child = container.getChildAt(i)
                        val childColor = (child.background as? android.graphics.drawable.ColorDrawable)?.color
                        child.alpha = if (childColor == selectedTitleColor) 0.2f else 1f
                    }
                }
            }
            container.addView(v)
        }
    }
}

private fun isValidEmailAddress(emailAddress: String) =
    emailAddress.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()