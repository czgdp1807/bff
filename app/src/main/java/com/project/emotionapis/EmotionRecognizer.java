package com.project.emotionapis;

import android.util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EmotionRecognizer
{
    private String emotionName;
    private static final HashSet<String> negationWords = new HashSet<String>();
    private static final HashMap<String, String> emotion2Remedy = new HashMap<String, String>();
    private static final HashMap<String, String> emotion2Places = new HashMap<String, String>();
    private static HashMap<String, Pair<String, String>>
    word2InversionEmotion = new HashMap<String, Pair<String, String>>();
    private static Boolean isStaticInitialised = false;

    private void initaliseStaticMembers()
    {
        if( !isStaticInitialised )
        {
            String negationWordsArray[] = {"not"};
            negationWords.addAll(Arrays.asList(negationWordsArray));
            emotion2Remedy.put("happy", "happy, fun or comedy content");
            emotion2Remedy.put("excited", "energetic, adventure or action content");
            emotion2Remedy.put("tender", "love, romance or dating content");
            emotion2Remedy.put("sad", "happy, fun or comedy content");
            emotion2Remedy.put("angry", "calm, relax, or comedy content");
            emotion2Remedy.put("scared", "brave or courage content");
            emotion2Remedy.put("nothing", "nature vlogs");
            emotion2Places.put("happy", "happy");
            emotion2Places.put("excited", "adventure");
            emotion2Places.put("tender", "park");
            emotion2Places.put("sad", "therapy");
            emotion2Places.put("angry", "temple");
            emotion2Places.put("scared", "temple");
            emotion2Places.put("nothing", "park");
            word2InversionEmotion.put("happy", new Pair<String, String>("sad", "happy"));
            word2InversionEmotion.put("sad", new Pair<String, String>("happy", "sad"));
            word2InversionEmotion.put("excited", new Pair<String, String>("tender", "excited"));
            word2InversionEmotion.put("tender", new Pair<String, String>("excited", "tender"));
            word2InversionEmotion.put("angry", new Pair<String, String>("tender", "angry"));
            word2InversionEmotion.put("scared", new Pair<String, String>("angry", "scared"));
            isStaticInitialised = true;
        }
    }

    public EmotionRecognizer()
    {
        emotionName = "nothing";
        initaliseStaticMembers();
    }

    public void guessAndSetEmotion(String message)
    {
        String lowerCaseMessage = message.toLowerCase();
        String words[] = lowerCaseMessage.split(" ");
        HashMap<String, Integer> emotionFrequency = new HashMap<String, Integer>();
        for( String emotion: emotion2Remedy.keySet() )
        {
            emotionFrequency.put(emotion, 0);
        }
        Boolean invertEmotion = false;
        for( String word: words )
        {
            if( negationWords.contains(word) )
            {
                invertEmotion = true;
                continue;
            }
            if( word2InversionEmotion.containsKey(word) )
            {
                String currEmotion;
                if( invertEmotion )
                {
                    String invertedWord = word2InversionEmotion.get(word).first;
                    currEmotion = word2InversionEmotion.get(invertedWord).second;
                    invertEmotion = false;
                }
                else
                {
                    currEmotion = word2InversionEmotion.get(word).second;
                }
                emotionFrequency.put(currEmotion, emotionFrequency.get(currEmotion) + 1);
            }
        }
        Integer maxFrequency = 0;
        for( Map.Entry<String, Integer> keyValue: emotionFrequency.entrySet() )
        {
            Integer currFreq = keyValue.getValue();
            if( currFreq > maxFrequency )
            {
                maxFrequency = currFreq;
                emotionName = keyValue.getKey();
            }
        }
    }

    public String getSpaceSeparatedRemedyTerms()
    {
        return emotion2Remedy.get(emotionName);
    }

    public String getRemedyPlaces()
    {
        return emotion2Places.get(emotionName);
    }

    public String getEmotionName()
    {
        return emotionName;
    }
}
