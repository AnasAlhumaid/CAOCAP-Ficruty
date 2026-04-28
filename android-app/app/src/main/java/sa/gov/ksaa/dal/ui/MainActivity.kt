package sa.gov.ksaa.dal.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.ui.viewModels.MainActivityVM
import java.net.SocketTimeoutException
import java.net.UnknownHostException

import android.Manifest
import android.graphics.Color
import android.widget.Toast
import androidx.core.app.ActivityCompat


//import sa.gov.ksaa.dal.activities.databinding.ActivityMain2Binding

class MainActivity : AppCompatActivity() {

    val vm: MainActivityVM by viewModels()

    lateinit var toolbar: Toolbar
    lateinit var appBarLayout: AppBarLayout
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    lateinit var navHostFragment : NavHostFragment
    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var appBarConfiguration :AppBarConfiguration
    lateinit var progress_bar: ImageView
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var bottomSheetModal: FrameLayout
    lateinit var snackbar: Snackbar
//    lateinit var progressIndicatorIV: ImageView


     fun replace(fragment: Fragment , layout: Int) {
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(layout,fragment)
        fragmentTransaction.commit()
    }
     fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL;
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appBarLayout = findViewById(R.id.appBarLayout)
        toolbar = findViewById(R.id.toolbar)
        checkPermission()
//        requestPermission()

//        toolbar.setNavigationIcon(R.drawable.twotone_keyboard_arrow_left_24)
        setSupportActionBar(toolbar)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.twotone_keyboard_arrow_left_24)
//        collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar_layout)

//        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main2) as NavHostFragment
//        navController = navHostFragment.navController

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main2) as NavHostFragment
//        val navController = navHostFragment.navController

//        navController = findNavController(R.id.nav_host_fragment_activity_main2)
        navController = navHostFragment.navController
//        appBarConfiguration = AppBarConfiguration(setOf(R.id.main, R.id.profile))
//        appBarConfiguration = AppBarConfiguration(
//            topLevelDestinationIds = setOf(),
//            fallbackOnNavigateUpListener = ::onSupportNavigateUp
//        )
//        drawerLayout = findViewById(R.id.drawer_layout)
//        navigationView  = findViewById(R.id.nav_view)
//        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.notifcation, R.id.contactUsFragment))
        toolbar.setupWithNavController(navController, appBarConfiguration)

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment, R.id.exploreFragment,
            R.id.notifcation, R.id.contactUsFragment))

        // Add menu items without overriding methods in the Activity
//        addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                // Add menu items here
//                menuInflater.inflate(R.menu.example_menu, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                // Handle the menu selection
//                return true
//            }
//        })

//        collapsingToolbarLayout.setupWithNavController(toolbar, navController, appBarConfiguration)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navigationView.setupWithNavController(navController)



        progress_bar = findViewById(R.id.progress_bar)


//        val wv = findViewById<View>(R.id.loaderWV) as WebView
//        wv.loadUrl("file:///android_asset/loader.gif")
//        progressIndicatorIV = findViewById(R.id.progressIndicatorIV)
//        try {
//            val inputStream = resources.openRawResource(R.raw.loader) // Replace with your GIF resource
//            val source = ImageDecoder.createSource(inputStream)
//            val drawable = ImageDecoder.decodeDrawable(source)
//            if (drawable is AnimatedImageDrawable) {
//                drawable.start()
//            }
//            progress_bar.setImageDrawable(drawable)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }


        Glide.with(this)
            .asGif()
            .load(R.raw.loader)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            .into(progress_bar)

//        startActivity(Intent(this, SlidingScreen::class.java))
//        binding = ActivityMain2Binding.inflate(layoutInflater)
//        setContentView(binding.root)
//
        bottomNavigationView = findViewById(R.id.nav_view)
        bottomSheetModal = findViewById(R.id.bottom_sheet_modal)




        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)


        snackbar = Snackbar.make(progress_bar, "", Snackbar.LENGTH_SHORT)
            .setAnchorView(progress_bar)

        vm.exceptionMutableLiveData.observe(this){
            Log.w(javaClass.simpleName, "onCreate: vm.exceptionMutableLiveData.observe : it = $it",it )
            it.printStackTrace()
            it?.let {
                val message = when(it){
                    is SocketTimeoutException -> "الخادم لا يستجيب، تأكد من الاتصال بالإنترنت و حاول مرة أخرى."
                    is UnknownHostException -> "لا يمكن الاتصال بالخادم، تأكد من الاتصال بالإنترنت و حاول مرة أخرى."
                    else -> null
                }
                message?.let {
                    Snackbar.make(progress_bar, it, Snackbar.LENGTH_LONG)
                        .setAnchorView(progress_bar)
                        .show()
                }
            }
            progress_bar.visibility = View.INVISIBLE
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
//    }


//    fun openFile(pickerInitialUri: Uri) {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "application/pdf"
//
//            // Optionally, specify a URI for the file that should appear in the
//            // system file picker when it loads.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
//        }
//
////        startActivityForResult(intent, PICK_PDF_FILE)
//
//        val startForResult = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                //  you will get result here in result.data
//            }
//        }
//
//        startForResult.launch(Intent(activity, CameraCaptureActivity::class.java))
//    }


    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
        private const val REQUEST_CODE_PICK_IMAGE = 102
    }

    @SuppressLint("HardwareIds")
    fun openChat(){
        val deviceId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
//        UserDetailActivity.open(
//            currActivity = this,
//            appSid = "Your AppSID",
//            locale = "en",
//            deviceID = deviceId,
//            domain = "Your Domain URL"
//        )
    }
}