package com.nachtgeistw.impurebird.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nachtgeistw.impurebird.R;
import com.nachtgeistw.impurebird.util.TweetAdapter;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;

import static com.nachtgeistw.impurebird.BirdMainInterface.twitter;

public class HomeFragment extends Fragment {

    private List<Tweets> tweetList = new ArrayList<>();
    private List<Status> statusList = new ArrayList<>();
    private int showTweetNum = 20;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById((R.id.home_timeline_recyclerview));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //获取推特并展示
        new PullHomeTimeline().execute();
        return root;
    }

    class PullHomeTimeline extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                statusList = twitter.getHomeTimeline();
                //一次 load showTweetNum 条。
                for (int i = 0; i < showTweetNum; i++) {
                    Tweets tweets = new Tweets(statusList.get(i).getText());
                    tweetList.add(tweets);
                }
                return true;
            } catch (TwitterException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            TweetAdapter adapter = new TweetAdapter(tweetList);
            recyclerView.setAdapter(adapter);
            if (!result) {
                Toast.makeText(getContext(), String.valueOf(R.string.pull_home_timeline_failed),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}