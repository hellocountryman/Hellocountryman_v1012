package com.feytuo.laoxianghao.util;

import java.io.File;

public class CleanCache {

	public static void cleanImageCache(){
		File dir = new File(SDcardTools.getSDPath()+"/laoxianghaoAudio");
		if(dir.exists() && dir.isDirectory()){
			File[] files= dir.listFiles();
			for(File temp : files){
				temp.delete();
			}
		}
	}
	public static void cleanXX_imageCache(){
		File dir = new File(SDcardTools.getSDPath()+"/xx_ImgCach");
		if(dir.exists() && dir.isDirectory()){
			File[] files= dir.listFiles();
			for(File temp : files){
				temp.delete();
			}
		}
	}
}
