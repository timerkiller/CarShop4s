package com.example.vke.shop4stech.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.activity.HomeActivity;
import com.example.vke.shop4stech.helper.PreferencesHelper;
import com.example.vke.shop4stech.helper.TransitionHelper;
import com.example.vke.shop4stech.model.User;


/**
 * Enable selection of user name.
 */
public class SignInFragment extends Fragment {

    private static final String ARG_EDIT = "EDIT";
    private User mUser;
    private EditText mUserName;
    private EditText mPassword;
    private Button mLoginButton;
    private boolean edit;
    private static SignInFragment ourInstance = null;
    private OnFragmentInteractionListener mListener;

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment getInstance(){
        if (ourInstance == null) {
            ourInstance = new SignInFragment();
        }
        return ourInstance;
    }

    public static SignInFragment newInstance(boolean edit) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_EDIT, edit);
        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            final int savedAvatarIndex = savedInstanceState.getInt(KEY_SELECTED_AVATAR_INDEX);
//            if (savedAvatarIndex != GridView.INVALID_POSITION) {
//                mSelectedAvatar = Avatar.values()[savedAvatarIndex];
//            }
//        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        if (mAvatarGrid != null) {
//            outState.putInt(KEY_SELECTED_AVATAR_INDEX, mAvatarGrid.getCheckedItemPosition());
//        } else {
//            outState.putInt(KEY_SELECTED_AVATAR_INDEX, GridView.INVALID_POSITION);
//        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        assurePlayerInit();
        checkIsInEditMode();

        if (mUser == null || edit) {
            System.out.println(">>>>>>>>>>>>>>in  onViewCreated");
            view.findViewById(R.id.empty).setVisibility(View.GONE);
//            view.findViewById(R.id.content).setVisibility(View.VISIBLE);
            initContentViews(view);
            initContents();
        } else {
            System.out.println(">>>>>>>>>>>>>>in  else");
            final Activity activity = getActivity();
//            CategorySelectionActivity.start(activity, mPlayer);
//            activity.finish();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    private void checkIsInEditMode() {
        final Bundle arguments = getArguments();
        //noinspection SimplifiableIfStatement
        if (arguments == null) {
            edit = false;
        } else {
            edit = arguments.getBoolean(ARG_EDIT, false);
        }
    }

    private void initContentViews(View view) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* no-op */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // hiding the floating action button if text is empty
                if (s.length() == 0) {
                   System.out.println("onTextChanged");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // showing the floating action button if avatar is selected and input data is valid
                System.out.println("onTextChanged");
            }
        };

        mUserName = (EditText) view.findViewById(R.id.edit_text_username);
        mUserName.addTextChangedListener(textWatcher);
        mPassword = (EditText) view.findViewById(R.id.edit_text_password);
        mPassword.addTextChangedListener(textWatcher);
        mLoginButton = (Button) view.findViewById(R.id.tech_button_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tech_button_login:
                        //save data should not put
                        //savePlayer(getActivity());
                        Intent startHomeActivityIntent = new Intent(getActivity(),HomeActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("username","vic");
                        bundle.putString("password","123456");
                        bundle.putString("accessToken","fdskfjksdlajfdsklffdsaf");

                        removeDoneFab(new Runnable() {
                            @Override
                            public void run() {
//                                if (null == mSelectedAvatarView) {
//                                    performSignInWithTransition(mAvatarGrid.getChildAt(
//                                            mSelectedAvatar.ordinal()));
//                                } else {
//                                    //performSignInWithTransition(mSelectedAvatarView);
//                                }
                            }
                        });
                        startActivity(startHomeActivityIntent,bundle);

                        getActivity().finish();
                        break;
                    default:
                        throw new UnsupportedOperationException(
                                "The onClick method has not been implemented for " + getResources()
                                        .getResourceEntryName(v.getId()));
                }
            }
        });
    }

    private void removeDoneFab(@Nullable Runnable endAction) {
        ViewCompat.animate(mLoginButton)
                .scaleX(0)
                .scaleY(0)
                .setInterpolator(new FastOutSlowInInterpolator())
                .withEndAction(endAction)
                .start();
    }


    private void performSignInWithTransition(View v) {
        final Activity activity = getActivity();
//
//        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, true,
//                new Pair<>(v, activity.getString(R.string.transition_avatar)));
//        @SuppressWarnings("unchecked")
//        ActivityOptionsCompat activityOptions = ActivityOptionsCompat
//                .makeSceneTransitionAnimation(activity, pairs);
//        HomeActivity.start(activity, mUser, activityOptions);
    }

    private void initContents() {
        assurePlayerInit();
        if (mUser != null) {
            mUserName.setText(mUser.getUserName());
            mPassword.setText(mUser.getPassword());
        }
    }

    private void assurePlayerInit() {
        if (mUser == null) {
            mUser = PreferencesHelper.getUser(getActivity());
        }
    }

    private void savePlayer(Activity activity) {
        mUser = new User(mUserName.getText().toString(), mPassword.getText().toString());
        PreferencesHelper.writeToPreferences(activity, mUser);
    }

    private boolean isAvatarSelected() {
        return true;
    }

    private boolean isInputDataValid() {
        return PreferencesHelper.isInputDataValid(mUserName.getText(), mPassword.getText());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}

