//Jose Antonio Castro Teodoro n01384776 Section B
//Daniel Moore n01354875 Section B
//Ryan Black n01305403 Section B
//Alyssa Gomez n01042777 Section B
package ca.kainotomia.it.aphrodite.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ca.kainotomia.it.aphrodite.R;

public class AccountSettingsFragment extends Fragment implements View.OnClickListener{

    private AccountViewModel accountViewModel;
    private ViewGroup container;
    private LayoutInflater inflater;
    private TextView textView;
    private Button button;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_account_settings, container, false);

        Button AFSS_Button_add = (Button) rootView.findViewById(R.id.AF_Button_settings);
        Button AFSS_Button_remove = (Button) rootView.findViewById(R.id.AFSS_Button_remove);
        Button AFSS_Button_terminate = (Button) rootView.findViewById(R.id.AFSS_Button_terminate);

        AFSS_Button_add.setOnClickListener(this::onClick);
        AFSS_Button_remove.setOnClickListener(this::onClick);
        AFSS_Button_terminate.setOnClickListener(this::onClick);

        this.container = container;
        this.inflater = inflater;

        return rootView;
    }

    public void onClick(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.AF_Button_settings:
                //fragment = new AccountProfileFragment();
                replaceFragment(fragment);
                break;

            case R.id.AFSS_Button_remove:

                break;

            case R.id.AFSS_Button_terminate:

                break;
        }
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
