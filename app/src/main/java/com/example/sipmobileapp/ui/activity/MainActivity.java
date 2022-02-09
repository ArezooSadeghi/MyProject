package com.example.sipmobileapp.ui.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.ActivityMainBinding;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

       /* if (SipMobileAppPreferences.getUserLoginKey(this) != null) {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavInflater navInflater = navHostFragment.getNavController().getNavInflater();
            NavGraph navGraph = navInflater.inflate(R.navigation.nav_graph);
            navGraph.setStartDestination(R.id.patientFragment);
            navHostFragment.getNavController().setGraph(navGraph);
        }*/
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

    }
}