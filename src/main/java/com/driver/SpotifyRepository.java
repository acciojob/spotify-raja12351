package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public static HashMap<Artist, List<Album>> artistAlbumMap;
    public static HashMap<Album, List<Song>> albumSongMap;
    public static HashMap<Playlist, List<Song>> playlistSongMap;
    public static HashMap<Playlist, List<User>> playlistListenerMap;
    public static HashMap<User, Playlist> creatorPlaylistMap;
    public static HashMap<User, List<Playlist>> userPlaylistMap;
    public static HashMap<Song, List<User>> songLikeMap;

    public static List<User> users;
    public static List<Song> songs;
    public static List<Playlist> playlists;
    public static List<Album> albums;
    public static List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public static User createUser(String name, String mobile) {
        User newUser = new User(name,mobile);
        users.add(newUser);
        userPlaylistMap.put(newUser, new ArrayList<>());
        return newUser;
    }

    public static Artist createArtist(String name) {
        Artist newArtist = new Artist(name);
        artists.add(newArtist);
        artistAlbumMap.put(newArtist,new ArrayList<>());
        return newArtist;
    }

    public static Album createAlbum(String title, String artistName) {
        Album newAlbum = new Album(title);
        albums.add(newAlbum);
        albumSongMap.put(newAlbum,new ArrayList<>());
        Optional<Artist> artist = findArtistByName(artistName);
        List<Album> albums = artistAlbumMap.get(artist.get());
        albums.add(newAlbum);
        artistAlbumMap.put(artist.get(),albums);
        return newAlbum;
    }

    public static Song createSong(String title, String albumName, int length) throws Exception{
        Song newSong = new Song(title, length);
        songs.add(newSong);
        songLikeMap.put(newSong, new ArrayList<>());
        Optional<Album> albumOptional = findAlbumByName(albumName);
        List<Song> songs = albumSongMap.get(albumOptional.get());
        songs.add(newSong);
        albumSongMap.put(albumOptional.get(), songs);
        return newSong;
    }

    public static Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist newPlayList = new Playlist(title);
        playlists.add(newPlayList);
        playlistSongMap.put(newPlayList, new ArrayList<>());
        playlistListenerMap.put(newPlayList,new ArrayList<>());
        Optional<User> userOptional = findUserByMobile(mobile);
        creatorPlaylistMap.put(userOptional.get(), newPlayList);
        for(Song song : songs){
            if(song.getLength()==length){
                playlistSongMap.get(newPlayList).add(song);
            }
        }
        userPlaylistMap.get(userOptional.get()).add(newPlayList);
        playlistListenerMap.get(newPlayList).add(userOptional.get());
        return newPlayList;
    }

    public static Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist newPlayList = new Playlist(title);
        playlists.add(newPlayList);
        playlistSongMap.put(newPlayList, new ArrayList<>());
        playlistListenerMap.put(newPlayList,new ArrayList<>());
        Optional<User> userOptional = findUserByMobile(mobile);
        creatorPlaylistMap.put(userOptional.get(), newPlayList);
        for(String songName : songTitles){
            Optional<Song> song = findSongByName(songName);
            if(song.isPresent()){
                playlistSongMap.get(newPlayList).add(song.get());
            }
        }
        userPlaylistMap.get(userOptional.get()).add(newPlayList);
        playlistListenerMap.get(newPlayList).add(userOptional.get());
        return newPlayList;
    }

    private static Optional<Song> findSongByName(String songName) {
        for(Song song : songs){
            if(song.getTitle().equals(songName)){
                return Optional.of(song);
            }
        }
        return Optional.empty();
    }

//    public static Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
//
//    }

    public static Song likeSong(String mobile, String songTitle) throws Exception {
        Optional<User> userOptional = findUserByMobile(mobile);
        Optional<Song> songOptional = findSongByTitle(songTitle);
        if(!songLikeMap.get(songOptional.get()).contains(userOptional.get())){
            songLikeMap.get(songOptional.get()).add(userOptional.get());
            songOptional.get().setLikes(songOptional.get().getLikes()+1);
        }
        Artist artist = artistForSong(songOptional.get());
        assert artist != null;
        artist.setLikes(artist.getLikes()+1);
        return songOptional.get();
    }

    private static Artist artistForSong(Song song) {
        //albumSongMap
        Album albumInsearch = null;
        for(Album album : albumSongMap.keySet()){
            if(albumSongMap.get(album).contains(song)){
                albumInsearch = album;
            }
        }
        for(Artist artist : artistAlbumMap.keySet()){
            if(artistAlbumMap.get(artist).contains(albumInsearch)){
                return artist;
            }
        }
        return null;
    }

    public static String mostPopularArtist() {
        int max = 0;
        String artist = "";
        for(Artist artist1 : artists){
            if(artist1.getLikes()>max){
                max = artist1.getLikes();
                artist = artist1.getName();
            }
        }
        return artist;
    }

    public static String mostPopularSong() {
        int max = 0;
        String Song = "";
        for(Song song1 : songs){
            if(song1.getLikes()>max){
                max = song1.getLikes();
                Song = song1.getTitle();
            }
        }
        return Song;

    }

    public static Optional<Artist> findArtistByName(String artistName) {
        for(Artist artist : artists){
            if(artist.getName().equals(artistName)){
                return Optional.of(artist);
            }
        }
        return Optional.empty();
    }

    public static Optional<Album> findAlbumByName(String albumName) {
        for(Album album : albums){
            if(album.getTitle().equals(albumName)){
                return Optional.of(album);
            }
        }
        return Optional.empty();
    }

    public static Optional<User> findUserByMobile(String mobile) {
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public static Optional<Playlist> findPlayListByTitle(String playlistTitle) {
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(playlistTitle)){
                return Optional.of(playlist);
            }
        }
        return Optional.empty();
    }

    public static ArrayList<User> usersForPlayList(Playlist playlist) {
        return new ArrayList<>(playlistListenerMap.get(playlist));
    }

    public static void updatePlayList(Optional<User> userOptional, Optional<Playlist> playlistOptional) {
        playlistListenerMap.get(playlistOptional.get()).add(userOptional.get());
        userPlaylistMap.get(userOptional.get()).add(playlistOptional.get());
        creatorPlaylistMap.put(userOptional.get(), playlistOptional.get());
    }

    public static Optional<Song> findSongByTitle(String songTitle) {
        for(Song song : songs){
            if(song.getTitle().equals(songTitle)){
                return Optional.of(song);
            }
        }
        return Optional.empty();
    }
}
