package com.training.MediaPlayer;

import android.net.Uri;

interface ISearchAudioFile {
    String getCurrent();
    String getNext();
    String getPrevious();
    String getCurrentMusicTitle();
}
