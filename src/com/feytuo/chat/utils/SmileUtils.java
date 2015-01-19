/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agrxxd to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Sxx the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feytuo.chat.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.feytuo.laoxianghao.R;

public class SmileUtils {
	
	public static final String xx_1 = "[):]";
	public static final String xx_2 = "[:D]";
	public static final String xx_3 = "[;)]";
	public static final String xx_4 = "[:-o]";
	public static final String xx_5 = "[:p]";
	public static final String xx_6 = "[(H)]";
	public static final String xx_7 = "[:@]";
	public static final String xx_8 = "[:s]";
	public static final String xx_9 = "[:$]";
	public static final String xx_10 = "[:(]";
	public static final String xx_11 = "[:'(]";
	public static final String xx_12 = "[:|]";
	public static final String xx_13 = "[(a)]";
	public static final String xx_14 = "[8o|]";
	public static final String xx_15 = "[8-|]";
	public static final String xx_16 = "[+o(]";
	public static final String xx_17 = "[<o)]";
	public static final String xx_18 = "[|-)]";
	public static final String xx_19 = "[*-)]";
	public static final String xx_20 = "[:-#]";
	public static final String xx_21 = "[:-*]";
	public static final String xx_22 = "[^o)]";
	public static final String xx_23 = "[8-)]";
	public static final String xx_24 = "[(|)]";
	public static final String xx_25 = "[(u)]";
	public static final String xx_26 = "[(S)]";
	public static final String xx_27 = "[(*)]";
	public static final String xx_28 = "[(#)]";
	public static final String xx_29 = "[(R)]";
	public static final String xx_30 = "[({)]";
	public static final String xx_31 = "[(})]";
	public static final String xx_32 = "[(k)]";
	public static final String xx_33 = "[(F)]";
	public static final String xx_34 = "[(W)]";
	public static final String xx_35 = "[(D)]";
	
	public static final String xx_36 = "[):!]";
	public static final String xx_37= "[:D!]";
	public static final String xx_38 = "[;)!]";
	public static final String xx_39 = "[:-o!]";
	public static final String xx_40 = "[:p1]";
	public static final String xx_41 = "[(H)!]";
	public static final String xx_42 = "[:@!]";
	public static final String xx_43 = "[:s!]";
	public static final String xx_44 = "[:$!]";
	public static final String xx_45 = "[:(!]";
	public static final String xx_46 = "[:'(!]";
	public static final String xx_47 = "[:|!]";
	public static final String xx_48 = "[(a)!]";
	public static final String xx_49 = "[8o|1]";
	public static final String xx_50 = "[8-|1]";
	public static final String xx_51 = "[+o(!]";
	public static final String xx_52 = "[<o)!]";
	public static final String xx_53 = "[|-)!]";
	public static final String xx_54 = "[*-)1]";
	public static final String xx_55 = "[:-#!]";
	public static final String xx_56 = "[:-*!]";
	public static final String xx_57 = "[^o)!]";
	public static final String xx_58 = "[8-)!]";
	public static final String xx_59 = "[(|)!]";
	public static final String xx_60 = "[(u)!]";
	public static final String xx_61 = "[(S)!]";
	public static final String xx_62 = "[(*)!]";
	public static final String xx_63 = "[(#)!]";
	public static final String xx_64 = "[(R)!]";
	public static final String xx_65 = "[({)!]";
	public static final String xx_66 = "[(})!]";
	public static final String xx_67 = "[(k)!]";
	public static final String xx_68 = "[(F)!]";
	public static final String xx_69 = "[(W)!]";
	public static final String xx_70 = "[(D)!]";
	
	public static final String xx_71 = "[):?]";
	public static final String xx_72 = "[:D?]";
	public static final String xx_73 = "[;)?]";
	public static final String xx_74 = "[:-o?]";
	public static final String xx_75 = "[:p?]";
	public static final String xx_76 = "[(H)?]";
	public static final String xx_77 = "[:@?]";
	public static final String xx_78 = "[:s?]";
	public static final String xx_79 = "[:$?]";
	public static final String xx_80 = "[:(?]";
	public static final String xx_81 = "[:'(?]";
	public static final String xx_82 = "[:|?]";
	public static final String xx_83 = "[(a)?]";
	public static final String xx_84 = "[8o|?]";
	public static final String xx_85 = "[8-|?]";
	public static final String xx_86 = "[+o(?]";
	public static final String xx_87 = "[<o)?]";
	public static final String xx_88 = "[|-)?]";
	public static final String xx_89 = "[*-)?]";
	public static final String xx_90 = "[:-#?]";
	
