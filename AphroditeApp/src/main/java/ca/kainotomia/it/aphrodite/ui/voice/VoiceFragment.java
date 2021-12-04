//Jose Antonio Castro Teodoro n01384776 Section B
//Daniel Moore n01354875 Section B
//Ryan Black n01305403 Section B
//Alyssa Gomez n01042777 Section B
package ca.kainotomia.it.aphrodite.ui.voice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import ca.kainotomia.it.aphrodite.R;
import ca.kainotomia.it.aphrodite.UpdateDBNode;
import ca.kainotomia.it.aphrodite.ui.home.HomeFragment;

public class VoiceFragment extends Fragment {

//    TextView n1, n2, n3, n4, n5, n6, n7;
//    TextView d1, d2, d3, d4, d5, d6, d7;
    ToggleButton muteMic;
    ExtendedFloatingActionButton newVoiceCommand;

    private RecyclerView voiceDefRV;
    private RecyclerView voiceUserRV;
    private FirebaseRecyclerAdapter<VoiceModel, VoiceHolder> voiceFBRA;
    private FirebaseRecyclerAdapter<VoiceModel, VoiceHolder> voiceUserFBRA;
    private FragmentContainerView newCmdFragment;
    private int numUserCommands;



//    // Write a message to the database
//    FirebaseDatabase database;
//    DatabaseReference myRef;

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.voice_fragment, container, false);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        voiceDefRV = root.findViewById(R.id.voice_defRecyclerView);
        voiceDefRV.setLayoutManager(new GridLayoutManager(root.getContext(),2));
        voiceDefRV.setHasFixedSize(false);

        voiceUserRV = root.findViewById(R.id.voice_userRecyclerView);
        voiceUserRV.setLayoutManager(new LinearLayoutManager(root.getContext()));
        voiceUserRV.setHasFixedSize(false);
//        voiceRV

        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(getActivity(), getString(R.string.voice_noConnection), Toast.LENGTH_LONG).show();
        }

        getFirebaseDefaultVoiceCommands();
        getFirebaseUserVoiceCommands();


        return root;
        // View root = inflater.inflate(R.layout.voice_fragment, container, false);
        //        return root;
    }

    private void getFirebaseDefaultVoiceCommands() {
        UpdateDBNode dbNode = new UpdateDBNode("def_voice_commands");
        Query query = dbNode.getDatabaseReference();

        FirebaseRecyclerOptions<VoiceModel> options = new FirebaseRecyclerOptions.Builder<VoiceModel>()
                .setQuery(query, new SnapshotParser<VoiceModel>() {
                    @NonNull
                    @Override
                    public VoiceModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new VoiceModel(snapshot.getKey(), snapshot.getValue().toString());
                    }
                })
                .build();

        voiceFBRA = new FirebaseRecyclerAdapter<VoiceModel, VoiceHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VoiceHolder holder, int position, @NonNull VoiceModel model) {
                holder.getTitle().setText(model.getVoiceCmdTitle());
                holder.getDesc().setText(model.getVoiceCmdDesc());
            }

            @NonNull
            @Override
            public VoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.voice_cmd_item, parent, false);
                return new VoiceHolder(view);
            }
        };
        voiceDefRV.setAdapter(voiceFBRA);
    }

    private void getFirebaseUserVoiceCommands() {
        UpdateDBNode dbNode = new UpdateDBNode("user_voice_commands");
        Query query = dbNode.getDatabaseReference().child(dbNode.getCurrentUid());

        FirebaseRecyclerOptions<VoiceModel> options = new FirebaseRecyclerOptions.Builder<VoiceModel>()
                .setQuery(query, new SnapshotParser<VoiceModel>() {
                    @NonNull
                    @Override
                    public VoiceModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                        numUserCommands++;
                        return new VoiceModel(snapshot.getKey(), snapshot.getValue().toString());
                    }
                })
                .build();

        voiceUserFBRA = new FirebaseRecyclerAdapter<VoiceModel, VoiceHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VoiceHolder holder, int position, @NonNull VoiceModel model) {
                holder.getTitle().setText(model.getVoiceCmdTitle());
                holder.getDesc().setText(model.getVoiceCmdDesc());
            }

            @NonNull
            @Override
            public VoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.voice_cmd_item, parent, false);
                return new VoiceHolder(view);
            }
        };
        voiceUserRV.setAdapter(voiceUserFBRA);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        voiceFBRA.stopListening();
        voiceUserFBRA.stopListening();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        voiceFBRA.startListening();
        voiceUserFBRA.startListening();
//        voiceUserFBRA.getItemCount();

        muteMic = view.findViewById(R.id.VC_muteMic_btnID);
        newVoiceCommand = view.findViewById(R.id.voice_fab);
        newCmdFragment = view.findViewById(R.id.voice_nestedFragment);

        //Update DB with mic state
        muteMic.setOnClickListener(v -> {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            System.out.println(networkInfo);
            if (networkInfo != null && networkInfo.isConnected()) {
                UpdateDBNode dbNode = new UpdateDBNode("mic_state");
                //Connected, update db with mic state
                if (muteMic.isChecked()) {
                    Toast.makeText(getActivity(), getString(R.string.VC_mic_muted), Toast.LENGTH_SHORT).show();
                    dbNode.setMicState(false);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.VC_mic_unmuted), Toast.LENGTH_SHORT).show();
                    dbNode.setMicState(true);
                }
            } else {
                //Not Connected, let user know that the button won't do anything
                Toast.makeText(getActivity(), getString(R.string.voice_noConnection), Toast.LENGTH_SHORT).show();
            }
        });

        newVoiceCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO user can only create 3 commands, check that 3 have been created, if 3 no more
                System.out.println(voiceUserFBRA.getItemCount());
                VoiceNewCmd voiceNewCmd = new VoiceNewCmd();
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.voice_nestedFragment, voiceNewCmd).commit();
                Animation fadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
                newCmdFragment.startAnimation(fadeIn);
                newCmdFragment.setVisibility(View.VISIBLE);


            }
        });

        Button goToLED = view.findViewById(R.id.VC_changeLED_btnID);
        goToLED.setOnClickListener(v -> {
            Fragment ledColour = new LEDColourFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, ledColour);
            transaction.addToBackStack(null);
            transaction.commit();
        });


    }

    public int getNumUserCommands() {
        return numUserCommands;
    }
}