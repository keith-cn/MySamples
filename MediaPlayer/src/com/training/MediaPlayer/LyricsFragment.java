package com.training.MediaPlayer;

//import android.app.Fragment;
import java.io.File;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class LyricsFragment extends ListFragment {
	private static final String TAG = "Keith's LyricsFragment";
	
	Lyric mLyric;
	LyricsListAdapter mLyricsListAdapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		mLyricsListAdapter = new LyricsListAdapter(getActivity());
		setListAdapter(mLyricsListAdapter);
	}
	
	public void setMusicUriInfo(String fullPath) {
		String lyricPath = null;
		
		lyricPath = fullPath.substring(0, fullPath.length()-3) + "lrc";
		Log.i(TAG, "setMusicUriInfo(): lyricPath=" + lyricPath);
		File file = new File(lyricPath);
		
		mLyric = new Lyric(file);
		mLyricsListAdapter = new LyricsListAdapter(getActivity());
		setListAdapter(mLyricsListAdapter);
		
//		ListView list = this.getListView();
//		((TextView)list.getVi getItemAtPosition(0)).setTextColor(0x12345678);
	}
	

    private class LyricsListAdapter extends BaseAdapter {
        public LyricsListAdapter(Context context) {
            mContext = context;
        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         * 
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
        	if (mLyric == null) {
        		return 0;
        	}
            return mLyric.list.size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         * 
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return mLyric.list.get(position);
        }

        /**
         * Use the array index as a unique id.
         * 
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a LyricSentenceView to hold each row.
         * 
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            LyricSentenceView sv;
            if (convertView == null) {
                sv = new LyricSentenceView(mContext, mLyric.list.get(position).getContent());
            } else {
                sv = (LyricSentenceView) convertView;
                sv.setSentence(mLyric.list.get(position).getContent());
            }

            return sv;
        }

        /**
         * Remember our context so we can use it when constructing views.
         */
        private Context mContext;
    }
    
    /**
     * We will use a LyricSentenceView to display each speech. It's just a LinearLayout
     * with two text fields.
     *
     */
    private class LyricSentenceView extends LinearLayout {
        public LyricSentenceView(Context context, String words) {
            super(context);

            this.setOrientation(VERTICAL);

            // Here we build the child views in code. They could also have
            // been specified in an XML file.

            mDialogue = new TextView(context);
            mDialogue.setText(words);
            addView(mDialogue, new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }


        /**
         * Convenience method to set the dialogue of a LyricSentenceView
         */
        public void setSentence(String words) {
            mDialogue.setText(words);
        }

        private TextView mDialogue;
    }
}
