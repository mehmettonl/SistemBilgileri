    package com.example.cihazbilgisi

    import android.annotation.SuppressLint
    import android.app.ActivityManager
    import android.os.Bundle
    import android.os.Environment
    import android.os.StatFs
    import android.view.animation.AlphaAnimation
    import android.view.animation.Animation
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.content.ContextCompat
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import com.example.cihazbilgisi.databinding.ActivityMainBinding
    import com.google.android.material.snackbar.Snackbar
    import java.io.BufferedReader
    import java.io.FileReader
    import java.io.IOException

    class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding
        private var isRamVisible = false
        private var isStorageVisible = false
        private var isCpuVisible = false
        private var isRamAnimating = false
        private var isStorageAnimating = false
        private var isCpuAnimating = false

        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
            enableEdgeToEdge()
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            val snackbar = Snackbar.make(findViewById(android.R.id.content), "Hoşgeldiniz!", Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(ContextCompat.getColor(this@MainActivity, R.color.green))
            snackbar.show()

            val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)

            val totalRam = memoryInfo.totalMem / (1024 * 1024) // MB
            val availableMemory = memoryInfo.availMem / (1024 * 1024) // MB

            val ramAnimation = AlphaAnimation(1f, 0f).apply {
                duration = 400
                repeatMode = Animation.REVERSE
                repeatCount = Animation.INFINITE
            }

            binding.imageViewRam.setOnClickListener {
                if (isRamVisible) {
                    binding.ramText.text = "Toplam RAM:"
                    binding.kullanilabilirRamText.text = "Kullanılabilir RAM:"
                    isRamVisible = false
                } else {
                    binding.ramText.text = "Toplam RAM: ${totalRam} MB"
                    binding.kullanilabilirRamText.text = "Kullanılabilir RAM: ${availableMemory} MB"
                    isRamVisible = true
                }

                if (isRamAnimating) {
                    binding.imageViewRam.clearAnimation()
                    isRamAnimating = false
                } else {
                    binding.imageViewRam.startAnimation(ramAnimation)
                    isRamAnimating = true
                }
            }

            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalStorage = (totalBlocks * blockSize) / (1024 * 1024)
            val availableStorage = (availableBlocks * blockSize) / (1024 * 1024)

            val storageAnimation = AlphaAnimation(1f, 0f).apply {
                duration = 400
                repeatMode = Animation.REVERSE
                repeatCount = Animation.INFINITE
            }

            binding.imageViewStorage.setOnClickListener {
                if (isStorageVisible) {
                    binding.depolamaText.text = "Depolama Bilgisi:"
                    binding.kullanilabilirDepolamaText.text = "Kullanılabilir Depolama:"
                    isStorageVisible = false
                } else {
                    binding.depolamaText.text = "Depolama Bilgisi: ${totalStorage} MB"
                    binding.kullanilabilirDepolamaText.text = "Kullanılabilir Depolama: ${availableStorage} MB"
                    isStorageVisible = true
                }

                if (isStorageAnimating) {
                    binding.imageViewStorage.clearAnimation()
                    isStorageAnimating = false
                } else {
                    binding.imageViewStorage.startAnimation(storageAnimation)
                    isStorageAnimating = true
                }
            }

            val cpuInfo = getCpuInfo()
            val numberOfCores = Runtime.getRuntime().availableProcessors()

            val cpuAnimation = AlphaAnimation(1f, 0f).apply {
                duration = 400
                repeatMode = Animation.REVERSE
                repeatCount = Animation.INFINITE
            }

            binding.imageViewCPU.setOnClickListener {
                if (isCpuVisible) {
                    binding.cpuText.text = "CPU Bilgisi:"
                    isCpuVisible = false
                } else {
                    binding.cpuText.text = "CPU Bilgisi: ${cpuInfo}"
                    binding.cpuText.text = "CPU Bilgisi: ${numberOfCores} Çekirdekli"
                    isCpuVisible = true
                }

                if (isCpuAnimating) {
                    binding.imageViewCPU.clearAnimation()
                    isCpuAnimating = false
                } else {
                    binding.imageViewCPU.startAnimation(cpuAnimation)
                    isCpuAnimating = true
                }
            }
        }

        private fun getCpuInfo(): String {
            return try {
                val cpuInfo = StringBuilder()
                val bufferedReader = BufferedReader(FileReader("/proc/cpuinfo"))
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    cpuInfo.append(line)
                    cpuInfo.append("\n")
                }
                bufferedReader.close()

                // CPU modelini alalım
                val modelRegex = Regex("model name\\s+:\\s+(.+)")
                val modelName = modelRegex.find(cpuInfo)?.groupValues?.get(1) ?: "Bilinmiyor"
                "Model: $modelName"
            } catch (e: IOException) {
                e.printStackTrace()
                "Bilinmiyor"
            }
        }
    }