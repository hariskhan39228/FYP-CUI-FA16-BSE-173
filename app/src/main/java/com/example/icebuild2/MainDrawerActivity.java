package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainDrawerActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private String currentUserEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        mToolbar=(Toolbar)findViewById(R.id.toolbar_nav);
        setSupportActionBar(mToolbar);
        String boardName=getIntent().getStringExtra("BoardName");
        getSupportActionBar().setTitle(boardName);

        mAuth=FirebaseAuth.getInstance();
        currentUserEmail=mAuth.getCurrentUser().getEmail();

        Toast.makeText(this, boardName, Toast.LENGTH_SHORT).show();

        drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);
        if(currentUserEmail.contains("-")){
            navigationView.inflateMenu(R.menu.drawer_menu_students);
        }else{
            navigationView.inflateMenu(R.menu.drawer_menu);
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.nav_documents:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new DocumentsFragment()).commit();
                        break;
                    case R.id.nav_requests:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new RequestsFragment()).commit();
                        break;
                    case R.id.nav_members:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new MembersFragment()).commit();
                        break;
                    case R.id.nav_quizzes:
                        if(currentUserEmail.contains("-")){
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container,
                                            new AttemptQuizFragment()).commit();
                        }else{
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container,
                                            new SetQuizFragment()).commit();
                        }
                        break;
                }
                return false;
            }
        });

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,mToolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DocumentsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_documents);
        }
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}