	private static final Factory spannableFactory = Spannable.Factory
	        .getInstance();
	
	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {
		
	    addPattern(emoticons, xx_1, R.drawable.xx_1);
	    addPattern(emoticons, xx_2, R.drawable.xx_2);
	    addPattern(emoticons, xx_3, R.drawable.xx_3);
	    addPattern(emoticons, xx_4, R.drawable.xx_4);
	    addPattern(emoticons, xx_5, R.drawable.xx_5);
	    addPattern(emoticons, xx_6, R.drawable.xx_6);
	    addPattern(emoticons, xx_7, R.drawable.xx_7);
	    addPattern(emoticons, xx_8, R.drawable.xx_8);
	    addPattern(emoticons, xx_9, R.drawable.xx_9);
	    addPattern(emoticons, xx_10, R.drawable.xx_10);
	    addPattern(emoticons, xx_11, R.drawable.xx_11);
	    addPattern(emoticons, xx_12, R.drawable.xx_12);
	    addPattern(emoticons, xx_13, R.drawable.xx_13);
	    addPattern(emoticons, xx_14, R.drawable.xx_14);
	    addPattern(emoticons, xx_15, R.drawable.xx_15);
	    addPattern(emoticons, xx_16, R.drawable.xx_16);
	    addPattern(emoticons, xx_17, R.drawable.xx_17);
	    addPattern(emoticons, xx_18, R.drawable.xx_18);
	    addPattern(emoticons, xx_19, R.drawable.xx_19);
	    addPattern(emoticons, xx_20, R.drawable.xx_20);
	    addPattern(emoticons, xx_21, R.drawable.xx_21);
	    addPattern(emoticons, xx_22, R.drawable.xx_22);
	    addPattern(emoticons, xx_23, R.drawable.xx_23);
	    addPattern(emoticons, xx_24, R.drawable.xx_24);
	    addPattern(emoticons, xx_25, R.drawable.xx_25);
	    addPattern(emoticons, xx_26, R.drawable.xx_26);
	    addPattern(emoticons, xx_27, R.drawable.xx_27);
	    addPattern(emoticons, xx_28, R.drawable.xx_28);
	    addPattern(emoticons, xx_29, R.drawable.xx_29);
	    addPattern(emoticons, xx_30, R.drawable.xx_30);
	    addPattern(emoticons, xx_31, R.drawable.xx_31);
	    addPattern(emoticons, xx_32, R.drawable.xx_32);
	    addPattern(emoticons, xx_33, R.drawable.xx_33);
	    addPattern(emoticons, xx_34, R.drawable.xx_34);
	    addPattern(emoticons, xx_35, R.drawable.xx_35);
	    
	    addPattern(emoticons, xx_36, R.drawable.xx_36);
	    addPattern(emoticons, xx_37, R.drawable.xx_37);
	    addPattern(emoticons, xx_38, R.drawable.xx_38);
	    addPattern(emoticons, xx_39, R.drawable.xx_39);
	    addPattern(emoticons, xx_40, R.drawable.xx_40);
	    addPattern(emoticons, xx_41, R.drawable.xx_41);
	    addPattern(emoticons, xx_42, R.drawable.xx_42);
	    addPattern(emoticons, xx_43, R.drawable.xx_43);
	    addPattern(emoticons, xx_44, R.drawable.xx_44);
	    addPattern(emoticons, xx_45, R.drawable.xx_45);
	    addPattern(emoticons, xx_46, R.drawable.xx_46);
	    addPattern(emoticons, xx_47, R.drawable.xx_47);
	    addPattern(emoticons, xx_48, R.drawable.xx_48);
	    addPattern(emoticons, xx_49, R.drawable.xx_49);
	    addPattern(emoticons, xx_50, R.drawable.xx_50);
	    addPattern(emoticons, xx_51, R.drawable.xx_51);
	    addPattern(emoticons, xx_52, R.drawable.xx_52);
	    addPattern(emoticons, xx_53, R.drawable.xx_53);
	    addPattern(emoticons, xx_54, R.drawable.xx_54);
	    addPattern(emoticons, xx_55, R.drawable.xx_55);
	    addPattern(emoticons, xx_56, R.drawable.xx_56);
	    addPattern(emoticons, xx_57, R.drawable.xx_57);
	    addPattern(emoticons, xx_58, R.drawable.xx_58);
	    addPattern(emoticons, xx_59, R.drawable.xx_59);
	    addPattern(emoticons, xx_60, R.drawable.xx_60);
	    addPattern(emoticons, xx_61, R.drawable.xx_61);
	    addPattern(emoticons, xx_62, R.drawable.xx_62);
	    addPattern(emoticons, xx_63, R.drawable.xx_63);
	    addPattern(emoticons, xx_64, R.drawable.xx_64);
	    addPattern(emoticons, xx_65, R.drawable.xx_65);
	    addPattern(emoticons, xx_66, R.drawable.xx_66);
	    addPattern(emoticons, xx_67, R.drawable.xx_67);
	    addPattern(emoticons, xx_68, R.drawable.xx_68);
	    addPattern(emoticons, xx_69, R.drawable.xx_69);
	    addPattern(emoticons, xx_70, R.drawable.xx_70);
	    addPattern(emoticons, xx_71, R.drawable.xx_71);
	    addPattern(emoticons, xx_72, R.drawable.xx_72);
	    addPattern(emoticons, xx_73, R.drawable.xx_73);
	    addPattern(emoticons, xx_74, R.drawable.xx_74);
	    addPattern(emoticons, xx_75, R.drawable.xx_75);
	    addPattern(emoticons, xx_76, R.drawable.xx_76);
	    addPattern(emoticons, xx_77, R.drawable.xx_77);
	    addPattern(emoticons, xx_78, R.drawable.xx_78);
	    addPattern(emoticons, xx_79, R.drawable.xx_79);
	    addPattern(emoticons, xx_80, R.drawable.xx_80);
	    addPattern(emoticons, xx_81, R.drawable.xx_81);
	    addPattern(emoticons, xx_82, R.drawable.xx_82);
	    addPattern(emoticons, xx_83, R.drawable.xx_83);
	    addPattern(emoticons, xx_84, R.drawable.xx_84);
	    addPattern(emoticons, xx_85, R.drawable.xx_85);
	    addPattern(emoticons, xx_86, R.drawable.xx_86);
	    addPattern(emoticons, xx_87, R.drawable.xx_87);
	    addPattern(emoticons, xx_88, R.drawable.xx_88);
	    addPattern(emoticons, xx_89, R.drawable.xx_89);
	    addPattern(emoticons, xx_90, R.drawable.xx_90);
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
	        int resource) {
	    map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                spannable.setSpan(new ImageSpan(context, entry.getValue()),
	                        matcher.start(), matcher.end(),
	                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	            }
	        }
	    }
	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}
	
	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	
	
}
