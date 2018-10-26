package com.project.jhunting1.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.jhunting1.Local.JhuntingDB;
import com.project.jhunting1.Local.JobDataSource;
import com.project.jhunting1.Local.Job_ApplicationDataSource;
import com.project.jhunting1.Model.Job_Applications;
import com.project.jhunting1.Model.Jobs;
import com.project.jhunting1.Model.Player;
import com.project.jhunting1.R;
import com.project.jhunting1.Repository.JobRepo;
import com.project.jhunting1.Repository.Job_ApplicationRepo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class applicationListAdapter extends ArrayAdapter<Job_Applications> {

    private Context context;
    private int layout_Resource;
    private View view;
    private List<Job_Applications> applicationsList = new ArrayList<Job_Applications>();

    JhuntingDB jhuntingDB;
    private static JobRepo jobRepo;
    private Job_ApplicationRepo job_applicationRepo;

    private Player player;
    private Jobs job;

    private ArrayAdapter<Job_Applications> applicationsArrayAdapter;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public applicationListAdapter(@NonNull Context context, int resource, @NonNull List<Job_Applications> objects, View view) {
        super(context, resource, objects);
        this.context = context;
        this.layout_Resource = resource;
        this.view = view;
        this.applicationsList = objects;
        jhuntingDB = JhuntingDB.getINSTANCE(view.getContext());
        jobRepo = jobRepo.getInstance(JobDataSource.getInstance(jhuntingDB.jobDAO()));
        job_applicationRepo = job_applicationRepo.getInstance(Job_ApplicationDataSource.getInstance(jhuntingDB.job_applicationDAO()));
        applicationsArrayAdapter = new ArrayAdapter<Job_Applications>(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (!applicationsList.isEmpty()) {
            final int player_id = getItem(position).getPlayer_id();
            final int job_id = getItem(position).getJob_id();
            final String status = getItem(position).getStatus().toString();

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layout_Resource, parent, false);
            final View finalConvertView = convertView;
            Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                   // player = job_applicationRepo.getPlayer(player_id);
                   // job = jobRepo.getJob(job_id);

                    getItem(position).setJobs(job_applicationRepo.getJob(job_id));
                    getItem(position).setPlayer(job_applicationRepo.getPlayer(player_id));
                    applicationsArrayAdapter.notifyDataSetChanged();
                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer() {

                                   @Override
                                   public void accept(Object o) throws Exception {
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                   }
                               }, new Action() {
                                   @Override
                                   public void run() throws Exception {

                                   }
                               }

                    );

            TextView tvPlayerName = (TextView) finalConvertView.findViewById(R.id.textViewPlayerName);
            TextView tvJobTitle = (TextView) finalConvertView.findViewById(R.id.textViewJobTitle);
            TextView tvStatus = (TextView) finalConvertView.findViewById(R.id.textViewStatus);

            if (getItem(position).getPlayer() != null && getItem(position).getJobs() != null) {
                tvPlayerName.setText(getItem(position).getPlayer().getName().toString());
                tvJobTitle.setText(getItem(position).getJobs().getJob_title().toString());
            }
            tvStatus.setText(status);


            compositeDisposable.add(disposable);
        }

        return convertView;
    }
}
