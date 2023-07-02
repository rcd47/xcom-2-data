package com.github.rcd47.x2data.lib.photobooth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.rcd47.x2data.lib.unreal.UnrealObjectParserTest;

public class PhotoboothDatabaseParserTest {
	
	@Test
	public void testParse() throws Exception {
		Map<Integer, CampaignPhotoData> campaigns =
				new PhotoboothDatabaseParser().parseFile(UnrealObjectParserTest.loadFile("/photobooth/PhotoboothData.x2"));
		
		assertThat(campaigns).hasSize(59);
		
		assertThat(campaigns.get(1).Posters.get(0).CharacterIDs).containsExactlyInAnyOrder(421, 427, 409, 415);
		assertThat(campaigns.get(1).Posters.get(0).Favorite).isFalse();
		assertThat(campaigns.get(1).Posters.get(0).PhotoFilename).isEqualTo("..\\..\\XComGame\\Photobooth\\Campaign_1\\UserPhotos\\Poster_000.png");
		assertThat(campaigns.get(1).Posters.get(0).PhotoType).isEqualTo(EPhotoDataType.ePDT_User);
		assertThat(campaigns.get(1).Posters.get(0).TextureSizeX).isEqualTo(720);
		assertThat(campaigns.get(1).Posters.get(0).TextureSizeY).isEqualTo(1080);
		
		assertThat(campaigns.get(1).Posters.get(2).CharacterIDs).containsExactlyInAnyOrder(4945);
		assertThat(campaigns.get(1).Posters.get(2).Favorite).isFalse();
		assertThat(campaigns.get(1).Posters.get(2).PhotoFilename).isEqualTo("..\\..\\XComGame\\Photobooth\\Campaign_1\\UserPhotos\\Poster_002.png");
		assertThat(campaigns.get(1).Posters.get(2).PhotoType).isEqualTo(EPhotoDataType.ePDT_Captured);
		assertThat(campaigns.get(1).Posters.get(2).TextureSizeX).isEqualTo(720);
		assertThat(campaigns.get(1).Posters.get(2).TextureSizeY).isEqualTo(1080);
		
		assertThat(campaigns.get(64).HeadShots.get(12).CharacterIDs).containsExactlyInAnyOrder(484);
		assertThat(campaigns.get(64).HeadShots.get(12).Favorite).isFalse();
		assertThat(campaigns.get(64).HeadShots.get(12).PhotoFilename).isEqualTo("..\\..\\XComGame\\Photobooth\\Campaign_64\\HeadShots\\Headshot_012.png");
		assertThat(campaigns.get(64).HeadShots.get(12).PhotoType).isEqualTo(EPhotoDataType.ePDT_HeadShot);
		assertThat(campaigns.get(64).HeadShots.get(12).TextureSizeX).isEqualTo(512);
		assertThat(campaigns.get(64).HeadShots.get(12).TextureSizeY).isEqualTo(512);
	}
	
}
