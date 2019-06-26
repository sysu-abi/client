package com.sysu.ceres.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.sysu.ceres.CeresConfig;
import com.sysu.ceres.R;
import com.sysu.ceres.activity.EditTaskActivity;
import com.sysu.ceres.activity.LoginActivity;
import com.sysu.ceres.http.ApiMethods;
import com.sysu.ceres.model.Status;
import com.sysu.ceres.model.Task;
import com.sysu.ceres.model.UserList;
import com.sysu.ceres.observer.MyObserver;
import com.sysu.ceres.observer.ObserverOnNextListener;

import java.sql.Timestamp;

import static android.content.ContentValues.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaskDetailFragment extends Fragment {

    private static final String ARG_CURRENT_TASK = "current_task";

    //0-未参与；1-参与；2-发布者; 3-发布者带问卷
    private int show_status = 0;
    Button edit_btn;
    Button disjoin_btn;
    Button finish_btn;
    Button join_btn;
    Button get_statistic_btn;

    private Task currentTask;
    private ObserverOnNextListener<Status> listener = new ObserverOnNextListener<Status>() {
        @Override
        public void onNext(Status status) {
            Log.d(TAG, "onNext: " + status.toString());
            if (status.getState().equals("success")) {
                Toast.makeText(getActivity(),
                        status.getState(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(),
                        status.getState(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private ObserverOnNextListener<UserList> getJoinUserlistener = new ObserverOnNextListener<UserList>() {
        @Override
        public void onNext(UserList userlist) {
            Log.d(TAG, "onNext: " + userlist.toString());
            if (userlist.isJointUser(CeresConfig.currentUser.getUid())) {
                show_status = 1;
                edit_btn.setVisibility(View.GONE);
                disjoin_btn.setVisibility(View.VISIBLE);
                finish_btn.setVisibility(View.GONE);
                join_btn.setVisibility(View.GONE);
            } else {
                show_status = 0;
                edit_btn.setVisibility(View.GONE);
                disjoin_btn.setVisibility(View.GONE);
                finish_btn.setVisibility(View.GONE);
                join_btn.setVisibility(View.VISIBLE);
            }
        }
    };


    public static TaskDetailFragment newInstance(Task item) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CURRENT_TASK, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTask = (Task) getArguments().getSerializable(ARG_CURRENT_TASK);
        }
        if (CeresConfig.currentUser == null) {
            show_status = 0; //未参与
        } else if (currentTask.getUid().equals(CeresConfig.currentUser.getUid())){
            show_status = currentTask.getType() == "survey" ? 3 : 2; // 发布者
        } else {
            ApiMethods.getJoinUsers(new MyObserver<UserList>(getActivity(), getJoinUserlistener), currentTask.getTid().intValue());
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_task_detail, container, false);

        final TextView task_title = root.findViewById(R.id.task_detail_title);
        final TextView task_detail = root.findViewById(R.id.task_detail_detail);
        final TextView task_current_num = root.findViewById(R.id.task_detail_current_num);
        final TextView task_total_num = root.findViewById(R.id.task_detail_total_num);
        final TextView task_type = root.findViewById(R.id.task_detail_type);
        final TextView task_money = root.findViewById(R.id.task_detail_money);
        final TextView task_start_time = root.findViewById(R.id.task_detail_start_time);
        final TextView task_end_time = root.findViewById(R.id.task_detail_end_time);

        task_title.setText(currentTask.getTitle());
        task_detail.setText(currentTask.getDetail());
        task_current_num.setText("Current num: " + currentTask.getCurrentNum().toString());
        task_total_num.setText("Total num: " + currentTask.getTotalNum().toString());
        task_type.setText("Type: " + currentTask.getType());
        task_money.setText("Money: "  + currentTask.getMoney().toString());
        Timestamp time = new Timestamp(currentTask.getStartTime());
        task_start_time.setText("Start Time: " + time.toString());
        time = new Timestamp(currentTask.getEndTime());
        task_end_time.setText("End Time: " + time.toString());

        edit_btn = root.findViewById(R.id.task_detail_edit_btn);
        disjoin_btn = root.findViewById(R.id.task_detail_disjoin_btn);
        finish_btn = root.findViewById(R.id.task_detail_finish_btn);
        join_btn = root.findViewById(R.id.task_detail_join_btn);
        get_statistic_btn = root.findViewById(R.id.task_detail_get_static);

        Log.d("current task uid: " , currentTask.getUid().toString());
        Log.d("user: ", CeresConfig.currentUser.getUid().toString());

        switch (show_status) {
            case 0:
                edit_btn.setVisibility(View.GONE);
                disjoin_btn.setVisibility(View.GONE);
                finish_btn.setVisibility(View.GONE);
                join_btn.setVisibility(View.VISIBLE);
                get_statistic_btn.setVisibility(View.GONE);
                break;
            case 1:
                edit_btn.setVisibility(View.GONE);
                disjoin_btn.setVisibility(View.GONE);
                finish_btn.setVisibility(View.VISIBLE);
                join_btn.setVisibility(View.GONE);
                get_statistic_btn.setVisibility(View.GONE);
                break;
            case 2:
                edit_btn.setVisibility(View.VISIBLE);
                finish_btn.setVisibility(View.VISIBLE);
                disjoin_btn.setVisibility(View.GONE);
                join_btn.setVisibility(View.GONE);
                get_statistic_btn.setVisibility(View.GONE);
                break;
            case 3:
                edit_btn.setVisibility(View.VISIBLE);
                finish_btn.setVisibility(View.VISIBLE);
                disjoin_btn.setVisibility(View.GONE);
                join_btn.setVisibility(View.GONE);
                get_statistic_btn.setVisibility(View.VISIBLE);
            default:
                edit_btn.setVisibility(View.GONE);
                disjoin_btn.setVisibility(View.GONE);
                finish_btn.setVisibility(View.GONE);
                join_btn.setVisibility(View.VISIBLE);
                get_statistic_btn.setVisibility(View.GONE);
                break;
        }

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ARG_CURRENT_TASK, currentTask);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        disjoin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiMethods.disjoinTask(new MyObserver<Status>(root.getContext(), listener), currentTask.getTid().intValue(), CeresConfig.currentUser.getUid().intValue());
            }
        });

        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiMethods.endTask(new MyObserver<Status>(root.getContext(), listener), currentTask.getTid().intValue(), CeresConfig.currentUser.getUid().intValue());
            }
        });

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CeresConfig.currentUser != null) {
                    ApiMethods.joinTask(new MyObserver<Status>(root.getContext(), listener), currentTask.getTid().intValue(), CeresConfig.currentUser.getUid().intValue());
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        });

        get_statistic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        return root;
    }
}