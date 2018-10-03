package vn.ditagis.com.tanhoa.qlsc.fragment.list_task;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.ditagis.com.tanhoa.qlsc.ListTaskActivity;
import vn.ditagis.com.tanhoa.qlsc.R;


@SuppressLint("ValidFragment")
public class SearchFragment extends Fragment {
    private View mRootView;

    @SuppressLint("ValidFragment")
    public SearchFragment(ListTaskActivity activity, final LayoutInflater inflater) {
        mRootView = inflater.inflate(R.layout.fragment_list_task_search, null);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRootView;
    }

    public void refresh() {

    }
}
