package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gossip.Fragments.ChatsFragment;
import com.example.gossip.Fragments.ProfileFragment;
import com.example.gossip.Fragments.UsersFragment;
import com.example.gossip.Model.Chat;
import com.example.gossip.Model.User;
import com.example.gossip.Notifications.Data;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//to set the tool bar in the activity
        getSupportActionBar().setTitle("");


        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final TabLayout tabLayout=findViewById(R.id.tab_layout);
        final ViewPager viewPager=findViewById(R.id.view_pager);//used to swipe and go to new tab
        //for implementation class ViewPagerAdapter is created


        //TODO unread messages
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //getSupportFrgamentManager() returns the FragmentManager for interacting with the activities associated with it.
                //FragmentManager manages Fragments. It handles transaction between Fragments. Transaction is like add replace remove fragments
                ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
                //ViewPagerAdapter class we made below which extends FrgamentPagerAdapter object of this class is viewPagerAdapter

                int unread=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat= snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }
                if(unread==0){
                    viewPagerAdapter.addFragment(new ChatsFragment(),"Chats");//add fragments using class object we made a function addFragment in the class with param- fragment,string
                }else {
                    viewPagerAdapter.addFragment(new ChatsFragment(),"("+unread+") Chats");
                }

                viewPagerAdapter.addFragment(new UsersFragment(),"Users");
                viewPagerAdapter.addFragment(new ProfileFragment(),"Profile");

                viewPager.setAdapter(viewPagerAdapter);//viewPager is the layout in which the adapter containing the fragments is set

                tabLayout.setupWithViewPager(viewPager);//this function is used to link the tab layout with the ViewPager.
                // This layout will be automatically populated from the pager adapter's page title
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //menu is set in res for the three dot options on the top right of the screen
    //onCreateOptionsMenu is overrided and onOptionsItemSelected also
    //in StartActivity onStart is overrided firebase getuser is used

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                //FLAG_ACTIVITY_CLEAR_TOP  the activities above it are destroyed in the stack so it is on the top and reused.
                // so  it will start activity using intent and clear the stack as per the flag is set
                return true;
        }
        return false;
    }

//View pager is used to swipe left or right and see new tabs. For tabs fragments is used so that different functionalities widgets views can be added
    //In XML view pager and tablayout is set. tablayout is for the two fragments users and chats it in the AppBarLayout
// and view pagers is for the fragments
    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;//for fragment and title
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            //constructor of class to initialize the value fragments and titles
            super(fm);//to call the constructor of parent class FragmentPagerAdapter is extended PagerAdapter
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }
        //two methods override getItem and getCount()
    //getCount() returns the number of pages we will have for that ArrayList is used in which fragments are. when object of this class is created in onCreate the variablex
    //gets initialized with the value getSupportFragmentManager() and getPosistion for the position

        @NonNull
        @Override
        public Fragment getItem(int position) {
            //returns the fragment to display for that page
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public  void addFragment(Fragment fragment,String title){
            //to add the adapter this function is created
            fragments.add(fragment);//from onCreate--fragment and titiles is passed
            titles.add(title);
        }

    //Ctrl+O
    @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            //returns the page title for the top indicator in the above getItem returns the fragments
            return titles.get(position);
        }
    }

    //code for showing user is active or not thats why onResume and onPause methods are used and same code is being copied in MessageActivity.java
    //xml user_item -> on and off layout is added
    //registered in RegisterActivity and as we have added in UserAdapter so we need to change wherever UserAdapter is used
    // like UserFragment it is set to false as it is shown only in chats secttion so true in chatsfragment
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
