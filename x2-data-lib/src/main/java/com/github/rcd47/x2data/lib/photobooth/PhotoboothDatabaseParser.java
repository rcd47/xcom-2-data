package com.github.rcd47.x2data.lib.photobooth;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.github.rcd47.x2data.lib.unreal.UnrealUtils;

public class PhotoboothDatabaseParser {
	
	public Map<Integer, CampaignPhotoData> parseFile(ByteBuffer buffer) {
		Map<Integer, CampaignPhotoData> campaigns = new HashMap<>();
		
		// don't know what the first 12 bytes are
		buffer.order(ByteOrder.LITTLE_ENDIAN).position(12);
		
		int campaignCount = buffer.getInt();
		while (--campaignCount >= 0) {
			CampaignPhotoData campaignData = new CampaignPhotoData();
			campaigns.put(buffer.getInt(), campaignData);
			campaignData.Posters = parsePhotoList(buffer);
			campaignData.HeadShots = parsePhotoList(buffer);
		}
		
		return campaigns;
	}
	
	private List<PhotoData> parsePhotoList(ByteBuffer buffer) {
		int photosCount = buffer.getInt();
		List<PhotoData> photos = new ArrayList<>(photosCount);
		
		while (--photosCount >= 0) {
			PhotoData photoData = new PhotoData();
			photos.add(photoData);
			
			int charactersCount = buffer.getInt();
			photoData.CharacterIDs = new HashSet<>();
			while (--charactersCount >= 0) {
				photoData.CharacterIDs.add(buffer.getInt());
			}
			
			photoData.PhotoFilename = UnrealUtils.readString(buffer);
			
			// skip time field
			buffer.position(buffer.position() + 4);
			
			photoData.Favorite = buffer.getInt() != 0;
			
			photoData.TextureSizeX = buffer.getInt();
			
			photoData.TextureSizeY = buffer.getInt();
			
			photoData.PhotoType = EPhotoDataType.values()[buffer.get()];
			
			// skip 14 strings
			for (int i = 0; i < 14; i++) {
				UnrealUtils.readString(buffer);
			}
			
			// skip 9 ints
			buffer.position(buffer.position() + 36);
		}
		
		return photos;
	}
	
}
