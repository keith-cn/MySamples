package com.training.MediaPlayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class Lyric {
	private static final String TAG = "Keith's Lyric";

    private static final long serialVersionUID = 20071125L;
    private static Logger log = Logger.getLogger(Lyric.class.getName());
    private int width;
    private int height;
    private long time;
    private long tempTime;
    public List<Sentence> list = new ArrayList<Sentence>();
    private boolean isMoving;
    private int currentIndex;
    private boolean initDone;
    private transient File file;
    private boolean enabled = true;
    private long during = Integer.MAX_VALUE;
    private int offset;
    private static final Pattern pattern = Pattern.compile("(?<=\\[).*?(?=\\])");

    public Lyric(File file) {
		Log.i(TAG, "Lyric constructor");
        this.file = file;
        initFile(file);
        initDone = true;
    }

    private void initFile(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
            Log.i(TAG, "initFile(), br="+br);
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp).append("\n");
            }
//            Log.i(TAG, "initFile(), sb="+sb.toString());
            initSentence(sb.toString());
        } catch (Exception ex) {
    		Log.e(TAG, "initFile fail 001");

        } finally {
            try {
                br.close();
            } catch (Exception ex) {
        		Log.e(TAG, "initFile fail 002");
            }
        }
    }


    private void initSentence(String content) {
        if (content == null || content.trim().equals("")) {
//            list.add(new Sentence(info.getFormattedName(), Integer.MIN_VALUE, Integer.MAX_VALUE));
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new StringReader(content));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                parseLine(temp.trim());
            }
            br.close();
            Collections.sort(list, new Comparator<Sentence>() {

                public int compare(Sentence o1, Sentence o2) {
                    return (int) (o1.getFromTime() - o2.getFromTime());
                }
            });
            if (list.size() == 0) {
                list.add(new Sentence("Music title", 0, Integer.MAX_VALUE));
                return;
            } else {
                Sentence first = list.get(0);
                list.add(0, new Sentence(" ", 0, first.getFromTime()));
            }

            int size = list.size();
            for (int i = 0; i < size; i++) {
                Sentence next = null;
                if (i + 1 < size) {
                    next = list.get(i + 1);
                }
                Sentence now = list.get(i);
                if (next != null) {
                    now.setToTime(next.getFromTime() - 1);
                }
            }
            if (list.size() == 1) {
                list.get(0).setToTime(Integer.MAX_VALUE);
            } else {
                Sentence last = list.get(list.size() - 1);
//                last.setToTime(info == null ? Integer.MAX_VALUE : info.getLength() * 1000 + 1000);
            }
        } catch (Exception ex) {
            Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void parseLine(String line) {
        if (line.equals("")) {
            return;
        }
        Matcher matcher = pattern.matcher(line);
        List<String> temp = new ArrayList<String>();
        int lastIndex = -1;
        int lastLength = -1;
        while (matcher.find()) {
            String s = matcher.group();
            int index = line.indexOf("[" + s + "]");
            if (lastIndex != -1 && index - lastIndex > lastLength + 2) {
                String content = line.substring(lastIndex + lastLength + 2, index);
                for (String str : temp) {
                    long t = parseTime(str);
                    if (t != -1) {
                        list.add(new Sentence(content, t));
                    }
                }
                temp.clear();
            }
            temp.add(s);
            lastIndex = index;
            lastLength = s.length();
        }
        if (temp.isEmpty()) {
            return;
        }
        
        try {
        	
            int length = lastLength + 2 + lastIndex;
            String content = line.substring(length > line.length() ? line.length() : length);
            
            for (String s : temp) {
                long t = parseTime(s);
                if (t != -1) {
                    list.add(new Sentence(content, t));
                }
            }
        } catch (Exception exe) {
        }
        
    }

    private long parseTime(String time) {
        String[] ss = time.split("\\:|\\.");
        if (ss.length < 2) {
            return -1;
        } else if (ss.length == 2) {
            try {
                if (offset == 0 && ss[0].equalsIgnoreCase("offset")) {
                    offset = Integer.parseInt(ss[1]);
                    return -1;
                }
                int min = Integer.parseInt(ss[0]);
                int sec = Integer.parseInt(ss[1]);
                if (min < 0 || sec < 0 || sec >= 60) {
                    throw new RuntimeException("error");
                }
                return (min * 60 + sec) * 1000L;
            } catch (Exception exe) {
                return -1;
            }
        } else if (ss.length == 3) {
            try {
                int min = Integer.parseInt(ss[0]);
                int sec = Integer.parseInt(ss[1]);
                int mm = Integer.parseInt(ss[2]);
                if (min < 0 || sec < 0 || sec >= 60 || mm < 0 || mm > 99) {
                    throw new RuntimeException("error!");
                }
                return (min * 60 + sec) * 1000L + mm * 10;
            } catch (Exception exe) {
                return -1;
            }
        } else {
            return -1;
        }
    }

}
