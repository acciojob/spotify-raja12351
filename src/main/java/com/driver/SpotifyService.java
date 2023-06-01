package com.driver;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    //Auto-wire will not work in this case, no need to change this and add autowire

    SpotifyRepository spotifyRepository = new SpotifyRepository();

    public static User createUser(String name, String mobile){
        return SpotifyRepository.createUser(name, mobile);
    }

    public static Artist createArtist(String name) {
        return SpotifyRepository.createArtist(name);
    }

    public static Album createAlbum(String title, String artistName) {
        Optional<Artist> artistOptional = SpotifyRepository.findArtistByName(artistName);
        if(artistOptional.isEmpty()){
            SpotifyRepository.createArtist(artistName);
        }
        return SpotifyRepository.createAlbum(title, artistName);
    }

    public static Song createSong(String title, String albumName, int length) throws Exception {
        Optional<Album> albumOptional = SpotifyRepository.findAlbumByName(albumName);
        if(albumOptional.isEmpty()){
            throw new RuntimeException("Album does not exist");
        }
        return SpotifyRepository.createSong(title, albumName, length);
    }

    public static Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Optional<User> userOptional = SpotifyRepository.findUserByMobile(mobile);
        if(userOptional.isEmpty()){
            throw new RuntimeException("User does not exist");
        }
        return SpotifyRepository.createPlaylistOnLength(mobile, title, length);
    }

    public static Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Optional<User> userOptional = SpotifyRepository.findUserByMobile(mobile);
        if(userOptional.isEmpty()){
            throw new RuntimeException("User does not exist");
        }
        return SpotifyRepository.createPlaylistOnName(mobile, title, songTitles);
    }

    public static Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Optional<User> userOptional = SpotifyRepository.findUserByMobile(mobile);
        Optional<Playlist> playlistOptional = SpotifyRepository.findPlayListByTitle(playlistTitle);
        if(userOptional.isEmpty()){
            throw new RuntimeException("User does not exist");
        }
        if(playlistOptional.isEmpty()){
            throw new RuntimeException("Playlist does not exist");
        }
        ArrayList<User> users = SpotifyRepository.usersForPlayList(playlistOptional.get());
        if(users.contains(userOptional.get())){
            return playlistOptional.get();
        }
        else{
            SpotifyRepository.updatePlayList(userOptional,playlistOptional);
        }
        return playlistOptional.get();
    }

    public static Song likeSong(String mobile, String songTitle) throws Exception {
        Optional<User> userOptional = SpotifyRepository.findUserByMobile(mobile);
        Optional<Song> songOptional = SpotifyRepository.findSongByTitle(songTitle);
        if(userOptional.isEmpty()){
            throw new Exception("User does not exist");
        }
        if(songOptional.isEmpty()){
            throw new Exception("Song does not exist");
        }
        return SpotifyRepository.likeSong(mobile, songTitle);
    }

    public static String mostPopularArtist() {
        return SpotifyRepository.mostPopularArtist();
    }

    public static String mostPopularSong() {
        return SpotifyRepository.mostPopularSong();
    }
}
