package com.hemant.askagain;
import static android.view.View.GONE;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    static ArrayList<PostModel> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddPostBtn;
    private String name, profilepic,profession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initViews(view);


        if(fabAddPostBtn.getVisibility()==GONE){
            fabAddPostBtn.setVisibility(View.VISIBLE);
        }


        fabAddPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabAddPostBtn.setVisibility(GONE);
                openAddPostFragment(new AddPostFragment());
            }
        });

        return view;
    }


    private void openAddPostFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction= requireActivity().getSupportFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        Log.d("TAG", "Bundle: "+bundle);
        bundle.putString("Name", name );
        bundle.putString("ProfilePic", profilepic);
        bundle.putString("Profession", profession);

        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment,fragment).addToBackStack(null).commit();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllPost();
    }

    private void getAllPost() {
        PostAdapter postAdapter = new PostAdapter(postList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postAdapter);

        FirebaseDatabase.getInstance().getReference().child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds: snapshot.getChildren()){
                        PostModel post = ds.getValue(PostModel.class);
                        post.setPostId(ds.getKey());
                        postList.add(post);
                        Log.d("TAG", "onDataChange: " + postList.size());
                    }
                    postAdapter.notifyItemInserted(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        fabAddPostBtn = view.findViewById(R.id.fabAddPostBtn);
        postList.clear();
    }
}